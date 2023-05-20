package com.jwt.freecloud.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jwt.freecloud.common.entity.UserJoin;
import com.jwt.freecloud.dao.UserJoinMapper;
import com.jwt.freecloud.service.UserJoinService;
import org.springframework.stereotype.Service;

/**
 * 
 * 用户参与活动记录表 服务实现类
 *
 * @author jwt
 * @since 2023-03-02
 */
@Service
public class UserJoinServiceImpl extends ServiceImpl<UserJoinMapper, UserJoin> implements UserJoinService {

}
