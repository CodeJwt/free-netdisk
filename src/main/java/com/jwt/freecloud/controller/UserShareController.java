package com.jwt.freecloud.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.jwt.freecloud.common.constants.RequestExceptionEnum;
import com.jwt.freecloud.common.dto.ShareDTO;
import com.jwt.freecloud.common.entity.UserShareItem;
import com.jwt.freecloud.service.UserShareService;
import com.jwt.freecloud.util.FreeResult;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 
 * 用户分享表 前端控制器
 *
 * @author jwt
 * @since 2023-02-08
 */
@RestController
@RequestMapping("/userShare")
public class UserShareController {

    @Autowired
    UserShareService userShareService;

    @PostMapping("/share")
    public FreeResult shareFiles(@RequestBody String shares){
        Map<String, String> map = JSON.parseObject(shares, new TypeReference<Map<String, String>>() {});
        List<Long> fileIds = JSON.parseObject(map.get("fileIds"), new TypeReference<List<Long>>() {});
        ShareDTO shareDTO = JSON.parseObject(map.get("shareDTO"), new TypeReference<ShareDTO>() {});
        return userShareService.shareFiles(fileIds,shareDTO);
    }

    @GetMapping("/visit")
    public FreeResult visitShareByUrl(String shareUrl) {
        return userShareService.visitShareByUrl(shareUrl);
    }

    @PostMapping("/confirm")
    public FreeResult confirmShare(@Param("shareUrl") String shareUrl, @Param("pwd") String pwd) {
        String[] s = shareUrl.split("_");
        if (s.length != 2) {
            return FreeResult.fail(RequestExceptionEnum.BAD_REQUEST);
        }
        return userShareService.confirmShare(s[0],Long.parseLong(s[1]), pwd);
    }

    /**
     * 用户个人分享内容
     * @return
     */
    @GetMapping("/list")
    public FreeResult listShare(Integer page, Integer limit) {
        return userShareService.listShare(page, limit);
    }

    @GetMapping("/listOne")
    public FreeResult listOneShare(Long shareId) {
        List<UserShareItem> shareItems = userShareService.listOneShare(shareId);
        return FreeResult.success().putData("list", shareItems);
    }


}

