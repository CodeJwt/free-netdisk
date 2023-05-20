package com.jwt.freecloud.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jwt.freecloud.common.constants.FileConstants;
import com.jwt.freecloud.common.entity.FileOrigin;
import com.jwt.freecloud.common.entity.FileUserOrigin;
import com.jwt.freecloud.common.entity.User;
import com.jwt.freecloud.dao.FileOriginMapper;
import com.jwt.freecloud.dao.FileUserOriginMapper;
import com.jwt.freecloud.dao.UserMapper;
import com.jwt.freecloud.service.FileOriginService;
import com.jwt.freecloud.util.MinioTemplate;
import com.jwt.freecloud.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * 
 * 源文件表 服务实现类
 *
 * @author jwt
 * @since 2023-03-02
 */
@Service
public class FileOriginServiceImpl extends ServiceImpl<FileOriginMapper, FileOrigin> implements FileOriginService {

    @Autowired
    FileUserOriginMapper fileUserOriginMapper;
    
    @Autowired
    ThreadPoolExecutor executor;

    @Autowired
    UserMapper userMapper;

    @Autowired
    MinioTemplate minioTemplate;

    @Autowired
    RedisUtil redisUtil;

    @Override
    public void deleteNoRelationFiles(List<Long> originIds) {
        System.out.println("deleteNoRelationFiles");
        // 筛选出还有关联关系的源文件id
        List<FileUserOrigin> fileUserOrigins = fileUserOriginMapper.hasFileRelation(originIds);
        // 把originId放入hashset
        Set<Long> set = new HashSet<>();
        for (FileUserOrigin fileUserOrigin : fileUserOrigins) {
            set.add(fileUserOrigin.getOriginId());
        }

        // 根据源文件id获取源文件信息
        List<FileOrigin> originFiles = baseMapper.getListByIds(originIds);
        // 把没有关联关系的文件放入待删除集合
        List<FileOrigin> filesToDelete = new ArrayList<>();
        List<Long> originsToDelete = new ArrayList<>();
        for (FileOrigin originFile : originFiles) {
            if (set.contains(originFile.getOriginId())) {
                continue;
            }
            filesToDelete.add(originFile);
            originsToDelete.add(originFile.getOriginId());
        }

        if (originsToDelete.size() == 0) {
            System.out.println("没有要清理的");
            return;
        }
        List<Integer> userIds = filesToDelete.stream().map(FileOrigin::getCreateUserId).collect(Collectors.toList());
        // 获取用户信息(userId,reg_time两者确定minio存放位置)
        List<User> users = userMapper.getListByIds(userIds);
        // Map根据userId查询注册时间
        Map<Integer, LocalDateTime> map = new HashMap<>();
        for (User user : users) {
            map.put(user.getUserId(), user.getRegTime());
        }

        // 遍历集合删除minio中的源文件
        for (FileOrigin fileOrigin : filesToDelete) {

            Integer userId = fileOrigin.getCreateUserId();
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String bucketName = dtf.format(map.get(userId));
            String identify = fileOrigin.getFileIdentify();
            redisUtil.deleteString(FileConstants.FILE_FAST_UPLOAD_PREFIX + identify);
            try {
                minioTemplate.deleteObject(bucketName, userId + "/" + identify + "." + fileOrigin.getFileExtName());
            } catch (Exception e) {
                log.error("源文件删除异常" + e);
            }
        }

        // 更新源文件状态
        this.removeByIds(originsToDelete);
    }
}
