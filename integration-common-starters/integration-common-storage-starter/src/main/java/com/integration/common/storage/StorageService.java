package com.integration.common.storage;

import java.io.IOException;
import java.io.InputStream;

/**
 * 对象存储上传与固定公开访问 URL（需 Bucket/对象允许匿名读或经 CDN 等放行）。
 */
public interface StorageService {

    /**
     * 上传对象。
     *
     * @param objectKey     对象键（路径），如 {@code images/2025/avatar.png}
     * @param inputStream   内容流（调用方负责关闭，实现内会读取完毕）
     * @param contentLength 字节长度，须与实际一致
     * @param contentType   MIME，可为空
     * @return 桶名、对象键、公开访问 URL（无签名参数）
     */
    StorageUploadResult upload(String objectKey, InputStream inputStream, long contentLength, String contentType)
            throws IOException;

    /**
     * 构建对象的公开 GET 访问地址（不含时效参数）。
     *
     * @param objectKey 对象键
     * @return HTTPS 访问地址
     */
    String getObjectAccessUrl(String objectKey);
}
