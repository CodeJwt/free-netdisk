package com.jwt.freecloud.service.impl;


import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jwt.freecloud.common.constants.FileConstants;
import com.jwt.freecloud.common.constants.RequestExceptionEnum;
import com.jwt.freecloud.common.constants.ResponseMessageEnum;
import com.jwt.freecloud.common.dto.ShareDTO;
import com.jwt.freecloud.common.entity.FileUser;
import com.jwt.freecloud.common.entity.UserShare;
import com.jwt.freecloud.common.entity.UserShareItem;
import com.jwt.freecloud.common.vo.MyShareVO;
import com.jwt.freecloud.dao.FileUserMapper;
import com.jwt.freecloud.dao.UserShareMapper;
import com.jwt.freecloud.interceptor.LoginInterceptor;
import com.jwt.freecloud.service.UserShareItemService;
import com.jwt.freecloud.service.UserShareService;
import com.jwt.freecloud.util.CheckUtil;
import com.jwt.freecloud.util.FreeResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 
 * 用户分享表 服务实现类
 *
 * @author jwt
 * @since 2023-03-02
 */
@Service
public class UserShareServiceImpl extends ServiceImpl<UserShareMapper, UserShare> implements UserShareService {

    @Autowired
    FileUserMapper fileUserMapper;

    @Autowired
    UserShareItemService userShareItemService;

    @Transactional
    @Override
    public FreeResult shareFiles(List<Long> fileIds, ShareDTO shareDTO) {
        Integer userId = LoginInterceptor.loginUser.get().getUserId();
        UserShare share = new UserShare();

        List<FileUser> fileList = fileUserMapper.selectBatchIds(fileIds);

        String pwd = "";
        // 如果不是公开，设置校验码以及公开状态为私密；否则直接默认
        if (shareDTO.getPublicFlag() == FileConstants.SHARE_SECRET) {
            share.setPublicFlag(FileConstants.SHARE_SECRET);
            pwd = UUID.randomUUID().toString().substring(0, 4);
            share.setSharePwd(pwd);
        }
        LocalDateTime now = LocalDateTime.now();
        share.setShareTime(now);
        if (FileConstants.SHARE_FOREVER_TIME == shareDTO.getEndTime()) {
            share.setEndTime(FileConstants.FORREVER_TIME);
        }
        share.setEndTime(now.plusDays(shareDTO.getEndTime()));
        share.setShareUserId(userId);
        share.setStatus(FileConstants.FILE_NORMAL);
        // uuid去掉'-'，长度就是32，满足数据表设计长度
        share.setShareIdentify(UUID.randomUUID().toString().replace("-", ""));
        this.save(share);

        // 插入shareItem数据
        List<UserShareItem> itemList = fileList.stream().map(fileUser -> {
            UserShareItem userShareItem = new UserShareItem();
            BeanUtil.copyProperties(fileUser, userShareItem);
            userShareItem.setShareId(share.getShareId());
            return userShareItem;
        }).collect(Collectors.toList());

        userShareItemService.saveBatch(itemList);

        // 生成分享链接并返回
        String shareUrl = share.getShareIdentify() + "_" + share.getShareId();
        return FreeResult.success(ResponseMessageEnum.FILE_SHARE_SUCCESS).putData("shareUrl",shareUrl).putData("sharePwd",pwd);
    }

    @Override
    public FreeResult visitShareByUrl(String shareUrl) {
        String[] content = CheckUtil.checkShareUrl(shareUrl);
        if (content == null) {
            return FreeResult.fail(RequestExceptionEnum.BAD_REQUEST);
        }
        Long shareId = Long.valueOf(content[1]);
        UserShare share = this.getById(shareId);
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(share.getEndTime())) {
            return FreeResult.fail(ResponseMessageEnum.FILE_SHARE_EXPIRE);
        }

        // 返回是否公开
        return FreeResult.success().putData("publicFlag", share.getPublicFlag()).putData("shareId",shareId);
    }

    @Override
    public FreeResult confirmShare(String identify, Long shareId, String pwd) {
        UserShare share = this.getById(shareId);
        // 分享id不存在、分享唯一校验不对、分享密码不对
        if (share == null || !share.getShareIdentify().equals(identify) || !share.getSharePwd().equals(pwd)) {
            return FreeResult.fail(RequestExceptionEnum.BAD_REQUEST);
        }
        List<UserShareItem> shareItems = listOneShare(shareId);
        return FreeResult.success().putData("list",shareItems);
    }

    @Override
    public FreeResult listShare(Integer page, Integer limit) {
        QueryWrapper<UserShare> queryWrapper = new QueryWrapper<UserShare>();
        queryWrapper.eq("share_user_id", LoginInterceptor.loginUser.get().getUserId());
        Page<UserShare> p = new Page<>(page,limit);
        List<UserShare> shareList = baseMapper.selectPage(p, queryWrapper).getRecords();
        LocalDateTime now = LocalDateTime.now();
        List<Object> list = shareList.stream().map(share -> {
            MyShareVO vo = new MyShareVO();
            BeanUtil.copyProperties(share, vo);
            String overdue = now.isAfter(share.getEndTime()) ? "是" : "否";
            vo.setOverdue(overdue);
            return vo;
        }).collect(Collectors.toList());
        return FreeResult.success().putData("list",list);
    }

    @Override
    public List<UserShareItem> listOneShare(Long shareId) {
        QueryWrapper<UserShareItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("share_id",shareId);
        List<UserShareItem> list = userShareItemService.list(queryWrapper);
        return list;
    }
}
