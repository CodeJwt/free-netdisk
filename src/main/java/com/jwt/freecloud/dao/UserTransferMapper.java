package com.jwt.freecloud.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jwt.freecloud.common.entity.UserTransfer;
import org.apache.ibatis.annotations.Mapper;

/**
 * 
 * 用户传输表 Mapper 接口
 *
 * @author jwt
 * @since 2023-02-08
 */
@Mapper
public interface UserTransferMapper extends BaseMapper<UserTransfer> {

}
