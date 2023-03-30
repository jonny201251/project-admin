package com.haiying.project.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class CodeExcel {
    @ExcelProperty("名称")
    private String name;

    @ExcelProperty("甲乙方")
    private String c;

    @ExcelProperty("合作方")
    private String p;

    @ExcelProperty("部门")
    private String deptName;

    private Integer deptId;

    @ExcelProperty("金额")
    private Double money;

    @ExcelProperty("备案时间")
    private String aa;

    @ExcelProperty("任务号")
    private String taskCode;


    private String remark;
}
