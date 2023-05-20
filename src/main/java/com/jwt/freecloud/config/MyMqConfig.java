package com.jwt.freecloud.config;

import com.jwt.freecloud.common.constants.FileConstants;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author：揭文滔
 * @since：2023/3/24
 */
@Configuration
public class MyMqConfig {


    /**
     * 绑定延时队列
     * @return
     */
    @Bean
    public Binding recycleCreateBinding() {
        return BindingBuilder.bind(recycleCleanDelayQueue()).to(recycleExchange()).with(FileConstants.RECYCLE_CLEAN_DELAY);
    }

    /**
     * 绑定回收清理队列
     * @return
     */
    @Bean
    public Binding recycleReleaseBinding() {
        return BindingBuilder.bind(recycleCleanQueue()).to(recycleExchange()).with(FileConstants.RECYCLE_CLEAN);
    }

    /**
     * 延时清理队列
     * @return
     */
    @Bean
    public Queue recycleCleanDelayQueue() {

        Map<String,Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange","recycle-exchange");
        arguments.put("x-dead-letter-routing-key",FileConstants.RECYCLE_CLEAN);
        arguments.put("x-message-ttl", FileConstants.FILE_AUTO_CLEAN_TIME);

        return new Queue("recycle.clean.delay.queue",true, false, false, arguments);
    }

    /**
     * 回收站清理队列
     * @return
     */
    @Bean
    public Queue recycleCleanQueue() {
        return new Queue("recycle.clean.queue", true, false, false);
    }

    /**
     * 回收交换机
     * @return
     */
    @Bean
    public DirectExchange recycleExchange() {
        return new DirectExchange("recycle-exchange",true, false);
    }


    @Bean
    public Binding folderMoveBingding() {
        return BindingBuilder.bind(folderMoveQueue()).to(folderMoveExchange()).with(FileConstants.DIRECTORY_MOVE);
    }

    /**
     * 文件夹移动处理队列
     * @return
     */
    @Bean
    public Queue folderMoveQueue() {
        return new Queue("folder.move.queue",true, false, false);
    }

    /**
     * 文件夹移动交换机
     * @return
     */
    @Bean
    public DirectExchange folderMoveExchange() {
        return new DirectExchange("folder-move-exchange", true, false);
    }

}
