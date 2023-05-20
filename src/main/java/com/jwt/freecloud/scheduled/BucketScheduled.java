package com.jwt.freecloud.scheduled;

import com.jwt.freecloud.util.MinioTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author：揭文滔
 * @since：2023/3/1
 */
@Slf4j
@Service
public class BucketScheduled {

    @Autowired
    public MinioTemplate minioTemplate;


    /**
     * 创建日期对应的存储桶
     * 1、Spring中6位组成，不允许第7位的年,语法：秒 分 时 日 月 周
     * 2、定时任务不应该阻塞。默认是阻塞的
     */
    @Async("threadPoolExecutor")
    @Scheduled(cron = "0 0 10 * * *") //每天10点执行
    public void createTodayBucket () {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime regTime = LocalDateTime.now();
        String bucket = formatter.format(regTime);
        if ( !minioTemplate.bucketExist(bucket)) {
            minioTemplate.createBucket(bucket);
            log.info("{} create bucket: {} success!", regTime,bucket);
        }
    }

}
