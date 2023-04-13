package com.haiying.project.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class ProjectExcel {
    @ExcelProperty("部门")
    private String deptName;
    private Integer deptId;
    @ExcelProperty("项目类别")
    private String projectType;
    @ExcelProperty("任务号")
    private String taskCode;
    @ExcelProperty("项目名称")
    private String name;
    @ExcelProperty("项目性质")
    private String property;
    @ExcelProperty("客户")
    private String customerName;
    private Integer customerId;
    @ExcelProperty("供方")
    private String providerName;
    private Integer providerId;

    @ExcelProperty("合同号")
    private String contractCode;

    @ExcelProperty("合同金额")
    private Double contractMoney;
    @ExcelProperty("预计毛利率")
    private String projectRate;
    @ExcelProperty("成本总预算")
    private Double totalCost;

    @ExcelProperty("材料及设备费13%")
    private Double a1;
    @ExcelProperty("材料及设备费3%")
    private Double a2;
    @ExcelProperty("材料及设备费1%")
    private Double a3;
    @ExcelProperty("材料及设备费0%")
    private Double a4;
    @ExcelProperty("技术服务费6%")
    private Double b1;
    @ExcelProperty("技术服务费3%")
    private Double b2;
    @ExcelProperty("劳务费6%")
    private Double c1;
    @ExcelProperty("劳务费3%")
    private Double c2;
    @ExcelProperty("劳务费1%")
    private Double c3;
    @ExcelProperty("劳务费0%")
    private Double c4;

    @ExcelProperty("工程款9%")
    private Double d1;
    @ExcelProperty("工程款3%")
    private Double d2;

    @ExcelProperty("税费")
    private Double e;
    @ExcelProperty("交易服务费")
    private Double f;
    @ExcelProperty("差旅费")
    private Double g;
    @ExcelProperty("交通费")
    private Double h;

    @ExcelProperty("其他-招待费")
    private Double j1;
    @ExcelProperty("其他-专家费用")
    private Double j2;
    @ExcelProperty("其他-锁证费")
    private Double j3;
    @ExcelProperty("其他-资金成本")
    private Double j4;
    @ExcelProperty("其他-其他1")
    private Double j5;
    @ExcelProperty("其他-其他2")
    private Double j6;
    @ExcelProperty("其他-其他3")
    private Double j7;
    @ExcelProperty("其他-其他4")
    private Double j8;
    @ExcelProperty("其他-其他5")
    private Double j9;


}
