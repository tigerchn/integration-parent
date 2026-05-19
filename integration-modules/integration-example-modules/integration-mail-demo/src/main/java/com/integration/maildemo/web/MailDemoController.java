package com.integration.maildemo.web;

import com.integration.common.core.api.ApiResult;
import com.integration.common.mail.IntegrationMailService;
import com.integration.common.mail.MailAttachment;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

/**
 * 邮件发送联调接口（需可连 {@code spring.mail.*} 指向的 SMTP）。
 */
@RestController
@RequestMapping("/api/mail-demo")
@Tag(name = "Mail demo")
public class MailDemoController {

    private final IntegrationMailService integrationMailService;

    public MailDemoController(IntegrationMailService integrationMailService) {
        this.integrationMailService = integrationMailService;
    }

    @GetMapping("/ping")
    @Operation(summary = "Health-style ping")
    public ApiResult<Map<String, String>> ping() {
        return ApiResult.ok(Map.of("status", "UP"));
    }

    /**
     * @param body 收件人、主题、纯文本正文；可选 {@code attachments}（Base64）
     */
    @PostMapping("/text")
    @Operation(summary = "Send plain text email (optional JSON attachments as base64)")
    public ApiResult<Void> sendText(@Valid @RequestBody TextMailRequest body) throws MessagingException {
        integrationMailService.sendText(body.to(), body.subject(), body.text(), fromJsonParts(body.attachments()));
        return ApiResult.ok();
    }

    /**
     * @param body 收件人、主题、HTML 正文；可选 {@code attachments}（Base64）
     */
    @PostMapping("/html")
    @Operation(summary = "Send HTML email (optional JSON attachments as base64)")
    public ApiResult<Void> sendHtml(@Valid @RequestBody HtmlMailRequest body) throws MessagingException {
        integrationMailService.sendHtml(body.to(), body.subject(), body.html(), fromJsonParts(body.attachments()));
        return ApiResult.ok();
    }

    /**
     * @param to      收件人
     * @param subject 主题
     * @param text    正文
     * @param files   可选，多个文件字段名均为 {@code files}
     */
    @PostMapping(value = "/text-multipart", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Send plain text email with multipart file attachments")
    public ApiResult<Void> sendTextMultipart(
            @RequestPart("to") @NotBlank String to,
            @RequestPart("subject") @NotBlank String subject,
            @RequestPart("text") @NotBlank String text,
            @RequestPart(value = "files", required = false) List<MultipartFile> files)
            throws MessagingException, IOException {
        integrationMailService.sendText(to, subject, text, fromMultipartFiles(files));
        return ApiResult.ok();
    }

    /**
     * @param to      收件人
     * @param subject 主题
     * @param html    HTML 正文
     * @param files   可选，多个文件字段名均为 {@code files}
     */
    @PostMapping(value = "/html-multipart", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Send HTML email with multipart file attachments")
    public ApiResult<Void> sendHtmlMultipart(
            @RequestPart("to") @NotBlank String to,
            @RequestPart("subject") @NotBlank String subject,
            @RequestPart("html") @NotBlank String html,
            @RequestPart(value = "files", required = false) List<MultipartFile> files)
            throws MessagingException, IOException {
        integrationMailService.sendHtml(to, subject, html, fromMultipartFiles(files));
        return ApiResult.ok();
    }

    private static List<MailAttachment> fromJsonParts(List<AttachmentPart> parts) {
        if (parts == null || parts.isEmpty()) {
            return List.of();
        }
        List<MailAttachment> list = new ArrayList<>();
        for (AttachmentPart p : parts) {
            byte[] bytes;
            try {
                bytes = Base64.getDecoder().decode(p.contentBase64());
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException("Invalid base64 for attachment filename=" + p.filename(), ex);
            }
            if (StringUtils.hasText(p.contentType())) {
                list.add(new MailAttachment(p.filename(), bytes, p.contentType().trim()));
            } else {
                list.add(new MailAttachment(p.filename(), bytes));
            }
        }
        return list;
    }

    private static List<MailAttachment> fromMultipartFiles(List<MultipartFile> files) throws IOException {
        if (files == null || files.isEmpty()) {
            return List.of();
        }
        List<MailAttachment> list = new ArrayList<>();
        for (MultipartFile file : files) {
            if (file == null || file.isEmpty()) {
                continue;
            }
            String name = StringUtils.hasText(file.getOriginalFilename()) ? file.getOriginalFilename() : "attachment";
            String ct = file.getContentType();
            if (StringUtils.hasText(ct)) {
                list.add(new MailAttachment(name, file.getBytes(), ct));
            } else {
                list.add(new MailAttachment(name, file.getBytes()));
            }
        }
        return list;
    }

    public record TextMailRequest(
            @NotBlank String to,
            @NotBlank String subject,
            @NotBlank String text,
            List<AttachmentPart> attachments
    ) {
    }

    public record HtmlMailRequest(
            @NotBlank String to,
            @NotBlank String subject,
            @NotBlank String html,
            List<AttachmentPart> attachments
    ) {
    }

    /**
     * @param filename        附件文件名
     * @param contentBase64   附件内容的 Base64
     * @param contentType     可选 MIME，如 {@code application/pdf}
     */
    public record AttachmentPart(
            @NotBlank String filename,
            @NotBlank String contentBase64,
            String contentType
    ) {
    }
}
