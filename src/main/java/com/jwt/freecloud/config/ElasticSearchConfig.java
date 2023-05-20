package com.jwt.freecloud.config;


import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author：揭文滔
 * @since：2023/3/1
 * es连接配置
 */
@Configuration
public class ElasticSearchConfig {
    @Bean
    public ElasticsearchClient elasticsearchClient(){
        RestClient client = RestClient.builder(new HttpHost("192.168.10.132", 9200,"http")).build();
        ElasticsearchTransport transport = new RestClientTransport(client,new JacksonJsonpMapper());
        return new ElasticsearchClient(transport);
    }
}
