package com.example.manager.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import lombok.ToString;

/**
 * Excel导入导出DTO
 */
@Data
@ToString
public class ExcelDTO {
    @ExcelProperty("菜单名称")
    private String name;
}
