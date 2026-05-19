package com.integration.common.mail.support;

import com.integration.common.mail.IntegrationMailService;
import com.integration.common.mail.MailAttachment;
import com.integration.common.mail.autoconfigure.MailIntegrationProperties;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.Collection;

/**
 * 默认实现：解析发件人后通过 {@link JavaMailSender} 发送。
 */
public class DefaultIntegrationMailService implements IntegrationMailService {

    private static final String UTF_8 = StandardCharsets.UTF_8.name();

    private final JavaMailSender javaMailSender;
    private final MailIntegrationProperties integrationProperties;
    private final MailProperties springMailProperties;

    /**
     * @param javaMailSender          Spring 提供的邮件发送器
     * @param integrationProperties   {@code integration.mail.*}
     * @param springMailProperties    可为 {@code null}；存在时用于解析默认 {@code From}
     */
    public DefaultIntegrationMailService(JavaMailSender javaMailSender,
                                         MailIntegrationProperties integrationProperties,
                                         MailProperties springMailProperties) {
        this.javaMailSender = javaMailSender;
        this.integrationProperties = integrationProperties;
        this.springMailProperties = springMailProperties;
    }

    @Override
    public void sendText(String to, String subject, String text, Collection<MailAttachment> attachments)
            throws MessagingException {
        sendText(java.util.List.of(to), subject, text, attachments);
    }

    @Override
    public void sendText(Collection<String> to, String subject, String text,
                         Collection<MailAttachment> attachments) throws MessagingException {
        if (to == null || to.isEmpty()) {
            throw new IllegalArgumentException("recipients must not be empty");
        }
        Collection<MailAttachment> safeAttachments = attachments != null ? attachments : java.util.List.of();
        boolean multipart = !safeAttachments.isEmpty();
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, multipart, UTF_8);
        applyFrom(helper);
        helper.setTo(to.toArray(String[]::new));
        helper.setSubject(subject);
        helper.setText(text, false);
        addAttachments(helper, safeAttachments);
        javaMailSender.send(mimeMessage);
    }

    @Override
    public void sendHtml(String to, String subject, String html, Collection<MailAttachment> attachments)
            throws MessagingException {
        if (!StringUtils.hasText(to)) {
            throw new IllegalArgumentException("recipient must not be blank");
        }
        Collection<MailAttachment> safeAttachments = attachments != null ? attachments : java.util.List.of();
        boolean multipart = !safeAttachments.isEmpty();
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, multipart, UTF_8);
        applyFrom(helper);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(html, true);
        addAttachments(helper, safeAttachments);
        javaMailSender.send(mimeMessage);
    }

    private void addAttachments(MimeMessageHelper helper, Collection<MailAttachment> attachments)
            throws MessagingException {
        for (MailAttachment att : attachments) {
            ByteArrayResource resource = new ByteArrayResource(att.content());
            if (StringUtils.hasText(att.contentType())) {
                helper.addAttachment(att.filename(), resource, att.contentType());
            } else {
                helper.addAttachment(att.filename(), resource);
            }
        }
    }

    private void applyFrom(MimeMessageHelper helper) throws MessagingException {
        String address = resolveFromAddress();
        String display = integrationProperties.getFromDisplayName();
        try {
            if (StringUtils.hasText(display)) {
                helper.setFrom(new InternetAddress(address, display, UTF_8));
            } else {
                helper.setFrom(address);
            }
        } catch (java.io.UnsupportedEncodingException e) {
            throw new IllegalStateException("UTF-8 not supported for mail display name", e);
        }
    }

    private String resolveFromAddress() {
        if (StringUtils.hasText(integrationProperties.getFromAddress())) {
            return integrationProperties.getFromAddress().trim();
        }
        if (springMailProperties != null) {
            String username = springMailProperties.getUsername();
            if (StringUtils.hasText(username) && username.contains("@")) {
                return username.trim();
            }
        }
        throw new IllegalStateException(
                "No sender address: set integration.mail.from-address, "
                        + "or use spring.mail.username as a full email address.");
    }
}
