package com.haiying.project.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class BudgetExcel {
    @ExcelProperty("备案号")
    private String taskCode;
    @ExcelProperty("项目名称")
    private String name;
    @ExcelProperty("预计利润率")
    private String projectRate;
    @ExcelProperty("总成本")
    private Double totalMoney;

    @ExcelProperty("技术服务费税率1")
    private String rate1;
    @ExcelProperty("技术服务费金额1")
    private Double money1;

    @ExcelProperty("采购费税率")
    private String rate2;
    @ExcelProperty("采购费金额")
    private Double money2;

    @ExcelProperty("技术服务费税率")
    private String rate3;
    @ExcelProperty("技术服务费金额")
    private Double money3;

    @ExcelProperty("税费")
    private Double money4;
    @ExcelProperty("招待费")
    private Double money5;
    @ExcelProperty("差旅费")
    private Double money6;
    @ExcelProperty("交通费")
    private Double money7;
    @ExcelProperty("评审费")
    private Double money8;
    @ExcelProperty("其他")
    private Double money9;







}

