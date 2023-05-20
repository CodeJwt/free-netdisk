package com.jwt.freecloud.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jwt.freecloud.common.entity.UserLevel;

/**
 * 
 * 用户等级表 服务类
 *
 * @author jwt
 * @since 2023-03-02
 */
public interface UserLevelService extends IService<UserLevel> {

    void increaseExpByUpload(Integer userId);

    void insertUserLevel(Integer userId);
}
