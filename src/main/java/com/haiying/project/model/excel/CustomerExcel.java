package com.haiying.project.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class CustomerExcel {
    @ExcelProperty("名称")
    private String name;

    @ExcelProperty("得分")
    private String score;

    @ExcelProperty("结论")
    private String result;

    @ExcelProperty("部门")
    private String deptName;

    private Integer deptId;



    private String remark;
}
