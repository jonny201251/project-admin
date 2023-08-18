package com.haiying.project.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

@Data
public class InContractExcel {
    @ExcelProperty("合同编号")
    @ColumnWidth(25)
    private String contractCode;
    @ExcelProperty("合同名称")
    @ColumnWidth(25)
    private String contractName;
    @ExcelProperty("对方名称")
    @ColumnWidth(25)
    private String customerName;
    @ExcelProperty("金额")
    @ColumnWidth(15)
    private Double contractMoney;
    @ExcelProperty("任务号")
    @ColumnWidth(15)
    private String taskCode;
    @ExcelProperty("经营方式")
    @ColumnWidth(15)
    private String property;
    @ExcelProperty("合同类别")
    @ColumnWidth(15)
    private String contractType;
    @ExcelProperty("合同级别")
    @ColumnWidth(15)
    private String contractLevel;
    @ExcelProperty("用印类别")
    @ColumnWidth(15)
    private String printType;

    @ExcelProperty("承办部门")
    @ColumnWidth(15)
    private String deptName;

    @ExcelProperty("用印日期")
    @ColumnWidth(15)
    private String printDate;
    @ExcelProperty("承办人")
    @ColumnWidth(15)
    private String displayName;
    @ExcelProperty("项目所在地")
    @ColumnWidth(15)
    private String location;
    @ExcelProperty("开工日期")
    @ColumnWidth(15)
    private String startDate;
    @ExcelProperty("竣工日期")
    @ColumnWidth(15)
    private String endDate;
    @ExcelProperty("签订日期")
    @ColumnWidth(15)
    private String expectDate;
    @ExcelProperty("归档日期")
    @ColumnWidth(15)
    private String documentDate;
    @ExcelProperty("备注")
    @ColumnWidth(25)
    private String remark;
}
