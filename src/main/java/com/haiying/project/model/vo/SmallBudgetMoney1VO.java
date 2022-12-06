package com.haiying.project.model.vo;

import lombok.Data;
//一般项目预算表-基本信息
@Data
public class SmallBudgetMoney1VO {
    //部门名称、项目名称、项目负责人、项目类型、项目任务号、type
    private String deptName;
    private String name;
    private String projectDisplayName;
    private String property;
    private String taskCode;
    private String type;
    //合同金额、成本总预算、开工时间、预计完工时间、质保金比例、预计毛利率
    private Double contractMoney;
    private Double totalCost;
    private String startDate;
    private String endDate;
    private String protectRate;
    private String projectRate;
    //结算金额、收入调整金额、支出调整金额、备注
    private String endMoney;
    private Double inChangeMoney;
    private Double outChangeMoney;
    private String invoiceRate;
    private String remark;
    //投标保证金、履约保证金、预付款担保、其他担保
    private String style1;
    private Double money1;
    private String outDate1;
    private String inDate1;
    private String style2;
    private Double money2;
    private String outDate2;
    private String inDate2;
    private String style3;
    private Double money3;
    private String outDate3;
    private String inDate3;
    private String style4;
    private Double money4;
    private String outDate4;
    private String inDate4;
}
