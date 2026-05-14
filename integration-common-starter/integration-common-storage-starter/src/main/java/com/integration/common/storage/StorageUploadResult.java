package com.integration.common.storage;

/**
 * 上传完成后的元数据与公开访问地址。
 *
 * @param bucket    存储桶名称
 * @param objectKey 对象键
 * @param accessUrl 固定公开对象 URL
 */
public record StorageUploadResult(String bucket, String objectKey, String accessUrl) {
}
