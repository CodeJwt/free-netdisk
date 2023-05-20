package com.jwt.freecloud.listener;

import com.jwt.freecloud.common.entity.FileUser;
import com.jwt.freecloud.service.FileUserService;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author：揭文滔
 * @since：2023/3/24
 */
@Component
@RabbitListener(queues = "folder.move.queue")
public class FolderMoveListener {

    @Autowired
    FileUserService fileUserService;

    @RabbitHandler
    public void listen(FileUser fileUser, Channel channel, Message message) throws IOException {

        try {
            //TODO fileUserService.folderMove
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (IOException e) {
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        }
    }
}
