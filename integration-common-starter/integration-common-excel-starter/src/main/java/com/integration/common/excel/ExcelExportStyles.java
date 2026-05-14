package com.integration.common.excel;

import com.alibaba.excel.write.builder.ExcelWriterBuilder;
import com.alibaba.excel.write.handler.SheetWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteWorkbookHolder;
import com.alibaba.excel.write.metadata.style.WriteCellStyle;
import com.alibaba.excel.write.metadata.style.WriteFont;
import com.alibaba.excel.write.style.HorizontalCellStyleStrategy;
import com.alibaba.excel.write.style.column.LongestMatchColumnWidthStyleStrategy;
import com.alibaba.excel.write.style.row.SimpleRowHeightStyleStrategy;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;

/**
 * 导出 xlsx 时的默认美化：表头底色与加粗、细边框、对齐、自动列宽、行高、冻结首行。
 */
final class ExcelExportStyles {

    private ExcelExportStyles() {}

    static void register(ExcelWriterBuilder writerBuilder) {
        writerBuilder
                .registerWriteHandler(new HorizontalCellStyleStrategy(headStyle(), contentStyle()))
                .registerWriteHandler(new LongestMatchColumnWidthStyleStrategy())
                .registerWriteHandler(new SimpleRowHeightStyleStrategy((short) 24, (short) 20))
                .registerWriteHandler(freezeHeaderRow());
    }

    private static WriteCellStyle headStyle() {
        WriteCellStyle style = new WriteCellStyle();
        style.setHorizontalAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPatternType(FillPatternType.SOLID_FOREGROUND);
        thinBorder(style);
        WriteFont font = new WriteFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 11);
        font.setColor(IndexedColors.BLACK.getIndex());
        style.setWriteFont(font);
        return style;
    }

    private static WriteCellStyle contentStyle() {
        WriteCellStyle style = new WriteCellStyle();
        style.setHorizontalAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setWrapped(true);
        thinBorder(style);
        WriteFont font = new WriteFont();
        font.setFontHeightInPoints((short) 10);
        style.setWriteFont(font);
        return style;
    }

    private static void thinBorder(WriteCellStyle style) {
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setLeftBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setRightBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
    }

    private static SheetWriteHandler freezeHeaderRow() {
        return new SheetWriteHandler() {
            @Override
            public void afterSheetCreate(
                    WriteWorkbookHolder writeWorkbookHolder, WriteSheetHolder writeSheetHolder) {
                writeSheetHolder.getSheet().createFreezePane(0, 1);
            }
        };
    }
}
