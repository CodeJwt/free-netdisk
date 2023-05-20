package com.jwt.freecloud.common.dto;

import lombok.Data;

/**
 * @author：揭文滔
 * @since：2023/3/3
 * 前端搜索条件
 */
@Data
public class SearchDTO {

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 当前页
     */
    private Integer currentPage;

    /**
     * 每页数量
     */
    private Integer pageCount;


}
