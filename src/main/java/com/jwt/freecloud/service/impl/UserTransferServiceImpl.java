package com.jwt.freecloud.service.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.file.FileNameUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.HashMultimap;
import com.jwt.freecloud.common.constants.FileConstants;
import com.jwt.freecloud.common.constants.RequestExceptionEnum;
import com.jwt.freecloud.common.constants.ResponseMessageEnum;
import com.jwt.freecloud.common.dto.UploadDTO;
import com.jwt.freecloud.common.entity.FileOrigin;
import com.jwt.freecloud.common.entity.FileUser;
import com.jwt.freecloud.common.entity.FileUserOrigin;
import com.jwt.freecloud.common.entity.UserTransfer;
import com.jwt.freecloud.common.vo.UserVO;
import com.jwt.freecloud.dao.UserTransferMapper;
import com.jwt.freecloud.interceptor.LoginInterceptor;
import com.jwt.freecloud.service.*;
import com.jwt.freecloud.util.*;
import io.minio.CreateMultipartUploadResponse;
import io.minio.ListPartsResponse;
import io.minio.StatObjectResponse;
import io.minio.messages.Part;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 
 * 用户传输表 服务实现类
 *
 * @author jwt
 * @since 2023-03-02
 */
@Service
@Slf4j
public class UserTransferServiceImpl extends ServiceImpl<UserTransferMapper, UserTransfer> implements UserTransferService {


    @Autowired
    MinioTemplate minioTemplate;

    @Autowired
    RedisUtil redisUtil;

    @Autowired
    FileUserService fileUserService;

    @Autowired
    FileOriginService fileOriginService;
    
    @Autowired
    FileUserOriginService fileUserOriginService;

    @Autowired
    SearchService searchService;

    @Autowired
    UserService userService;
    
    @Autowired
    UserLevelService userLevelService;

    @Autowired
    ThreadPoolExecutor executor;



    @Override
    public FreeResult createFileUpload(UploadDTO uploadDTO) {
        UserVO userVO = LoginInterceptor.loginUser.get();
        if (!CheckUtil.checkMemoryEnough(userVO, Long.valueOf(uploadDTO.getFileSize()))) {
            //用户容量不足
            return FreeResult.fail(ResponseMessageEnum.SPACE_NOT_ENOUGH);
        }
        if (fileSecondUpload(uploadDTO)) {
            //文件秒传成功
            return FreeResult.success(ResponseMessageEnum.FILE_SECOND_UPLOAD_SUCCESS).putData("skipUpload", true);
        }

        return createMultipartUpload(uploadDTO);
    }


    /**
     * 文件秒传
     * @param uploadDTO
     * @return
     */
    @Transactional
    public Boolean fileSecondUpload(UploadDTO uploadDTO) {

        UserVO userVO = LoginInterceptor.loginUser.get();
        Integer userId = userVO.getUserId();

        String originId = redisUtil.getString(FileConstants.FILE_FAST_UPLOAD_PREFIX + uploadDTO.getFileIdentify());
        if (originId == null) {
            return false;
        }
        // mybatisplus的查询已经自动带上逻辑删除字段，获取fileOrigin是为了判断源文件是否已被删除
        // 只有未被删除的源文件才可以被使用
        FileOrigin fileOrigin = fileOriginService.getById(Long.parseLong(originId));
        if (fileOrigin == null) {
            return false;
        }

        //根据文件路径获取parentId
        String filePath = uploadDTO.getFilePath();
        Long parentId = fileUserService.getParentIdByPath(filePath, userId);
        
        LocalDateTime now = LocalDateTime.now();
        //TODO 简化fileuser创建
        FileUser fileUser = new FileUser();
        String name = uploadDTO.getFileName().substring(0, uploadDTO.getFileName().lastIndexOf('.'));
        fileUser.setFileName(name);
        fileUser.setFileSize(fileOrigin.getFileSize());
        fileUser.setFileExtName(fileOrigin.getFileExtName());
        fileUser.setStatus(FileConstants.FILE_NORMAL);
        fileUser.setFilePath(filePath + uploadDTO.getFileName());
        fileUser.setOriginId(Long.parseLong(originId));
        fileUser.setUserId(userId);
        fileUser.setParentId(parentId);
        fileUser.setCreateTime(now);
        fileUser.setUpdateTime(now);
        fileUser.setDirFlag(FileConstants.FILE_FLAG);
        fileUserService.save(fileUser);

        //插入用户文件源文件关联数据
        FileUserOrigin fileUserOrigin = new FileUserOrigin();
        fileUserOrigin.setOriginId(Long.parseLong(originId));
        fileUserOrigin.setFileId(fileUser.getFileId());
        fileUserOriginService.save(fileUserOrigin);

        //更新用户容量
        Long usedMemory = userVO.getUsedMemory() + uploadDTO.getFileSize();

        userService.updateUsedMemory(userVO, usedMemory);

        // 更新redis过期时间
        redisUtil.setExpire(FileConstants.FILE_FAST_UPLOAD_PREFIX + uploadDTO.getFileIdentify(),FileConstants.FILE_SECEND_IDENTIFY_EXPIRE, TimeUnit.DAYS);

        // 更新es
        searchService.updateEsByFileUser(fileUser);

        return true;
    }

