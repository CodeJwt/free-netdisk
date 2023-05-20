package com.jwt.freecloud.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author：揭文滔
 * @since：2023/3/1
 * 定时任务
 *      1、@EnableScheduling 开启定时任务
 *      2、@Scheduled  开启一个定时任务
 *      3、自动配置类 TaskSchedulingAutoConfiguration
 *
 * 异步任务
 *      1、@EnableAsync 开启异步任务功能
 *      2、@Async 给希望异步执行的方法上标注
 *      3、自动配置类 TaskExecutionAutoConfiguration 属性绑定在TaskExecutionProperties
 *
 */
@EnableScheduling
@EnableAsync
@Configuration
public class ScheduledConfig {
}
