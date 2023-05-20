package com.jwt.freecloud.service.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.file.FileNameUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jwt.freecloud.common.constants.FileConstants;
import com.jwt.freecloud.common.constants.RequestExceptionEnum;
import com.jwt.freecloud.common.constants.ResponseMessageEnum;
import com.jwt.freecloud.common.dto.ListFilesDTO;
import com.jwt.freecloud.common.entity.FileUser;
import com.jwt.freecloud.common.vo.FileUserVO;
import com.jwt.freecloud.common.vo.UserVO;
import com.jwt.freecloud.dao.FileUserMapper;
import com.jwt.freecloud.interceptor.LoginInterceptor;
import com.jwt.freecloud.service.*;
import com.jwt.freecloud.util.CheckUtil;
import com.jwt.freecloud.util.FileUtil;
import com.jwt.freecloud.util.FreeResult;
import com.jwt.freecloud.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 用户文件表（页面展示） 服务实现类
 *
 * @author jwt
 * @since 2023-03-02
 */
@Service
public class FileUserServiceImpl extends ServiceImpl<FileUserMapper, FileUser> implements FileUserService {

    @Autowired
    FileRecycleService fileRecycleService;

    @Autowired
    FileUserOriginService fileUserOriginService;

    @Autowired
    ThreadPoolExecutor executor;

    @Autowired
    UserService userService;

    @Autowired
    SearchService searchService;

    @Autowired
    RedisUtil redisUtil;