    /**
     * 创建分片上传任务
     * @param uploadDTO
     * @return
     */
    @SneakyThrows
    public FreeResult createMultipartUpload(UploadDTO uploadDTO) {

        // 获取用户所在存储桶
        UserVO userVO = LoginInterceptor.loginUser.get();
        String bucketName = minioTemplate.getBucket(userVO);

        String extName = FileNameUtil.extName(uploadDTO.getFileName());
        String minioFileName = userVO.getUserId() + "/" +uploadDTO.getFileIdentify() + "." + extName;
        String fileName = uploadDTO.getFileName().replace("."+extName, "");
        // 更新文件名（截取后缀）
        uploadDTO.setFileExtName(extName);
        uploadDTO.setFileName(fileName);
        Integer totalChunks = uploadDTO.getTotalChunks();

        // 1. 根据文件名创建签名
        Map<String, Object> result = new HashMap<>();
        // 2. 创建分片任务，并返回uploadId
        String contentType = FileConstants.FILE_UPLOAD_CONTENT_TYPE;
        HashMultimap<String, String> headers = HashMultimap.create();
        headers.put("Content-Type", contentType);
        CreateMultipartUploadResponse response = minioTemplate.createMultipartUpload(bucketName, null, minioFileName, headers, null);
        String uploadId = response.result().uploadId();
        result.put("uploadId", uploadId);

        // 把文件信息（如名称等）临时放到redis中, 文件上传完成时需要拿到数据插入到数据库中
        BoundHashOperations<String, Object, Object> hash = redisUtil.getHash(FileConstants.FILE_UPLOAD_INFO_PREFIX + userVO.getUserId());
        hash.put(uploadId, JSON.toJSONString(uploadDTO));

        // 3. 请求Minio 服务，获取每个分块带签名的上传URL
        Map<String, String> reqParams = new HashMap<>();
        reqParams.put("uploadId", uploadId);

        // 4. 循环分块数 从1开始,MinIO 存储服务定义分片索引却是从1开始的
        for (int i = 1; i <= totalChunks; i++) {
            reqParams.put("partNumber", String.valueOf(i));
            String uploadUrl = minioTemplate.getPresignedObjectUrl(bucketName, minioFileName, reqParams);// 获取URL,主要这里前端上传的时候，要传递二进制流，而不是file
            result.put("chunk_" + (i - 1), uploadUrl); // 添加到集合
        }


        return FreeResult.success(ResponseMessageEnum.FILE_UPLOAD_CREATED_SUCCESS).setResultMap(result);
    }

