package com.jwt.freecloud.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jwt.freecloud.common.entity.FileRecycleItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 
 * 回收站详细表 Mapper 接口
 *
 * @author jwt
 * @since 2023-03-24
 */
@Mapper
public interface FileRecycleItemMapper extends BaseMapper<FileRecycleItem> {

    List<FileRecycleItem> getItemsByRecycleId(@Param("recycleId") Long recycleId);
}
