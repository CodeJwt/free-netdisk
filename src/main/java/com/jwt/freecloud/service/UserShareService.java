package com.jwt.freecloud.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jwt.freecloud.common.dto.ShareDTO;
import com.jwt.freecloud.common.entity.UserShare;
import com.jwt.freecloud.common.entity.UserShareItem;
import com.jwt.freecloud.util.FreeResult;

import java.util.List;

/**
 * 
 * 用户分享表 服务类
 *
 * @author jwt
 * @since 2023-03-02
 */
public interface UserShareService extends IService<UserShare> {

    FreeResult shareFiles(List<Long> fileIds, ShareDTO shareDTO);

    FreeResult visitShareByUrl(String shareUrl);

    List<UserShareItem> listOneShare(Long shareId);

    FreeResult listShare(Integer page, Integer limit);

    FreeResult confirmShare(String identify,Long shareId, String pwd);
}

