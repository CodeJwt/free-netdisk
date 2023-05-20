package com.jwt.freecloud.controller;


import com.jwt.freecloud.common.dto.UploadDTO;
import com.jwt.freecloud.service.FileOriginService;
import com.jwt.freecloud.service.FileUserService;
import com.jwt.freecloud.service.UserTransferService;
import com.jwt.freecloud.util.FreeResult;
import com.jwt.freecloud.util.MinioTemplate;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 
 * 用户传输表 前端控制器
 *
 * @author jwt
 * @since 2023-02-08
 */
@Slf4j
@RestController
@RequestMapping("/userTransfer")
public class UserTransferController {

    @Autowired
    UserTransferService userTransferService;

    @Autowired
    MinioTemplate minioTemplate;

    @Autowired
    FileUserService fileUserService;

    @Autowired
    FileOriginService fileOriginService;


    /**
     * 文件上传请求：1、文件秒传，直接返回文件上传成功
     *            2、返回分片上传需要的签名数据URL及 uploadId
     * @param upload
     * @return
     */
    @RequestMapping("/createFileUpload")
    @ResponseBody
    public FreeResult createFileUpload(UploadDTO upload) {

        return userTransferService.createFileUpload(upload);

    }

    @GetMapping("/completeMultipartUpload")
    @SneakyThrows
    @ResponseBody
    public FreeResult completeMultipartUpload(String uploadId) {
        return userTransferService.completeMultipartUpload(uploadId);
    }

    // TODO 下载待测试
    @GetMapping("/download")
    public FreeResult downloadFile(Integer fileId, HttpServletResponse response) throws IOException {
        return userTransferService.downloadFile(fileId,response);
    }
}

