package com.jwt.freecloud.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.jwt.freecloud.common.dto.CleanDTO;
import com.jwt.freecloud.service.FileRecycleService;
import com.jwt.freecloud.util.FreeResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 
 * 回收站表 前端控制器
 *
 * @author jwt
 * @since 2023-03-24
 */
@RestController
@RequestMapping("/fileRecycle")
public class FileRecycleController {

    @Autowired
    FileRecycleService fileRecycleService;

    @GetMapping("/list")
    public FreeResult listRecycle(Integer page, Integer limit) {
        return fileRecycleService.listRecycle(page, limit);
    }

    @PostMapping("/clean")
    public FreeResult cleanFiles(@RequestBody String items, HttpServletRequest request) {
        Map<String, String> map = JSON.parseObject(items, new TypeReference<Map<String, String>>() {});
        List<CleanDTO> itemList = JSON.parseObject(map.get("itemList"), new TypeReference<List<CleanDTO>>() {});
        return fileRecycleService.cleanFiles(itemList);
    }

    @PostMapping("/recover")
    public FreeResult recoverFiles(@RequestBody String itemIds) {
        Map<String, String> map = JSON.parseObject(itemIds, new TypeReference<Map<String, String>>() {});
        List<Long> list = JSON.parseObject(map.get("itemIds"), new TypeReference<List<Long>>(){});
        return fileRecycleService.recoverFiles(list);
    }
}

