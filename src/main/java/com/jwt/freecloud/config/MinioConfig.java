package com.jwt.freecloud.config;

import com.jwt.freecloud.util.MinioTemplate;
import com.jwt.freecloud.util.MyMinioClient;
import io.minio.MinioClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author：揭文滔
 * @since：2023/2/9
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({MinioClient.class})
@EnableConfigurationProperties(MinioProperties.class)
@ConditionalOnExpression("${minio.enabled}")
public class MinioConfig {

    @Bean
    @ConditionalOnMissingBean(MyMinioClient.class)
    public MyMinioClient minioClient(MinioProperties properties) {
        MinioClient minioClient = MinioClient.builder()
                .endpoint(properties.getEndpoint())
                .credentials(properties.getAccessKey(), properties.getSecretKey())
                .build();
        return new MyMinioClient(minioClient);
    }

    @Bean
    @ConditionalOnMissingBean(MinioTemplate.class)
    public MinioTemplate minioTemplate(MyMinioClient minioClient, MinioProperties properties) {
        return new MinioTemplate(minioClient, properties);
    }

}
