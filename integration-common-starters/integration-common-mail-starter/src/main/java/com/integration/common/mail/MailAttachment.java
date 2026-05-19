package com.integration.common.mail;

import java.util.Objects;

/**
 * 邮件附件：二进制内容与展示文件名。
 *
 * @param filename    MIME 中的文件名，非空
 * @param content     文件字节内容
 * @param contentType 可选 MIME 类型，为空时由底层使用默认类型
 */
public record MailAttachment(String filename, byte[] content, String contentType) {

    /**
     * 无显式 {@code Content-Type} 的附件（由客户端/传输层采用默认类型）。
     */
    public MailAttachment(String filename, byte[] content) {
        this(filename, content, null);
    }

    public MailAttachment {
        Objects.requireNonNull(filename, "filename");
        if (filename.isBlank()) {
            throw new IllegalArgumentException("filename must not be blank");
        }
        Objects.requireNonNull(content, "content");
    }
}
