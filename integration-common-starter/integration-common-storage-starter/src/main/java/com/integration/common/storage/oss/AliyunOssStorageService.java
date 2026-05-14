package com.integration.common.storage.oss;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.ObjectMetadata;
import com.integration.common.storage.StorageService;
import com.integration.common.storage.StorageUploadResult;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 阿里云 OSS：虚拟主机风格固定公开 URL。
 */
public class AliyunOssStorageService implements StorageService {

    private final OSS ossClient;
    private final String bucket;
    private final String endpoint;

    public AliyunOssStorageService(OSS ossClient, String bucket, String endpoint) {
        this.ossClient = ossClient;
        this.bucket = bucket;
        this.endpoint = endpoint;
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
            ossClient.putObject(bucket, objectKey, inputStream, meta);
        } catch (OSSException e) {
            throw new IOException("OSS upload failed: " + e.getMessage(), e);
        } catch (ClientException e) {
            throw new IOException("OSS upload failed: " + e.getMessage(), e);
        }
        String url = getObjectAccessUrl(objectKey);
        return new StorageUploadResult(bucket, objectKey, url);
    }

    @Override
    public String getObjectAccessUrl(String objectKey) {
        return publicObjectUrl(objectKey);
    }

    private String publicObjectUrl(String objectKey) {
        String host = stripScheme(endpoint);
        String path = encodePathSegments(objectKey);
        return "https://" + bucket + "." + host + "/" + path;
    }

    private static String stripScheme(String raw) {
        String ep = raw.trim();
        if (ep.startsWith("https://")) {
            return ep.substring("https://".length());
        }
        if (ep.startsWith("http://")) {
            return ep.substring("http://".length());
        }
        return ep;
    }

    private static String encodePathSegments(String objectKey) {
        String[] parts = objectKey.split("/");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (!StringUtils.hasText(part)) {
                continue;
            }
            if (sb.length() > 0) {
                sb.append('/');
            }
            sb.append(URLEncoder.encode(part, StandardCharsets.UTF_8).replace("+", "%20"));
        }
        return sb.isEmpty() ? URLEncoder.encode(objectKey, StandardCharsets.UTF_8).replace("+", "%20") : sb.toString();
    }
}
