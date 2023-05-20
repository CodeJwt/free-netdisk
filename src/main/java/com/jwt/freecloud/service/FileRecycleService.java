package com.jwt.freecloud.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jwt.freecloud.common.dto.CleanDTO;
import com.jwt.freecloud.common.entity.FileRecycle;
import com.jwt.freecloud.common.entity.FileRecycleItem;
import com.jwt.freecloud.common.entity.FileUser;
import com.jwt.freecloud.util.FreeResult;

import java.util.List;

/**
 * 
 * 回收站表 服务类
 *
 * @author jwt
 * @since 2023-03-24
 */
public interface FileRecycleService extends IService<FileRecycle> {

    void filesToRecycle(List<FileUser> fileIds);

    /**
     * 自动清理
     * @param list
     * @return 返回对应的文件id集合
     */
    List<Long> autoCleanFiles(List<FileRecycleItem> list);

    /**
     * 手动清理
     * @param itemList 回收细分项集合
     * @return 返回新的已使用容量
     */
    FreeResult cleanFiles(List<CleanDTO> itemList);

    /**
     * 还原文件
     * @param itemIds
     * @return
     */
    FreeResult recoverFiles(List<Long> itemIds);

    FreeResult listRecycle(Integer page, Integer limit);
}
