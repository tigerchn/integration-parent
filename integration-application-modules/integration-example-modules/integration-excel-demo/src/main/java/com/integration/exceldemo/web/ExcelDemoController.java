package com.integration.exceldemo.web;

import com.alibaba.excel.exception.ExcelAnalysisException;
import com.integration.common.core.api.ApiResult;
import com.integration.common.excel.ExcelHelper;
import com.integration.exceldemo.model.DemoUserExcelRow;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Excel 导入：multipart 上传 xlsx，解析为 {@link DemoUserExcelRow} 列表。<br>
 * Excel 导出：请求体为 JSON 数组，下载 xlsx。
 */
@RestController
@RequestMapping("/api/excel-demo")
public class ExcelDemoController {

    private static final String SHEET = "用户";

    private final ExcelHelper excelHelper;

    public ExcelDemoController(ExcelHelper excelHelper) {
        this.excelHelper = excelHelper;
    }

    /**
     * 上传 xlsx，将首个 Sheet 解析为 {@link DemoUserExcelRow}。
     *
     * @param file 表单字段名 {@code file}
     */
    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResult<List<DemoUserExcelRow>> importExcel(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("请上传非空的 xlsx 文件（字段名 file）");
        }
        try (InputStream in = file.getInputStream()) {
            return ApiResult.ok(excelHelper.read(in, DemoUserExcelRow.class));
        } catch (ExcelAnalysisException ex) {
            throw new IllegalArgumentException("Excel 解析失败: " + ex.getMessage(), ex);
        } catch (IOException ex) {
            throw new IllegalArgumentException("读取上传文件失败: " + ex.getMessage(), ex);
        }
    }

    /**
     * 将 JSON 数组导出为 xlsx（表头与 {@link DemoUserExcelRow} 注解一致）。
     *
     * @param rows 实体列表，可为空（仅表头）
     */
    @PostMapping(value = "/export", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<byte[]> exportExcel(@RequestBody List<DemoUserExcelRow> rows) {
        List<DemoUserExcelRow> safe = rows != null ? rows : List.of();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        excelHelper.write(out, DemoUserExcelRow.class, SHEET, safe);
        byte[] body = out.toByteArray();
        String filename = URLEncoder.encode("用户导出.xlsx", StandardCharsets.UTF_8).replace("+", "%20");
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename*=UTF-8''" + filename)
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(body);
    }

    /**
     * 下载内置示例数据，便于未准备文件时快速验证导出格式。
     */
    @GetMapping("/export/sample")
    public ResponseEntity<byte[]> exportSample() {
        List<DemoUserExcelRow> sample = new ArrayList<>();
        DemoUserExcelRow r1 = new DemoUserExcelRow();
        r1.setUsername("张三");
        r1.setAge(28);
        r1.setEmail("zhangsan@example.com");
        sample.add(r1);
        DemoUserExcelRow r2 = new DemoUserExcelRow();
        r2.setUsername("李四");
        r2.setAge(35);
        r2.setEmail("lisi@example.com");
        sample.add(r2);
        return exportExcel(sample);
    }
}
