package com.jwt.freecloud.util;

import com.google.common.collect.Multimap;
import com.jwt.freecloud.common.vo.UserVO;
import com.jwt.freecloud.config.MinioProperties;
import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import io.minio.messages.Part;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * @author：揭文滔
 * @since：2023/2/9
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class MinioTemplate {

    private MyMinioClient minioClient;

    private MinioProperties minioProperties;

    /**
     * 创建存储桶
     * @param bucketName
     */
    @SneakyThrows
    public void createBucket(String bucketName) {
        minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
    }

    /**
     * 判断存储桶是否存在
     * @param bucketName
     * @return
     */
    @SneakyThrows
    public boolean bucketExist(String bucketName) {
        return minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
    }

    /**
     * 返回当前用户对应的存储桶
     * @param userVo
     * @return
     */
    public String getBucket(UserVO userVo) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime regTime = userVo.getRegTime();
        String bucket = dtf.format(regTime);
        return bucket;
    }

    /**
     * 返回临时带签名、过期时间一天、Get请求方式的访问URL
     *
     * @param bucketName  桶名
     * @param ossFilePath Oss文件路径
     * @return
     */
    @SneakyThrows
    public String getPresignedObjectUrl(String bucketName, String ossFilePath, Map<String, String> queryParams) {
        return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Method.PUT)
                        .bucket(bucketName)
                        .object(ossFilePath)
                        .expiry(60 * 60 * 24)
                        .extraQueryParams(queryParams)
                        .build());
    }

    /**
     * 判断文件在不在
     * @param bucketName
     * @param objectName
     * @return
     */
    public StatObjectResponse statObject(String bucketName, String objectName) {
        StatObjectResponse statObjectResponse = null;
        try {
            statObjectResponse = minioClient.statObject(StatObjectArgs.builder().bucket(bucketName).object(objectName).build());
        } catch (Exception e) {
            log.info("查无此文件" + e);
        }
        return statObjectResponse;
    }

    /**
     * GetObject接口用于获取某个文件（Object）。此操作需要对此Object具有读权限。
     *
     * @param bucketName  桶名
     * @param objectName 对象名
     */
    @SneakyThrows
    public InputStream getObject(String bucketName, String objectName) {
        return minioClient.getObject(
                GetObjectArgs.builder().bucket(bucketName).object(objectName).build());
    }

    /**
     * 创建分片上传请求
     *
     * @param bucketName       存储桶
     * @param region           区域
     * @param objectName       对象名
     * @param headers          消息头
     * @param extraQueryParams 额外查询参数
     */
    public CreateMultipartUploadResponse createMultipartUpload(String bucketName, String region, String objectName,
            Multimap<String, String> headers, Multimap<String, String> extraQueryParams)
            throws NoSuchAlgorithmException, InsufficientDataException, IOException, InvalidKeyException,
            ServerException, XmlParserException, ErrorResponseException, InternalException, InvalidResponseException {
        return minioClient.createMultipartUpload(bucketName, region, objectName, headers, extraQueryParams);

    }

    /**
     * 完成分片上传，执行合并文件
     *
     * @param bucketName       存储桶
     * @param objectName       对象名
     * @param uploadId         上传ID
     * @param parts            分片
     */
    public ObjectWriteResponse completeMultipartUpload(String bucketName, String objectName,
            String uploadId, Part[] parts) throws NoSuchAlgorithmException, InsufficientDataException,
            IOException, InvalidKeyException, ServerException, XmlParserException, ErrorResponseException,
            InternalException, InvalidResponseException {
        return minioClient.completeMultipartUpload(bucketName, null, objectName, uploadId, parts, null, null);
    }

    /**
     * 查询分片数据
     *
     * @param bucketName       存储桶
     * @param region           区域
     * @param objectName       对象名
     * @param maxParts         最大分片数
     * @param partNumberMarker PartNumber起始位置，默认0
     * @param uploadId         上传ID
     * @param extraHeaders     额外消息头
     * @param extraQueryParams 额外查询参数
     */
    public ListPartsResponse listMultipart(String bucketName, String objectName, Integer maxParts,
            String uploadId) throws NoSuchAlgorithmException, InsufficientDataException,
            IOException, InvalidKeyException, ServerException, XmlParserException, ErrorResponseException,
            InternalException, InvalidResponseException {
        return minioClient.listMultipart(bucketName, null, objectName, maxParts, 0, uploadId, null, null);
    }

    /**
     * 删除指定桶下的指定对象
     * @param bucketName
     * @param objectName
     *
     */
    @SneakyThrows
    public void deleteObject(String bucketName, String objectName){
        minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(objectName).build());
    }


}
