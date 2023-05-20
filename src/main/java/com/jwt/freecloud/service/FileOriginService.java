package com.jwt.freecloud.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jwt.freecloud.common.entity.FileOrigin;

import java.util.List;

/**
 * 
 * 源文件表 服务类
 *
 * @author jwt
 * @since 2023-03-02
 */
public interface FileOriginService extends IService<FileOrigin> {

    /**
     * 删除没有关联的源文件
     * @param originIds
     */
    void deleteNoRelationFiles(List<Long> originIds);
}
