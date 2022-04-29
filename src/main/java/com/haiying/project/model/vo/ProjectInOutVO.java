package com.haiying.project.model.vo;

import lombok.Data;

@Data
public class ProjectInOutVO {
    private Integer id;
    private String name;
    private String taskCode;
    private String property;
    private String wbs;
    private String contractCode;
    private Double inMoneyTotal;//收款额
    private Double costMoneyTotal;//成本预算
    private String rate1;//预计利润率
    private String rate2;//实时利润率
    private Double endMoney;//结算金额
    private String deptName;
}
