package com.integration.common.storage.cos;

import com.integration.common.storage.StorageService;
import com.integration.common.storage.StorageUploadResult;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.exception.CosServiceException;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * 腾讯云 COS：固定公开对象 URL（{@link COSClient#getObjectUrl}）。
 */
public class TencentCosStorageService implements StorageService {

    private final COSClient cosClient;
    private final String bucket;

    public TencentCosStorageService(COSClient cosClient, String bucket) {
        this.cosClient = cosClient;
        this.bucket = bucket;
    }

    @Override
    public StorageUploadResult upload(String objectKey, InputStream inputStream, long contentLength, String contentType)
            throws IOException {
        ObjectMetadata meta = new ObjectMetadata();
        meta.setContentLength(contentLength);
        if (StringUtils.hasText(contentType)) {
            meta.setContentType(contentType);
        }
        try {
            cosClient.putObject(new PutObjectRequest(bucket, objectKey, inputStream, meta));
        } catch (CosServiceException e) {
            throw new IOException("COS upload failed: " + e.getMessage(), e);
        } catch (CosClientException e) {
            throw new IOException("COS upload failed: " + e.getMessage(), e);
        }
        String url = getObjectAccessUrl(objectKey);
        return new StorageUploadResult(bucket, objectKey, url);
    }

    @Override
    public String getObjectAccessUrl(String objectKey) {
        return cosClient.getObjectUrl(bucket, objectKey).toString();
    }
}
