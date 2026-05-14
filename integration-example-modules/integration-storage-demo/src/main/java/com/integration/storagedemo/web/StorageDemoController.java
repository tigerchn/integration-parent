package com.integration.storagedemo.web;

import com.integration.common.core.api.ApiResult;
import com.integration.common.core.trace.TraceConstants;
import com.integration.common.storage.StorageService;
import com.integration.common.storage.StorageUploadResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.MDC;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 云存储联调：始终注册路由；未装配 {@link StorageService} 时上传/访问地址接口返回 503。
 */
@RestController
@RequestMapping("/api/storage-demo")
@Tag(name = "Storage demo")
public class StorageDemoController {

    private static final DateTimeFormatter DAY = DateTimeFormatter.BASIC_ISO_DATE;

    private static final String STORAGE_DISABLED_MSG =
            "Object storage is not enabled: set integration.storage.enabled=true, integration.storage.type=cos|oss, "
                    + "and fill integration.storage.cos.* or integration.storage.oss.* in application.yml.";

    private final ObjectProvider<StorageService> storageService;

    public StorageDemoController(ObjectProvider<StorageService> storageService) {
        this.storageService = storageService;
    }

    @GetMapping("/ping")
    @Operation(summary = "Health-style ping")
    public ApiResult<Map<String, Object>> ping() {
        boolean ready = storageService.getIfAvailable() != null;
        return ApiResult.ok(Map.of(
                "status", "UP",
                "storageReady", ready,
                "hint", ready ? "upload and access-url are available" : STORAGE_DISABLED_MSG));
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload file to COS or OSS (multipart)")
    public ResponseEntity<ApiResult<Map<String, String>>> upload(
            @RequestPart("file") MultipartFile file,
            @RequestParam(value = "prefix", required = false) String prefix) throws IOException {
        StorageService svc = storageService.getIfAvailable();
        if (svc == null) {
            return serviceUnavailable();
        }
        if (file.isEmpty()) {
            throw new IllegalArgumentException("file must not be empty");
        }
        String objectKey = buildObjectKey(prefix, file.getOriginalFilename());
        StorageUploadResult result = svc.upload(
                objectKey,
                file.getInputStream(),
                file.getSize(),
                file.getContentType());
        Map<String, String> body = new LinkedHashMap<>();
        body.put("bucket", result.bucket());
        body.put("objectKey", result.objectKey());
        body.put("accessUrl", result.accessUrl());
        return ResponseEntity.ok(ApiResult.ok(body));
    }

    @GetMapping("/access-url")
    @Operation(summary = "Build public object access URL (no expiry query)")
    public ResponseEntity<ApiResult<Map<String, String>>> accessUrl(@RequestParam("key") String key) {
        StorageService svc = storageService.getIfAvailable();
        if (svc == null) {
            return serviceUnavailable();
        }
        if (!StringUtils.hasText(key)) {
            throw new IllegalArgumentException("key must not be blank");
        }
        String url = svc.getObjectAccessUrl(key.trim());
        return ResponseEntity.ok(ApiResult.ok(Map.of("objectKey", key.trim(), "accessUrl", url)));
    }

    private ResponseEntity<ApiResult<Map<String, String>>> serviceUnavailable() {
        ApiResult<Map<String, String>> body = ApiResult.fail(HttpStatus.SERVICE_UNAVAILABLE.value(), STORAGE_DISABLED_MSG);
        attachTrace(body);
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(body);
    }

    private static void attachTrace(ApiResult<?> body) {
        String traceId = MDC.get(TraceConstants.TRACE_ID);
        if (traceId != null) {
            body.setTraceId(traceId);
        }
    }

    private static String buildObjectKey(String prefix, String originalFilename) {
        String day = LocalDate.now().format(DAY);
        String name = StringUtils.hasText(originalFilename) ? originalFilename : "file.bin";
        name = name.replace('\\', '_').replace('/', '_');
        String p = StringUtils.hasText(prefix) ? prefix.trim() : "demo/uploads";
        if (p.startsWith("/")) {
            p = p.substring(1);
        }
        if (!p.endsWith("/")) {
            p = p + "/";
        }
        return p + day + "/" + UUID.randomUUID() + "_" + name;
    }
}
