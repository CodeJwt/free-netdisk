package com.jwt.freecloud.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author：揭文滔
 * @since：2023/3/1
 */
@Data
@ConfigurationProperties(prefix = "pool")
public class ThreadPoolProperties {
    private Integer coreSize;
    private Integer maxSize;
    private Integer keepAliveTime;
}
