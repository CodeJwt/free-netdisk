package com.jwt.freecloud.controller;

import com.jwt.freecloud.common.dto.SearchDTO;
import com.jwt.freecloud.service.SearchService;
import com.jwt.freecloud.util.FreeResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author：揭文滔
 * @since：2023/3/3
 */
@RestController
@RequestMapping("/search")
public class SearchController {

    @Autowired
    public SearchService searchService;

    @GetMapping("/files")
    public FreeResult searchFiles(SearchDTO searchDTO) {
        return searchService.searchFiles(searchDTO);
    }
}
