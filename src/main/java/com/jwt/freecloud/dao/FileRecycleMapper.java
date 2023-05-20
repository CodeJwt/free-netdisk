package com.jwt.freecloud.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jwt.freecloud.common.dto.RecycleDTO;
import com.jwt.freecloud.common.entity.FileRecycle;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 
 * 回收站表 Mapper 接口
 *
 * @author jwt
 * @since 2023-03-24
 */
@Mapper
public interface FileRecycleMapper extends BaseMapper<FileRecycle> {

    List<RecycleDTO> listPage(@Param("page") Integer page, @Param("pageSize") Integer pageSize, @Param("userId") Integer userId);
}
