package com.jwt.freecloud.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jwt.freecloud.common.entity.Activity;
import com.jwt.freecloud.dao.ActivityMapper;
import com.jwt.freecloud.service.ActivityService;
import org.springframework.stereotype.Service;

/**
 * 
 * 活动表 服务实现类
 *
 * @author jwt
 * @since 2023-03-02
 */
@Service
public class ActivityServiceImpl extends ServiceImpl<ActivityMapper, Activity> implements ActivityService {

}
