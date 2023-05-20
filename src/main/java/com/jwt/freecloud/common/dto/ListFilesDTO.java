package com.jwt.freecloud.common.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author：揭文滔
 * @since：2023/3/16
 */
@Data
public class ListFilesDTO implements Serializable {

    private static final long serialVersionUID = -3214860645146466465L;

    /**
     * 当前页数
     */
    private Integer page;

    /**
     * 每页条数
     */
    private Integer limit;

    /**
     * 文件路径（面包屑导航跳转）
     */
    private String filePath;

    /**
     * 排序规则
     */
    private String order;

    /**
     * 升序降序
     */
    private Boolean desc;

    /**
     * 父文件夹id（点击文件夹）
     */
    private Integer parentId;
}
