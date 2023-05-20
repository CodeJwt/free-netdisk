package com.jwt.freecloud.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jwt.freecloud.common.entity.FileOrigin;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 
 * 源文件表 Mapper 接口
 *
 * @author jwt
 * @since 2023-02-08
 */
@Mapper
public interface FileOriginMapper extends BaseMapper<FileOrigin> {


    List<FileOrigin> getListByIds(List<Long> originIds);
}
