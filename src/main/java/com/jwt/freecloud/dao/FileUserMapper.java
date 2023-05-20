package com.jwt.freecloud.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jwt.freecloud.common.entity.FileUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 
 * 用户文件表（页面展示） Mapper 接口
 *
 * @author jwt
 * @since 2023-02-08
 */
@Mapper
public interface FileUserMapper extends BaseMapper<FileUser> {

    /**
     * 返回仍在回收站中的用户文件
     * @param fileIds
     * @return
     */
    List<FileUser> getListByIds(@Param("list") List<Long> fileIds);


    /**
     * 从回收站中还原文件
     * @param list
     */
    void recoverFiles(@Param("list") List<Long> list);
}
