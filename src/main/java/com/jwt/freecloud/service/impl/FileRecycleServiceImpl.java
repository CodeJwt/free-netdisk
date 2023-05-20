package com.jwt.freecloud.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jwt.freecloud.common.constants.FileConstants;
import com.jwt.freecloud.common.constants.ResponseMessageEnum;
import com.jwt.freecloud.common.dto.CleanDTO;
import com.jwt.freecloud.common.dto.RecycleDTO;
import com.jwt.freecloud.common.entity.FileRecycle;
import com.jwt.freecloud.common.entity.FileRecycleItem;
import com.jwt.freecloud.common.entity.FileUser;
import com.jwt.freecloud.common.vo.RecycleVO;
import com.jwt.freecloud.common.vo.UserVO;
import com.jwt.freecloud.dao.FileRecycleMapper;
import com.jwt.freecloud.interceptor.LoginInterceptor;
import com.jwt.freecloud.service.*;
import com.jwt.freecloud.util.FreeResult;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * 
 * 回收站表 服务实现类
 *
 * @author jwt
 * @since 2023-03-24
 */
@Service
public class FileRecycleServiceImpl extends ServiceImpl<FileRecycleMapper, FileRecycle> implements FileRecycleService {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    FileRecycleItemService fileRecycleItemService;

    @Autowired
    ThreadPoolExecutor executor;

    @Autowired
    SearchService searchService;

    @Autowired
    FileUserService fileUserService;

    @Autowired
    UserService userService;

    @Transactional
    @Override
    public void filesToRecycle(List<FileUser> fileUsers) {

        UserVO userVO = LoginInterceptor.loginUser.get();
        FileRecycle fileRecycle = new FileRecycle();
        fileRecycle.setDeleteUserId(userVO.getUserId());
        LocalDateTime now = LocalDateTime.now();//删除时间
        LocalDateTime autoTime = now.plusDays(3);//自动清理时间
        fileRecycle.setDeleteTime(now);
        fileRecycle.setClearTime(autoTime);
        baseMapper.insert(fileRecycle);

        Long recycleId = fileRecycle.getRecycleId();
        //删除细分项
        List<FileRecycleItem> list = fileUsers.stream().map(fileuser -> {
            FileRecycleItem item = new FileRecycleItem();
            BeanUtil.copyProperties(fileuser, item);
            item.setRecycleId(recycleId);
            item.setStatus(FileConstants.FILE_NORMAL);
            return item;
        }).collect(Collectors.toList());

        fileRecycleItemService.saveBatch(list);

        String s = JSON.toJSONString(list);

        //创建成功后异步发消息到rabbitmq
        CompletableFuture.runAsync(
                () -> rabbitTemplate.convertAndSend(FileConstants.RECYCLE_EXCHANGE,FileConstants.RECYCLE_CLEAN_DELAY, s),executor);

    }

    @Transactional
    @Override
    public List<Long> autoCleanFiles(List<FileRecycleItem> itemList) {
        System.out.println("autoCleanFiles");
        //TODO 删掉过滤后测试
        List<Long> itemIds = new ArrayList<>(itemList.size());
        List<Long> fileIds = new ArrayList<>(itemList.size());
        for (FileRecycleItem fileRecycleItem : itemList) {
            fileIds.add(fileRecycleItem.getFileId());
            itemIds.add(fileRecycleItem.getItemId());
        }
        // 更改回收项状态，防止在回收站显示
        fileRecycleItemService.removeByIds(itemIds);

        baseMapper.deleteById(itemList.get(0).getRecycleId());

        return fileIds;
    }

    @Transactional
    @Override
    public FreeResult cleanFiles(List<CleanDTO> itemList) {
        //TODO 改为消息队列操作
        /*UserVO userVO = LoginInterceptor.loginUser.get();
        List<Long> itemIds = new ArrayList<>(itemList.size());
        List<Long> fileIds = new ArrayList<>(itemList.size());
        Long totalSize = 0L;
        for (CleanDTO fileRecycleItem : itemList) {
            itemIds.add(fileRecycleItem.getItemId());
            fileIds.add(fileRecycleItem.getFileId());
            totalSize += fileRecycleItem.getFileSize();
        }
        // 更改回收项状态，防止在回收站显示
        fileRecycleItemService.removeByIds(itemIds);


        // 异步批量删除es
        CompletableFuture.runAsync(() -> searchService.deleteEsByFileIds(fileIds),executor);

        // 更新容量
        Long newUsed = userVO.getUsedMemory() - totalSize;
        userService.updateUsedMemory(userVO, newUsed);*/
        List<FileRecycleItem> items = new ArrayList<>();
        List<Long> itemIds = new ArrayList<>(itemList.size());
        for (CleanDTO cleanDTO : itemList) {
            FileRecycleItem item = new FileRecycleItem();
            BeanUtil.copyProperties(cleanDTO, item);
            items.add(item);
            itemIds.add(cleanDTO.getItemId());
        }
        // 更改回收项状态，防止在回收站显示
        fileRecycleItemService.removeByIds(itemIds);

        String s = JSON.toJSONString(items);

        //创建成功后异步发消息到rabbitmq
        CompletableFuture.runAsync(
                () -> rabbitTemplate.convertAndSend(FileConstants.RECYCLE_EXCHANGE,FileConstants.RECYCLE_CLEAN, s),executor);

        return FreeResult.success(ResponseMessageEnum.FILE_DELETE_SUCCESS);
    }

    @Transactional
    @Override
    public FreeResult recoverFiles(List<Long> itemIds) {
        List<FileRecycleItem> itemList = fileRecycleItemService.listByIds(itemIds);
        List<Long> fileIds = itemList.stream().map(FileRecycleItem::getFileId).collect(Collectors.toList());

        fileUserService.recoverFiles(fileIds);

        fileRecycleItemService.removeByIds(itemIds);

        return FreeResult.success(ResponseMessageEnum.FILE_RECOVER_SUCCESS);
    }


    @Override
    public FreeResult listRecycle(Integer page, Integer limit) {
        //TODO 回收站展示测试
        Integer userId = LoginInterceptor.loginUser.get().getUserId();
        List<RecycleDTO> list = baseMapper.listPage((page - 1) * limit, limit, userId);
        List<RecycleVO> collect = list.stream().map(recycleDTO -> {
            RecycleVO recycleVO = new RecycleVO();
            BeanUtil.copyProperties(recycleDTO, recycleVO);
            return recycleVO;
        }).collect(Collectors.toList());
        return FreeResult.success().putData("list", collect);
    }
}
