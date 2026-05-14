package com.integration.exceldemo.model;

import com.alibaba.excel.annotation.ExcelProperty;

/**
 * 演示用 Excel 行模型：表头须与 {@link ExcelProperty#value()} 一致。
 */
public class DemoUserExcelRow {

    @ExcelProperty("用户姓名")
    private String username;

    @ExcelProperty("年龄")
    private Integer age;

    @ExcelProperty("邮箱")
    private String email;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
