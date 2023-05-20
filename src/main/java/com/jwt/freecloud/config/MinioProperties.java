package com.jwt.freecloud.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author：揭文滔
 * @since：2023/2/9
 */
@Data
@ConfigurationProperties(prefix = "minio")
public class MinioProperties {
    /**
     * 是否开启
     */
    Boolean enabled;

    /**
     *访问端点，集群时需提供统一入口
     */
    String endpoint;

    /**
     * 用户名
     */
    String accessKey;

    /**
     * 密码
     */
    String secretKey;

    /**
     * 默认存储桶名，没有指定时，会放在默认的存储桶
     */
    String defaultBucketName;
}
