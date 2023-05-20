package com.jwt.freecloud.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jwt.freecloud.common.entity.FileUser;
import com.jwt.freecloud.common.entity.FileUserOrigin;

import java.util.List;

/**
 * 
 * 用户文件关联源文件表 服务类
 *
 * @author jwt
 * @since 2023-03-02
 */
public interface FileUserOriginService extends IService<FileUserOrigin> {

    /**
     * 批量删除关联关系
     * @param files
     */
    void deleteBatchByIds(List<FileUser> files);
}