    /**
     * 上传所有分片完成，合并文件
     * @param uploadId
     * @return
     */
    @Transactional
    public FreeResult completeMultipartUpload(String uploadId) {

        // 获取用户所在存储桶
        UserVO userVO = LoginInterceptor.loginUser.get();
        Integer userId = userVO.getUserId();
        String bucketName = minioTemplate.getBucket(userVO);

        //从redis中获取文件信息
        BoundHashOperations<String, Object, Object> hash = redisUtil.getHash(FileConstants.FILE_UPLOAD_INFO_PREFIX + userVO.getUserId());
        UploadDTO uploadDTO = JSON.parseObject((String) hash.get(uploadId), new TypeReference<UploadDTO>() {});

        String minioFileName = userVO.getUserId() + "/" +uploadDTO.getFileIdentify() + "." + uploadDTO.getFileExtName();

        try {
            ListPartsResponse listPartsResponse = minioTemplate.listMultipart(bucketName, minioFileName, FileConstants.MAX_TOTAL_CHUNKS, uploadId);

            int partNumber = 1; // 分片序列从1开始

            List<Part> partList = listPartsResponse.result().partList();
            Part[] parts = new Part[partList.size()];
            for (Part part : partList) {
                // 将分片标记传递给Part对象
                parts[partNumber - 1] = new Part(partNumber, part.etag());
                partNumber++;
            }
            minioTemplate.completeMultipartUpload(bucketName, minioFileName, uploadId, parts);
        } catch (Exception e) {
            e.printStackTrace();
            return FreeResult.fail(ResponseMessageEnum.FILE_UPLOAD_FAIL);
        }

        //插入源文件记录
        LocalDateTime now = LocalDateTime.now();
        Integer fileType = FileUtil.getFileType(uploadDTO.getFileExtName());

        FileOrigin fileOrigin = new FileOrigin();
        BeanUtil.copyProperties(uploadDTO, fileOrigin);
        fileOrigin.setStatus(FileConstants.FILE_NORMAL);
        fileOrigin.setCreateUserId(userId);
        fileOrigin.setCreateTime(now);
        fileOrigin.setUpdateTime(now);
        fileOrigin.setFileType(fileType);
        fileOriginService.save(fileOrigin);
        
        //插入用户文件记录
        String filePath = uploadDTO.getFilePath();
        Long parentId = fileUserService.getParentIdByPath(filePath, userId);
        
        FileUser fileUser = new FileUser();
        BeanUtil.copyProperties(fileOrigin, fileUser);
        fileUser.setFileSize(uploadDTO.getFileSize());
        fileUser.setFileName(uploadDTO.getFileName());
        fileUser.setUserId(userId);
        fileUser.setFilePath(filePath + uploadDTO.getFileName() + "." + uploadDTO.getFileExtName());
        fileUser.setParentId(parentId);
        fileUser.setDirFlag(FileConstants.FILE_FLAG);
        fileUserService.save(fileUser);

        //插入用户文件源文件关联数据
        FileUserOrigin fileUserOrigin = new FileUserOrigin();
        fileUserOrigin.setOriginId(fileOrigin.getOriginId());
        fileUserOrigin.setFileId(fileUser.getFileId());
        fileUserOriginService.save(fileUserOrigin);
        
        //更新用户容量
        userService.updateUsedMemory(userVO, (long)uploadDTO.getFileSize());

        //更新用户经验等级
        userLevelService.increaseExpByUpload(userId);

        // redis删除上传文件信息
        hash.delete(uploadId);

        // redis存放md5
        redisUtil.setStringWithExpire(FileConstants.FILE_FAST_UPLOAD_PREFIX+fileOrigin.getFileIdentify(), String.valueOf(fileOrigin.getOriginId()),
                FileConstants.FILE_SECEND_IDENTIFY_EXPIRE, TimeUnit.DAYS);

        // 更新es
        searchService.updateEsByFileUser(fileUser);

        return FreeResult.success(ResponseMessageEnum.FILE_UPLOAD_SUCCESS);
    }

    /**
     * 下载文件
     * @param fileId
     * @return
     */
    @Override
    public FreeResult downloadFile(Integer fileId, HttpServletResponse response) {
        UserVO userVO = LoginInterceptor.loginUser.get();
        FileUser fileUser = fileUserService.getById(fileId);
        if (!userVO.getUserId().equals(fileUser.getUserId())) {
            return FreeResult.fail(RequestExceptionEnum.BAD_REQUEST);
        }
        FileOrigin fileOrigin = fileOriginService.getById(fileUser.getOriginId());
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String bucket = dtf.format(userVO.getRegTime());
        // 拼装minio对象路径
        String minioObjectName = fileOrigin.getCreateUserId() + "/" + fileOrigin.getFileIdentify() + "." + fileOrigin.getFileExtName();
        StatObjectResponse stat = minioTemplate.statObject(bucket, minioObjectName);
        if (stat == null) {
            return FreeResult.fail(ResponseMessageEnum.FILE_NOT_EXIST);
        }
        InputStream is = minioTemplate.getObject(bucket, minioObjectName);
        try {
            IOUtils.copy(is, response.getOutputStream());
        } catch (IOException e) {
            log.error("下载失败：" + e );
            return FreeResult.fail(ResponseMessageEnum.FILE_DOWNLOAD_ERROR);
        }
        return FreeResult.success();
    }
}