    /**
     * 从回收站中还原文件
     *
     * @param fileIds
     */
    @Override
    public void recoverFiles(List<Long> fileIds) {
        List<FileUser> fileUsers = this.listByIds(fileIds);
        for (FileUser fileUser : fileUsers) {
            fileUser.setStatus(FileConstants.FILE_NORMAL);
        }
        this.updateBatchById(fileUsers);
        try {
            searchService.updateEsByFileUsers(fileUsers);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 单个文件夹复制，内容存到redis
     *
     * @param directoryId
     * @param method
     * @return
     */
    @Override
    public FreeResult copyDirectory(Long directoryId, String method) {
        UserVO userVO = LoginInterceptor.loginUser.get();
        Integer userId = userVO.getUserId();
        String dirId = String.valueOf(directoryId);
        String content = method + "_" + dirId;
        redisUtil.setStringWithExpire(FileConstants.FILE_COPY_PREFIX + userId, content, FileConstants.FILE_COPY_EXPIRE, TimeUnit.SECONDS);
        return FreeResult.success();
    }

    /**
     * 移动或粘贴文件夹
     *
     * @param newPath
     * @return
     */
    @Override
    public FreeResult pasteDirectory(String newPath) throws IOException {
        UserVO userVO = LoginInterceptor.loginUser.get();
        // 获取要操作的文件id
        String content = redisUtil.getString(FileConstants.FILE_COPY_PREFIX + LoginInterceptor.loginUser.get().getUserId());
        if (content == null) {
            return FreeResult.fail(ResponseMessageEnum.FILE_PASTE_EMPTY);
        }
        String[] s = content.split("_");
        String method = s[0];//判断是剪切还是复制粘贴
        Long dirId = Long.parseLong(s[1]);

        if (FileConstants.FILE_MOVE.equals(method)) {
            // 剪切操作
            return moveDirectory(newPath, dirId);
        }

        // 根文件夹需要改变parentId，子文件或文件夹改变路径即可，而parentId保持原样
        FileUser directory = this.getById(dirId);
        Long parentId = getParentIdByPath(newPath,userVO.getUserId());
        directory.setParentId(parentId);
        directory.setFilePath(newPath + directory.getFileName());
        List<FileUser> fileList = new ArrayList<>();
        // 把根文件夹加入list
        baseMapper.insert(directory);
        searchService.updateEsByFileUser(directory);

        Long totalSize = getAllFilesByParentIdWithPaste(dirId, directory.getFileId(), directory.getFilePath() +  "/", fileList);

        //用户容量不足
        if (!CheckUtil.checkMemoryEnough(userVO, totalSize)) {
            return FreeResult.fail(ResponseMessageEnum.SPACE_NOT_ENOUGH);
        }

        // 粘贴操作
        this.saveBatch(fileList);
        // 更新es
        //searchService.updateEsByFileUsers(fileList);

        // 更新用户已用容量
        Long newUsed = userVO.getUsedMemory() + totalSize;
        userService.updateUsedMemory(userVO, newUsed);

        return FreeResult.success(ResponseMessageEnum.FILE_PASTE_SUCCESS);
    }

    public FreeResult moveDirectory(String newPath, Long dirId) throws IOException {
        UserVO userVO = LoginInterceptor.loginUser.get();
        // 根文件夹需要改变parentId，子文件或文件夹改变路径即可，而parentId保持原样
        FileUser directory = this.getById(dirId);
        Long parentId = getParentIdByPath(newPath,userVO.getUserId());
        directory.setParentId(parentId);
        directory.setFilePath(newPath + directory.getFileName());
        List<FileUser> fileList = new ArrayList<>();
        fileList.add(directory);

        Long totalSize = getAllFilesByParentIdWithMove(directory.getFileId(), directory.getFilePath() +  "/", fileList);

        //用户容量不足
        if (!CheckUtil.checkMemoryEnough(userVO, totalSize)) {
            return FreeResult.fail(ResponseMessageEnum.SPACE_NOT_ENOUGH);
        }

        this.updateBatchById(fileList);

        // 更新es
        searchService.updateEsByFileUsers(fileList);

        // 更新用户已用容量
        Long newUsed = userVO.getUsedMemory() + totalSize;
        userService.updateUsedMemory(userVO, newUsed);

        return FreeResult.success(ResponseMessageEnum.FILE_PASTE_SUCCESS);
    }

    /**
     * 递归获取文件夹内容,把文件加入到文件id集合（剪切或粘贴操作使用）
     *
     * @param parentId
     * @param newPath
     * @param fileList
     */
    public Long getAllFilesByParentIdWithPaste(Long findId, Long parentId, String newPath, List<FileUser> fileList) {
        QueryWrapper<FileUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id", findId);
        List<FileUser> list = this.list(queryWrapper);
        Long totalSize = 0L;
        for (FileUser fileUser : list) {
            fileUser.setFilePath(newPath + fileUser.getFileName());
            fileUser.setParentId(parentId);

            if (fileUser.getDirFlag() == FileConstants.DIRECTORY_FLAG) {
                Long oldId = fileUser.getFileId();
                this.save(fileUser);
                searchService.updateEsByFileUser(fileUser);
                totalSize += getAllFilesByParentIdWithPaste(oldId, fileUser.getFileId(), fileUser.getFilePath() + "/", fileList);
            } else {
                fileList.add(fileUser);
                totalSize += fileUser.getFileSize();
            }

        }
        return totalSize;
    }

    /**
     * 递归获取文件夹内容,把文件加入到文件id集合（剪切或粘贴操作使用）
     *
     * @param parentId
     * @param newPath
     * @param fileList
     */
    public Long getAllFilesByParentIdWithMove(Long parentId, String newPath, List<FileUser> fileList) {
        QueryWrapper<FileUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id", parentId);
        List<FileUser> list = this.list(queryWrapper);
        Long totalSize = 0L;
        for (FileUser fileUser : list) {

            if (fileUser.getDirFlag() == FileConstants.DIRECTORY_FLAG) {
                fileUser.setFilePath(newPath + fileUser.getFileName());
                totalSize += getAllFilesByParentIdWithMove(fileUser.getFileId(), fileUser.getFilePath() + "/", fileList);
            } else {
                fileUser.setFilePath(newPath + fileUser.getFileName() + "." + fileUser.getFileExtName());

                totalSize += fileUser.getFileSize();
            }
            fileList.add(fileUser);
        }
        return totalSize;
    }

    /**
     * 把复制内容存到redis中
     *
     * @param fileIds
     * @param method
     * @return
     */
    @Override
    public FreeResult copyFiles(List<Long> fileIds, String method) {
        UserVO userVO = LoginInterceptor.loginUser.get();
        Integer userId = userVO.getUserId();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < fileIds.size(); i++) {
            if(i != fileIds.size() - 1) {
                sb.append(fileIds.get(i)).append(',');
            } else {
                sb.append(fileIds.get(i));
            }
        }
        String content = method + "_" + sb.toString();
        redisUtil.setStringWithExpire(FileConstants.FILE_COPY_PREFIX + userId, content, FileConstants.FILE_COPY_EXPIRE, TimeUnit.SECONDS);
        return FreeResult.success();
    }

    /**
     * 移动或粘贴文件
     *
     * @param newPath
     * @return
     */
    @Transactional
    @Override
    public FreeResult pasteFiles(String newPath) throws IOException {
        UserVO userVO = LoginInterceptor.loginUser.get();
        // 获取要操作的文件id
        String content = redisUtil.getString(FileConstants.FILE_COPY_PREFIX + LoginInterceptor.loginUser.get().getUserId());
        if (content == null) {
            return FreeResult.fail(ResponseMessageEnum.FILE_PASTE_EMPTY);
        }
        String[] s = content.split("_");
        String method = s[0];//判断是剪切还是复制粘贴
        String[] fileIdstr = s[1].split(",");
        List<Long> fileIds = new ArrayList<>();
        for (String s1 : fileIdstr) {
            fileIds.add(Long.parseLong(s1));
        }
        List<FileUser> fileUsers = getNormalFilesByListIds(fileIds);
        Long totalSize = 0L;
        Long parentId = getParentIdByPath(newPath,userVO.getUserId());
        for (FileUser fileUser : fileUsers) {
            fileUser.setFilePath(newPath + fileUser.getFileName() + "." + fileUser.getFileExtName());
            fileUser.setParentId(parentId);
            totalSize += fileUser.getFileSize();
        }

        //用户容量不足
        if (!CheckUtil.checkMemoryEnough(userVO, totalSize)) {
            return FreeResult.fail(ResponseMessageEnum.SPACE_NOT_ENOUGH);
        }

        // 剪切操作
        if (FileConstants.FILE_MOVE.equals(method)) {
            //TODO case when优化
            this.updateBatchById(fileUsers);
            // 更新es
            searchService.updateEsByFileUsers(fileUsers);
            return FreeResult.success(ResponseMessageEnum.FILE_MOVE_SUCCESS);
        }

        // 粘贴操作
        this.saveBatch(fileUsers);
        searchService.updateEsByFileUsers(fileUsers);
        // 更新用户已用容量
        Long newUsed = userVO.getUsedMemory() + totalSize;

        userService.updateUsedMemory(userVO, newUsed);

        return FreeResult.success(ResponseMessageEnum.FILE_PASTE_SUCCESS);
    }

    /**
     * 筛选出正常的文件，防止复制粘贴中途某些文件被删除
     *
     * @param fileIds
     * @return
     */
    public List<FileUser> getNormalFilesByListIds(List<Long> fileIds) {
        QueryWrapper<FileUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", FileConstants.FILE_NORMAL);
        queryWrapper.in("file_id", fileIds);
        List<FileUser> fileUsers = baseMapper.selectList(queryWrapper);
        return fileUsers;
    }

    /**
     * 文件重命名
     *
     * @param fileUserVO
     * @return
     */
    @Override
    public FreeResult renameFile(FileUserVO fileUserVO) throws IOException {

        QueryWrapper<FileUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id", fileUserVO.getParentId());
        List<FileUser> list = this.list(queryWrapper);
        Set<String> set = list.stream().map(FileUser::getFileName).collect(Collectors.toSet());

        String newFileName = fileUserVO.getFileName();
        // 当前文件夹下如果有同名文件，返回失败
        if (set.contains(newFileName)) {
            return FreeResult.fail(ResponseMessageEnum.FILE_RENAME_ERROR);
        }

        FileUser file = this.getById(fileUserVO.getFileId());
        file.setFileName(newFileName);
        String fileAllPath = file.getFilePath();
        String name = FileNameUtil.getName(fileAllPath);
        String oldPath = fileAllPath.substring(0, fileAllPath.lastIndexOf(name));
        if (file.getDirFlag() == FileConstants.FILE_FLAG) {
            String newPath = oldPath + newFileName + "." + file.getFileExtName();
            file.setFilePath(newPath);
            file.setUpdateTime(LocalDateTime.now());
            baseMapper.updateById(file);
            //更新es
            searchService.updateEsByFileUser(file);
        }

        if (file.getDirFlag() == FileConstants.DIRECTORY_FLAG) {
            String newPath = oldPath + newFileName;
            List<FileUser> files = new ArrayList<>();
            file.setFilePath(newPath);
            files.add(file);
            getAllFilesByParentIdWithRename(file.getFileId(), newPath + "/", fileAllPath + "/", files);
            this.updateBatchById(files);
            searchService.updateEsByFileUsers(files);
        }


        return FreeResult.success();
    }

    /**
     * 递归获取文件夹内容,把文件加入到文件id集合（剪切或粘贴操作使用）
     *
     * @param parentId
     * @param newPath
     * @param fileList
     */
    public void getAllFilesByParentIdWithRename(Long parentId, String newPath, String oldPath, List<FileUser> fileList) {
        QueryWrapper<FileUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id", parentId);
        List<FileUser> list = this.list(queryWrapper);
        for (FileUser fileUser : list) {
            String oldP = fileUser.getFilePath();
            String newP = fileUser.getFilePath().replace(oldPath, newPath);
            if (fileUser.getDirFlag() == FileConstants.DIRECTORY_FLAG) {
                getAllFilesByParentIdWithRename(fileUser.getFileId(), newP + "/", oldP + "/", fileList);
            }
            // TODO 测试

            fileUser.setFilePath(newP);
            fileList.add(fileUser);


        }
    }

    public Long getParentIdByPath(String filePath, Integer userId) {

        // 根目录
        if ("/".equals(filePath)) {
            return 0L;
        }

        QueryWrapper<FileUser> queryWrapper = new QueryWrapper<>();
        // -1是为了去除最后的/
        String s = filePath.substring(0, filePath.length() - 1);
        queryWrapper.eq("file_path",s).eq("user_id", userId).select("file_id");
        FileUser one = baseMapper.selectOne(queryWrapper);
        return one.getFileId();
    }

    /**
     * 列出当前文件夹下的所有文件(点击文件夹或面包屑导航）
     *
     * @param listFilesDTO
     * @return
     */
    @Override
    public FreeResult listFiles(ListFilesDTO listFilesDTO, Integer method) {

        //当前用户id
        Integer userId = LoginInterceptor.loginUser.get().getUserId();
        QueryWrapper<FileUser> queryWrapper = new QueryWrapper<>();
        //根据parentId/FilePath，userId去查询，并根据指定的排序规则返回结果
        if (method == FileConstants.LIST_BY_PARENTID) {
            queryWrapper.eq("parent_id", listFilesDTO.getParentId());
        } else if (method == FileConstants.LIST_BY_PATH) {
            String filePath = listFilesDTO.getFilePath();
            Long parentIdByPath = getParentIdByPath(filePath, userId);
            queryWrapper.eq("parent_id", parentIdByPath);
        } else {
            return FreeResult.fail(RequestExceptionEnum.BAD_REQUEST);
        }
        queryWrapper.eq("user_id", userId).eq("status", FileConstants.FILE_NORMAL)
                .orderBy(true, !listFilesDTO.getDesc(), FileUtil.getFileOrder(listFilesDTO.getOrder()));
        //分页查询
        Page<FileUser> page = new Page<>(listFilesDTO.getPage(), listFilesDTO.getLimit());
        List<FileUser> lists = baseMapper.selectPage(page, queryWrapper).getRecords();
        if (lists == null || lists.size() == 0) {
            return FreeResult.success();
        }
        //通过stream流封装返回结果
        List<FileUserVO> result = lists.stream().map(fileUser -> {
            FileUserVO fileUserVO = BeanUtil.copyProperties(fileUser, FileUserVO.class);
            fileUserVO.setFilePath(FileUtil.getVoPath(fileUser.getFilePath()));
            return fileUserVO;
        }).collect(Collectors.toList());
        return FreeResult.success().putData("list", result);
    }

    @Transactional
    @Override
    public FreeResult filesToRecycle(List<Long> fileIds) throws IOException {
        List<FileUser> fileUsers = this.listByIds(fileIds);
        fileRecycleService.filesToRecycle(fileUsers);
        for (FileUser fileUser : fileUsers) {
            fileUser.setStatus(FileConstants.FILE_RECYCLE);
        }
        //逻辑删除字段有多个值，只能通过xml更新状态
        this.updateBatchById(fileUsers);

        // 更新es
        searchService.updateEsByFileUsers(fileUsers);

        return FreeResult.success();
    }


    /**
     * 批量删除文件（逻辑删除，文件状态置为已删除）
     *
     * @param fileIds
     */
    @Transactional
    @Override
    public List<FileUser> deleteBatchByIds(List<Long> fileIds) {

        System.out.println("deleteBatchByIds");
        List<FileUser> fileUsers = baseMapper.getListByIds(fileIds);
        if (fileUsers.size() == 0) {
            return fileUsers;
        }

        Integer userId = fileUsers.get(0).getUserId();
        List<FileUser> files = new ArrayList<>();
        List<Long> folders = fileOrFolder(fileUsers, files);

        if (folders.size() != 0) {
            //递归遍历文件夹
            for (Long folder : folders) {
                getAllFilesByFolderWithDelete(folder, files);
            }
        }
        Long totalSize = 0L;
        for (FileUser file : files) {
            totalSize += file.getFileSize();
        }
        this.updateBatchById(files);

        // 更新用户容量
        UserVO userVO = new UserVO();
        userVO.setUserId(userId);
        userService.updateUsedMemory(userVO, totalSize);

        //过滤掉文件夹
        List<FileUser> filterFiles = files.stream().filter(fileUser -> fileUser.getOriginId() != -1).collect(Collectors.toList());


        return filterFiles;
    }


    /**
     * 递归获取文件夹内容,把文件加入到文件id集合（删除操作使用）
     *
     * @param parentId 父文件夹id
     * @param files    文件id集合
     */
    public void getAllFilesByFolderWithDelete(Long parentId, List<FileUser> files) {
        QueryWrapper<FileUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id", parentId).eq("status",FileConstants.FILE_NORMAL);
        queryWrapper.select("file_id", "dir_flag", "origin_id","file_size","user_id");
        List<FileUser> list = this.list(queryWrapper);
        if (list == null || list.size() == 0) {
            return;
        }
        List<Long> folders = fileOrFolder(list, files);
        if (folders.size() != 0) {
            //递归遍历文件夹，
            for (Long folder : folders) {
                getAllFilesByFolderWithDelete(folder, files);
            }
        }
    }

    /**
     * 判断集合元素是文件还是文件夹，并进行分类
     *
     * @param fileUsers
     * @param files
     * @return 返回文件夹id集合
     */
    private List<Long> fileOrFolder(List<FileUser> fileUsers, List<FileUser> files) {
        List<Long> folders = new ArrayList<>();
        for (FileUser fileUser : fileUsers) {
            if (fileUser.getDirFlag() == FileConstants.DIRECTORY_FLAG) {
                folders.add(fileUser.getFileId());
            }
            //Integer fileSize = fileUser.getFileSize() == null ? 0 : fileUser.getFileSize();
            Integer fileSize = fileUser.getFileSize();
            files.add(new FileUser(fileUser.getFileId(), fileUser.getOriginId(), FileConstants.FILE_DELETE, fileSize,fileUser.getDirFlag()));
        }
        return folders;
    }

    /**
     * 新建文件夹
     *
     * @param filePath
     * @param fileName
     * @return
     */
    @Override
    public FreeResult createFolder(String filePath, String fileName, Long parentId) {
        Integer userId = LoginInterceptor.loginUser.get().getUserId();
        FileUser fileUser = createBaseFileUser();
        fileUser.setFileName(fileName);
        fileUser.setUserId(userId);
        fileUser.setDirFlag(FileConstants.DIRECTORY_FLAG);
        fileUser.setFilePath(filePath + fileName);
        fileUser.setParentId(parentId);
        fileUser.setFileSize(0);
        fileUser.setOriginId(FileConstants.DIRECTORY_ORIGINID);
        this.save(fileUser);

        // 更新es
        searchService.updateEsByFileUser(fileUser);

        return FreeResult.success();
    }

    /**
     * 创建fileUser，包含基本内容
     *
     * @return
     */
    public FileUser createBaseFileUser() {
        UserVO userVO = LoginInterceptor.loginUser.get();
        Integer userId = userVO.getUserId();
        LocalDateTime now = LocalDateTime.now();

        FileUser fileUser = new FileUser();
        fileUser.setStatus(FileConstants.FILE_NORMAL);
        fileUser.setUserId(userId);
        fileUser.setCreateTime(now);
        fileUser.setUpdateTime(now);
        return fileUser;
    }
}
