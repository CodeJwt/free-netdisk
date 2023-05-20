package com.jwt.freecloud.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jwt.freecloud.common.constants.FileConstants;
import com.jwt.freecloud.common.constants.ResponseMessageEnum;
import com.jwt.freecloud.common.entity.FileUser;
import com.jwt.freecloud.common.entity.UserShareItem;
import com.jwt.freecloud.common.vo.UserVO;
import com.jwt.freecloud.dao.UserShareItemMapper;
import com.jwt.freecloud.interceptor.LoginInterceptor;
import com.jwt.freecloud.service.FileUserService;
import com.jwt.freecloud.service.SearchService;
import com.jwt.freecloud.service.UserService;
import com.jwt.freecloud.service.UserShareItemService;
import com.jwt.freecloud.util.CheckUtil;
import com.jwt.freecloud.util.FreeResult;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * 
 * 用户分享详情表 服务实现类
 *
 * @author jwt
 * @since 2023-03-24
 */
@Service
public class UserShareItemServiceImpl extends ServiceImpl<UserShareItemMapper, UserShareItem> implements UserShareItemService {

    @Autowired
    FileUserService fileUserService;

    @Autowired
    UserService userService;

    @Autowired
    SearchService searchService;

    @Autowired
    ThreadPoolExecutor executor;

    /**
     * 保存分享
     * @param itemList
     * @param logicalPath
     * @return
     */
    @SneakyThrows
    @Transactional
    @Override
    public FreeResult saveShare(List<UserShareItem> itemList, String logicalPath) {
        UserVO userVO = LoginInterceptor.loginUser.get();
        Integer userId = userVO.getUserId();
        Long totalSize = 0L;
        for (UserShareItem userShareItem : itemList) {
            totalSize += userShareItem.getFileSize();
        }
        // 容量不足
        if (!CheckUtil.checkMemoryEnough(userVO, totalSize)) {
            return FreeResult.fail(ResponseMessageEnum.SPACE_NOT_ENOUGH);
        }
        Long parentId = fileUserService.getParentIdByPath(logicalPath,userId);
        LocalDateTime now = LocalDateTime.now();
        List<FileUser> fileUserList = itemList.stream().map(item -> {
            FileUser fileUser = new FileUser();
            BeanUtil.copyProperties(item, fileUser);
            fileUser.setUserId(userId);
            fileUser.setParentId(parentId);
            fileUser.setFilePath(logicalPath + item.getFileName() + "." + item.getFileExtName());
            fileUser.setStatus(FileConstants.FILE_NORMAL);
            fileUser.setCreateTime(now);
            fileUser.setUpdateTime(now);
            return fileUser;
        }).collect(Collectors.toList());

        // 保存分享文件到用户目录
        fileUserService.saveBatch(fileUserList);

        // 批量更新es
        searchService.updateEsByFileUsers(fileUserList);

        // 更新用户已用容量
        Long newUsed = userVO.getUsedMemory() + totalSize;
        userService.updateUsedMemory(userVO, newUsed);

        return FreeResult.success(ResponseMessageEnum.FILE_SHARE_SUCCESS);
    }
}
