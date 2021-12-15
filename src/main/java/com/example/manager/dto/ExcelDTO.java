package com.example.manager.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;
import lombok.ToString;

/**
 * Excel导入导出DTO
 */
@Data
@ToString
public class ExcelDTO {
    @ExcelProperty("菜单名称")
    @ColumnWidth(20)
    private String resuName;

    @ExcelProperty("菜单编号")
    @ColumnWidth(20)
    private String resuCodg;

    @ExcelProperty("子系统")
    @ColumnWidth(20)
    private String subsysName;

    @ExcelProperty("菜单路径")
    @ColumnWidth(50)
    private String resuPath;

    @ExcelProperty("父级名称")
    @ColumnWidth(20)
    private String prntResuName;

    @ExcelProperty("API路径，多个时回车分隔")
    @ColumnWidth(50)
    private String api;
}
