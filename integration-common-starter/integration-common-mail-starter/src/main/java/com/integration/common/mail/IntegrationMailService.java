package com.integration.common.mail;

import jakarta.mail.MessagingException;

import java.util.Collection;
import java.util.List;

/**
 * 基于 {@link org.springframework.mail.javamail.JavaMailSender} 的简易发信封装。
 * <p>需在应用中配置 {@code spring.mail.*}（至少能创建 {@code JavaMailSender}），并可选配置 {@code integration.mail.*}。
 */
public interface IntegrationMailService {

    /**
     * 发送纯文本邮件（单收件人）。
     *
     * @param to      收件人邮箱
     * @param subject 主题
     * @param text    正文
     */
    default void sendText(String to, String subject, String text) throws MessagingException {
        sendText(to, subject, text, List.of());
    }

    /**
     * 发送纯文本邮件（多收件人）。
     *
     * @param to      收件人邮箱，非空
     * @param subject 主题
     * @param text    正文
     */
    default void sendText(Collection<String> to, String subject, String text) throws MessagingException {
        sendText(to, subject, text, List.of());
    }

    /**
     * 发送纯文本邮件（单收件人），可带附件。
     *
     * @param to           收件人邮箱
     * @param subject      主题
     * @param text         正文
     * @param attachments  附件列表，可为空；非空时使用 MIME multipart
     */
    void sendText(String to, String subject, String text, Collection<MailAttachment> attachments)
            throws MessagingException;

    /**
     * 发送纯文本邮件（多收件人），可带附件。
     *
     * @param to           收件人邮箱，非空
     * @param subject      主题
     * @param text         正文
     * @param attachments  附件列表，可为空
     */
    void sendText(Collection<String> to, String subject, String text, Collection<MailAttachment> attachments)
            throws MessagingException;

    /**
     * 发送 HTML 邮件（单收件人）。
     *
     * @param to      收件人邮箱
     * @param subject 主题
     * @param html    HTML 正文
     */
    default void sendHtml(String to, String subject, String html) throws MessagingException {
        sendHtml(to, subject, html, List.of());
    }

    /**
     * 发送 HTML 邮件（单收件人），可带附件。
     *
     * @param to           收件人邮箱
     * @param subject      主题
     * @param html         HTML 正文
     * @param attachments  附件列表，可为空；非空时使用 MIME multipart
     */
    void sendHtml(String to, String subject, String html, Collection<MailAttachment> attachments)
            throws MessagingException;
}
