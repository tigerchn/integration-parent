package com.integration.common.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExcelHelperTest {

    public static class SampleRow {
        @ExcelProperty("名称")
        private String name;
        @ExcelProperty("数量")
        private Integer qty;

        public SampleRow() {}

        public SampleRow(String name, Integer qty) {
            this.name = name;
            this.qty = qty;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getQty() {
            return qty;
        }

        public void setQty(Integer qty) {
            this.qty = qty;
        }
    }

    @Test
    void writeThenReadRoundTrip() throws IOException {
        ExcelHelper helper = new ExcelHelper();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        helper.write(out, SampleRow.class, "测试", List.of(new SampleRow("A", 1), new SampleRow("B", 2)));

        List<SampleRow> rows;
        try (ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray())) {
            rows = helper.read(in, SampleRow.class);
        }

        assertThat(rows).hasSize(2);
        assertThat(rows.get(0).getName()).isEqualTo("A");
        assertThat(rows.get(0).getQty()).isEqualTo(1);
        assertThat(rows.get(1).getName()).isEqualTo("B");
        assertThat(rows.get(1).getQty()).isEqualTo(2);
    }
}
