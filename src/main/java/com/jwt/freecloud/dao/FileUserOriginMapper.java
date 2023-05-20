package com.jwt.freecloud.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jwt.freecloud.common.entity.FileUserOrigin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 
 * 用户文件关联源文件表 Mapper 接口
 *
 * @author jwt
 * @since 2023-02-08
 */
@Mapper
public interface FileUserOriginMapper extends BaseMapper<FileUserOrigin> {

    List<FileUserOrigin> hasFileRelation(@Param("list") List<Long> originIds);

    void deleteBatchByIds(@Param("list") List<FileUserOrigin> relations);
}
