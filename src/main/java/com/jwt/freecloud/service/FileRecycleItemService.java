package com.jwt.freecloud.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jwt.freecloud.common.entity.FileRecycleItem;

import java.util.List;

/**
 * 
 * 回收站详细表 服务类
 *
 * @author jwt
 * @since 2023-03-24
 */
public interface FileRecycleItemService extends IService<FileRecycleItem> {

    List<FileRecycleItem> getItemsByRecycleId(Long recycleId);
}
