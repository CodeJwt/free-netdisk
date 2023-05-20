package com.jwt.freecloud.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.jwt.freecloud.common.entity.UserShareItem;
import com.jwt.freecloud.service.UserShareItemService;
import com.jwt.freecloud.util.FreeResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 
 * 用户分享详情表 前端控制器
 *
 * @author jwt
 * @since 2023-03-24
 */
@RestController
@RequestMapping("/userShareItem")
public class UserShareItemController {

    @Autowired
    UserShareItemService userShareItemService;

    @PostMapping("/save")
    public FreeResult saveShare(@RequestBody String json) {
        Map<String, String> map = JSON.parseObject(json, new TypeReference<Map<String, String>>() {});
        List<UserShareItem> itemList = JSON.parseObject(map.get("itemList"), new TypeReference<List<UserShareItem>>() {});
        String savePath = map.get("savePath");
        return userShareItemService.saveShare(itemList, savePath);
    }

}

