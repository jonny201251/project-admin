package com.haiying.project.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProviderrExcel {
    @ColumnWidth(15)
    @ExcelProperty("项目类别")
    private String usee;
    @ColumnWidth(15)
    @ExcelProperty("项目大小")
    private String type;
    @ColumnWidth(40)
    @ExcelProperty("供方名称")
    private String name;
    @ColumnWidth(20)
    @ExcelProperty("供方企业性质")
    private String property;
    @ColumnWidth(50)
    @ExcelProperty("注册地址")
    private String address;
    @ColumnWidth(30)
    @ExcelProperty("纳税人识别号")
    private String code;
    @ExcelProperty("注册资本")
    private Double registerMoney;
    @ExcelProperty("实缴资本")
    private Double realMoney;
    @ExcelProperty("结论")
    private String result;
    @ExcelProperty("得分")
    private Integer score;
    @ExcelProperty("创建人")
    private String loginName;
    @ColumnWidth(20)
    @ExcelProperty("创建部门")
    private String deptName;
    @ColumnWidth(20)
    @ExcelProperty("创建时间")
    private LocalDateTime createDatetime;
    @ColumnWidth(50)
    @ExcelProperty("备注")
    private String remark;
}
