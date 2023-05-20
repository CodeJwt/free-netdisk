package com.jwt.freecloud.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jwt.freecloud.common.entity.FileRecycleItem;
import com.jwt.freecloud.dao.FileRecycleItemMapper;
import com.jwt.freecloud.service.FileRecycleItemService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 
 * 回收站详细表 服务实现类
 *
 * @author jwt
 * @since 2023-03-24
 */
@Service
public class FileRecycleItemServiceImpl extends ServiceImpl<FileRecycleItemMapper, FileRecycleItem> implements FileRecycleItemService {

    @Override
    public List<FileRecycleItem> getItemsByRecycleId(Long recycleId) {
        return baseMapper.getItemsByRecycleId(recycleId);
    }
}
