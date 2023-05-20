package com.jwt.freecloud.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.jwt.freecloud.common.entity.FileRecycleItem;
import com.jwt.freecloud.common.entity.FileUser;
import com.jwt.freecloud.service.*;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * @author：揭文滔
 * @since：2023/3/24
 */
@Component
@RabbitListener(queues = "recycle.clean.queue")
@Slf4j
public class RecycleCleanListener {

    @Autowired
    FileRecycleService fileRecycleService;

    @Autowired
    FileUserService fileUserService;

    @Autowired
    FileUserOriginService fileUserOriginService;

    @Autowired
    FileOriginService fileOriginService;

    @Autowired
    SearchService searchService;

    @Autowired
    ThreadPoolExecutor executor;

    /**
     * 收到清理消息后：
     *      1、根据fileRecycle去fileRecycleService查找，找到所有细分的fileRecycleItem细分项，进行逻辑删除，并返回对应的fileIds集合
     *      2、根据fileIds去fileRecycleService查找，分开文件夹和文件进行操作，对文件夹递归遍历，文件直接加入集合中，
     *          最后对文件集（包括文件夹）进行逻辑删除，返回不包含文件夹的FileUser集合
     *      3.1、FileUser集合中有fileId和originId，根据这俩可以在文件关联表中找到记录并执行物理删除（因为这个表普通的用户功能操作不了，不需要逻辑删除）
     *      3.2、从FileUser集合中提取出originIds，到源文件表进行逻辑删除
     *      4、判断源文件是否需要删除
     * @param jsonStr
     * @param channel
     * @param message
     * @throws IOException
     */
    @RabbitHandler
    public void listen(String jsonStr, Channel channel, Message message) throws IOException {

        try {
            List<FileRecycleItem> list = JSON.parseObject(jsonStr, new TypeReference<List<FileRecycleItem>>() {});

            System.out.println("自动清理开始");
            long old = System.currentTimeMillis();

            //1、根据fileRecycleItem细分项，进行逻辑删除，并返回对应的fileIds集合
            CompletableFuture<List<Long>> fileRecycleHandle = CompletableFuture.supplyAsync(
                    () -> fileRecycleService.autoCleanFiles(list), executor);

            //2、根据fileIds去fileRecycleService查找，分开文件夹和文件进行操作，对文件夹递归遍历，文件直接加入集合中，
            //     最后对文件集（包括文件夹）进行逻辑删除，返回不包含文件夹的FileUser集合
            //     并更新用户容量
            CompletableFuture<List<FileUser>> fileUserHandle = fileRecycleHandle.thenApplyAsync(
                    fileIds -> fileUserService.deleteBatchByIds(fileIds), executor);

            // 2.1、批量删除es
            fileRecycleHandle.thenAcceptAsync(
                    fileIds -> searchService.deleteEsByFileIds(fileIds),executor);

            //fileUserHandle返回结果，后边步骤都有用到，故提取出来
            List<FileUser> fileUsers = fileUserHandle.get();
            if (fileUsers == null || fileUsers.size() == 0) {
                long cur = System.currentTimeMillis();
                System.out.println("自动清理耗时： " + (cur - old) + "毫秒");
            } else {

                List<Long> originIds = fileUsers.stream().map(fileUser -> fileUser.getOriginId()).distinct().collect(Collectors.toList());

                //3.1、FileUser集合中有fileId和originId，根据这俩可以在文件关联表中找到记录并执行物理删除（因为这个表普通的用户功能操作不了，不需要逻辑删除）
                CompletableFuture<Void> userOriginHandle = fileUserHandle.thenRunAsync(() -> fileUserOriginService.deleteBatchByIds(fileUsers), executor);

                //4、判断源文件是否需要删除
                CompletableFuture<Void> minioHandle = userOriginHandle.thenRunAsync(() -> fileOriginService.deleteNoRelationFiles(originIds), executor);

                //等待所有异步任务完成
                CompletableFuture.allOf(fileRecycleHandle,fileUserHandle,userOriginHandle,minioHandle).get();

                long cur = System.currentTimeMillis();
                System.out.println("自动清理耗时： " + (cur - old) + "毫秒");
            }
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);

        } catch (IOException | ExecutionException | InterruptedException e) {
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
        }
    }
}
