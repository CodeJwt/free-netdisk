package com.jwt.freecloud.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jwt.freecloud.common.entity.Activity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 
 * 活动表 Mapper 接口
 *
 * @author jwt
 * @since 2023-02-08
 */
@Mapper
public interface ActivityMapper extends BaseMapper<Activity> {

}
