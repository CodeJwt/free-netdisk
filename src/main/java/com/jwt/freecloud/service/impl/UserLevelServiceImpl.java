package com.jwt.freecloud.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jwt.freecloud.common.constants.UserConstants;
import com.jwt.freecloud.common.entity.UserLevel;
import com.jwt.freecloud.dao.UserLevelMapper;
import com.jwt.freecloud.service.UserLevelService;
import com.jwt.freecloud.util.CheckUtil;
import org.springframework.stereotype.Service;

/**
 * 
 * 用户等级表 服务实现类
 *
 * @author jwt
 * @since 2023-03-02
 */
@Service
public class UserLevelServiceImpl extends ServiceImpl<UserLevelMapper, UserLevel> implements UserLevelService {

    /**
     * 更新用户经验
     */
    @Override
    public void increaseExpByUpload(Integer userId) {
        QueryWrapper<UserLevel> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",userId);
        UserLevel userLevel = baseMapper.selectOne(queryWrapper);
        CheckUtil.checkLevelByIncreaseExp(userLevel);
        this.updateById(userLevel);

    }

    /**
     * 根据用户id创建基础用户等级
     * @param userId
     */
    @Override
    public void insertUserLevel(Integer userId) {
        UserLevel userLevel = new UserLevel();
        userLevel.setUserId(userId);
        userLevel.setLevel(UserConstants.USER_BASE_LEVEL);
        userLevel.setCurrentExp(0);
        userLevel.setUpgradeExp(UserConstants.USER_BASE_LEVEL * UserConstants.USER_UPGRADE_EXP);
        baseMapper.insert(userLevel);
    }
}
