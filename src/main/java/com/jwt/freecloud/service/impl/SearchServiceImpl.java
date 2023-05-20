package com.jwt.freecloud.service.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkResponseItem;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.jwt.freecloud.common.constants.FileConstants;
import com.jwt.freecloud.common.dto.SearchDTO;
import com.jwt.freecloud.common.entity.FileUser;
import com.jwt.freecloud.common.vo.SearchVO;
import com.jwt.freecloud.common.vo.UserVO;
import com.jwt.freecloud.config.FileSearch;
import com.jwt.freecloud.interceptor.LoginInterceptor;
import com.jwt.freecloud.service.SearchService;
import com.jwt.freecloud.util.FileUtil;
import com.jwt.freecloud.util.FreeResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author：揭文滔
 * @since：2023/3/3
 */
@Slf4j
@Service
public class SearchServiceImpl implements SearchService {

    public static final String SEARCH_INDEX = "free_cloud";

    @Autowired
    public ElasticsearchClient esClient;

    @Override
    public FreeResult searchFiles(SearchDTO searchDTO) {

        UserVO loginUser = LoginInterceptor.loginUser.get();
        SearchResponse<FileSearch> search = null;

        int currentPage = searchDTO.getCurrentPage() - 1;
        int pageCount = searchDTO.getPageCount() == 0 ? 10 : searchDTO.getPageCount();

        try {


            search = esClient.search(s -> s
                            .index(SEARCH_INDEX)
                            .query(q -> q.
                                    bool(b -> b.
                                            filter(f -> f
                                                    .term(t -> t
                                                            .field("userId")
                                                            .value(loginUser.getUserId())
                                                    )
                                            )
                                            .filter(f -> f
                                                    .term(t -> t.
                                                            field("status")
                                                            .value(FileConstants.FILE_NORMAL)
                                                    )
                                            )
                                            .must(m -> m.
                                                    match(t -> t.
                                                            field("fileName").
                                                            query(searchDTO.getFileName())
                                                    )
                                            )
                                    )
                            )
                            .from(currentPage)
                            .size(pageCount)
                            ,FileSearch.class);


        } catch (IOException e) {
            e.printStackTrace();
        }
        List<SearchVO> list = new ArrayList<>();
        for (Hit<FileSearch> item : search.hits().hits()) {
            SearchVO vo = new SearchVO();
            BeanUtils.copyProperties(item.source(), vo);
            vo.setFilePath(FileUtil.getVoPath(vo.getFilePath()));
            list.add(vo);
        }

        return FreeResult.success().putData("list", list);
    }

    /**
     * 异步更新
     * @param fileUser
     * @return
     */
    @Async("threadPoolExecutor")
    @Override
    public void updateEsByFileUser(FileUser fileUser) {

        FileSearch fileSearch = new FileSearch();
        BeanUtils.copyProperties(fileUser, fileSearch);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String format = dtf.format(fileUser.getUpdateTime());
        fileSearch.setUpdateTime(format);

        try {
            esClient.index(i -> i.index(SEARCH_INDEX).id(String.valueOf(fileSearch.getFileId())).document(fileSearch));
        } catch (IOException e) {
            log.error("es更新失败，请重试");
        }

    }

    /**
     * 异步更新
     * @param fileUsers
     */
    @Async("threadPoolExecutor")
    @Override
    public void updateEsByFileUsers(List<FileUser> fileUsers) {
        // 批量条件
        BulkRequest.Builder bulk = new BulkRequest.Builder();

        // 把fileUser转换为fileSearch对象，再封装批量条件
        for (FileUser fileUser : fileUsers) {
            FileSearch fileSearch = new FileSearch();
            BeanUtils.copyProperties(fileUser, fileSearch);
            bulk.operations(op -> op
                    .index(idx -> idx
                    .index(SEARCH_INDEX)
                    .id(String.valueOf(fileSearch.getFileId()))
                    .document(fileSearch))
            );
        }

        // 执行批量操作
        BulkResponse result = null;
        try {
            result = esClient.bulk(bulk.build());
        } catch (IOException e) {
            log.error("es batchUpdate error");
            for (BulkResponseItem item : result.items()) {
                if (item.error() != null) {
                    log.error(item.error().reason());
                }
            }
        }

    }

    @Override
    public void deleteEsByFileId(Long fileId) {
        try {
            esClient.delete(i -> i.index(SEARCH_INDEX).id(String.valueOf(fileId)));
        } catch (IOException e) {
            log.error("es删除失败,请重试");
        }
    }


    @Override
    public void deleteEsByFileIds(List<Long> ids) {
        System.out.println("deleteEsByFileIds");
        BulkRequest.Builder bulk = new BulkRequest.Builder();

        for (Long id : ids) {
            bulk.operations(op -> op
                    .delete(d -> d
                    .index(SEARCH_INDEX)
                    .id(String.valueOf(id)))
            );
        }

        BulkResponse result = null;
        try {
            result = esClient.bulk(bulk.build());
        } catch (IOException e) {
            log.error("es batchDelete error");
            for (BulkResponseItem item : result.items()) {
                if (item.error() != null) {
                    log.error(item.error().reason());
                }
            }
        }

    }
}
