package com.jwt.freecloud.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jwt.freecloud.common.entity.FileUser;
import com.jwt.freecloud.common.entity.FileUserOrigin;
import com.jwt.freecloud.dao.FileUserOriginMapper;
import com.jwt.freecloud.service.FileUserOriginService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 
 * 用户文件关联源文件表 服务实现类
 *
 * @author jwt
 * @since 2023-03-02
 */
@Service
public class FileUserOriginServiceImpl extends ServiceImpl<FileUserOriginMapper, FileUserOrigin> implements FileUserOriginService {


    @Transactional
    @Override
    public void deleteBatchByIds(List<FileUser> files) {
        System.out.println("关联关系deleteBatchByIds");
        List<FileUserOrigin> relations = files.stream().map(file -> {
            FileUserOrigin fileUserOrigin = new FileUserOrigin();
            BeanUtils.copyProperties(file, fileUserOrigin);
            return fileUserOrigin;
        }).collect(Collectors.toList());

        //通过联合索引快速定位删除
        baseMapper.deleteBatchByIds(relations);

    }
}
