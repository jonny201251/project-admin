package com.haiying.project.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class ProviderExcel {
    @ExcelProperty("公司名称")
    private String name;
    @ExcelProperty("评审")
    private String ps;
    @ExcelProperty("得分")
    private String score;
    @ExcelProperty("部门")
    private String deptName;
    private Integer deptId;
    @ExcelProperty("备注")
    private String remark;
}
