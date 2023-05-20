package com.jwt.freecloud;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.jwt.freecloud.common.constants.FileConstants;
import com.jwt.freecloud.common.entity.FileOrigin;
import com.jwt.freecloud.common.entity.FileRecycle;
import com.jwt.freecloud.config.FileSearch;
import com.jwt.freecloud.dao.FileOriginMapper;
import com.jwt.freecloud.dao.FileRecycleMapper;
import com.jwt.freecloud.dao.FileUserMapper;
import com.jwt.freecloud.dao.FileUserOriginMapper;
import com.jwt.freecloud.service.FileRecycleService;
import com.jwt.freecloud.service.FileUserService;
import com.jwt.freecloud.util.MinioTemplate;
import com.jwt.freecloud.util.RedisUtil;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadPoolExecutor;

@SpringBootTest
class FreeCloudApplicationTests {

    @Autowired
    public ElasticsearchClient esClient;

    @Autowired
    public RedisUtil redisUtil;

    @Autowired
    FileRecycleMapper fileRecycleMapper;

    @Autowired
    FileUserService fileUserService;

    @Autowired
    FileUserMapper fileUserMapper;

    @Autowired
    FileUserOriginMapper fileUserOriginMapper;

    @Autowired
    FileOriginMapper fileOriginMapper;

    public static final String INDEX_NAME = "freecloud";

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    FileRecycleService fileRecycleService;

    @Autowired
    ThreadPoolExecutor executor;

    @Autowired
    MinioTemplate minioTemplate;


    //@Test
    void contextLoads() {
        FileSearch fileSearch = new FileSearch();
        fileSearch.setFileId(11L);
        fileSearch.setStatus(0);
        fileSearch.setUserId(1);
        fileSearch.setFileName("广州大学");

        try {
            IndexResponse response = esClient.index(i -> i
                    .index("free_cloud")
                    .id(String.valueOf(fileSearch.getFileId()))
                    .document(fileSearch));
            System.out.println("插入记录测试"+response.version());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Test
    void searchFiles() {
        int userId = 1;

        //SearchDTO searchDTO = new SearchDTO();
        //searchDTO.setFileName("广州");

        try {
            SearchResponse<FileSearch> search = esClient.search(s -> s
                            .index("free_cloud")
                            .query(q -> q
                                    .multiMatch(m -> m
                                            .query("135")
                                            .fields("fileName", "fileExtName")
                                    )
                            )
                            .sort(sort -> sort
                                    .field(f -> f
                                            .field("fileName")
                                            .order(SortOrder.Desc))
                            )
                    , FileSearch.class);
            //SearchResponse<FileSearch> search = esClient.search(s -> s
            //                .index("free_cloud")
            //                .query(q -> q.
            //                        bool(b -> b.
            //                                filter(f -> f
            //                                        .term(t -> t
            //                                                .field("userId")
            //                                                .value(1)
            //                                        )
            //                                )
            //                                .filter(f -> f
            //                                        .term(t -> t.
            //                                                field("status")
            //                                                .value(FileConstants.FILE_NORMAL)
            //                                        )
            //                                )
            //                                .must(m -> m.
            //                                        match(t -> t.
            //                                                field("fileName").
            //                                                query("135")
            //                                        )
            //                                )
            //                        )
            //                )
            //        ,FileSearch.class);
            System.out.println("1232131");
            for (Hit<FileSearch> hit : search.hits().hits()) {
                System.out.println(hit);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }




    @Test
    public void testMethod() {

        FileOrigin fileOrigin = new FileOrigin();
        fileOrigin.setOriginId(1L);
        fileOrigin.setFileIdentify("haha");
        fileOriginMapper.updateById(fileOrigin);
        System.out.println("更新后的内容" + fileOrigin);
    }

    /*@Test
    public void testThread() throws ExecutionException, InterruptedException {

        FileRecycle recycle = new FileRecycle();
        recycle.setRecycleId(2L);
        CompletableFuture<List<Long>> fileRecycleHandle = CompletableFuture.supplyAsync(
                () -> fileRecycleService.autoCleanFiles(recycle), executor);

        List<Long> longs = fileRecycleHandle.get();
        System.out.println("???");

        List<Long> list = new ArrayList<Long>();
        list.add(2L);
        list.add(4L);
        CompletableFuture<List<FileUser>> fileUserHandle = CompletableFuture.supplyAsync(() -> fileUserService.deleteBatchByIds(list), executor);

        System.out.println("2、根据fileIds去fileRecycleService查找，分开文件夹和文件进行操作，对文件夹递归遍历，文件直接加入集合中，");

        //fileUserHandle返回结果，后边三步都有用到，故提取出来
        List<FileUser> fileUsers = fileUserHandle.get();
        System.out.println("???");
        fileUserHandle.thenRunAsync(()-> System.out.println("aha?"),executor);
    }*/

    @Test
    public void testMq() {
        FileRecycle recycle = new FileRecycle();
        recycle.setRecycleId(2L);
        rabbitTemplate.convertAndSend(FileConstants.RECYCLE_EXCHANGE, FileConstants.RECYCLE_CLEAN, recycle);
    }

    @Test
    public void testInsert() {
        /*FileRecycle fileRecycle = new FileRecycle();
        Random random = new Random();
        for(int i = 0; i < 50; i++) {
            fileRecycle.setDeleteUserId(random.nextInt(10) + 1);
            fileRecycleMapper.insert(fileRecycle);
        }*/
        FileOrigin fileOrigin = new FileOrigin();
        fileOrigin.setStatus(0);
        for (int i = 0; i < 3; i++) {
            fileOriginMapper.insert(fileOrigin);
        }
    }

    @Test
    public void testSelect() {
        List<Integer> list = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < 5; i++) {
            list.add(random.nextInt(50) + 1);
            System.out.println("随机主键" + list.get(i));
        }
        List<FileRecycle> fileRecycles = fileRecycleMapper.selectBatchIds(list);
        fileRecycles.forEach(System.out::println);
    }


}
