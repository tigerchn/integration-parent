package com.integration.common.excel;

import com.alibaba.excel.EasyExcel;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;

/**
 * 基于 EasyExcel 的 xlsx 读写：表头行与实体字段通过 {@link com.alibaba.excel.annotation.ExcelProperty} 映射。
 * <p>默认读取<strong>第一个</strong> Sheet；导出为单个 Sheet。
 */
public class ExcelHelper {

    /**
     * 将首个 Sheet 解析为实体列表（首行为表头）。
     *
     * @param inputStream  xlsx 输入流（调用方负责关闭）
     * @param entityClass 带 {@code ExcelProperty} 的实体类型
     * @param <T>          行类型
     * @return 行数据列表（可能为空，不含表头行）
     */
    public <T> List<T> read(InputStream inputStream, Class<T> entityClass) {
        return EasyExcel.read(inputStream).head(entityClass).sheet().doReadSync();
    }

    /**
     * 将实体集合写入 xlsx（自动生成表头）。
     * <p>导出时附带默认样式：表头灰底加粗居中、内容区左对齐与自动换行、细边框、自动列宽、行高与冻结首行。
     *
     * @param outputStream  xlsx 输出流（调用方负责关闭）
     * @param entityClass   表头定义来源
     * @param sheetName     工作表名称
     * @param rows          数据行（可为空，仅输出表头）
     * @param <T>           行类型
     */
    public <T> void write(OutputStream outputStream, Class<T> entityClass, String sheetName, Collection<T> rows) {
        var writerBuilder = EasyExcel.write(outputStream, entityClass);
        ExcelExportStyles.register(writerBuilder);
        writerBuilder.sheet(sheetName).doWrite(rows);
    }
}
