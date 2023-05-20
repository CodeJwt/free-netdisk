package com.jwt.freecloud.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jwt.freecloud.common.entity.UserShareItem;
import com.jwt.freecloud.util.FreeResult;

import java.util.List;

/**
 * 
 * 用户分享详情表 服务类
 *
 * @author jwt
 * @since 2023-03-24
 */
public interface UserShareItemService extends IService<UserShareItem> {

    FreeResult saveShare(List<UserShareItem> itemList,String logicalPath);
}
