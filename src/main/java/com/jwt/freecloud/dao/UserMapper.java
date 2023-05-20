package com.jwt.freecloud.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jwt.freecloud.common.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 
 * 用户表 Mapper 接口
 *
 * @author jwt
 * @since 2023-02-08
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    List<User> getListByIds(List<Integer> userIds);

    void updateUsedMemory(Integer userId, Long usedMemory);
}
