package com.haiying.project.model.vo;

import com.haiying.project.model.entity.ProjectIn2;
import com.haiying.project.model.entity.ProjectOut2;
import lombok.Data;

import java.util.List;

@Data
public class ProjectInOutVO {
    private String name;
    private String taskCode;
    private String property;
    private String wbs;
    private String customerName;
    private String contractCode;
    private Double contractMoney;
    private Double totalMoney;//成本预算
    private String rate1;//预计利润率
    private String rate2;//实时利润率
    private Double endMoney;//结算金额
    private String remark;
    private Double inTotal;//累计收款
    private Double outTotal;//累计付款
    private Double moreTotal;//项目结余
    private Double deviceTotal;//材料及设备费
    private Double labourTotal;//劳务费
    private Double techTotal;//技术服务费
    private Double engTotal;//工程款
    private Double taxTotal;//税费
    private Double otherTotal;//其他费用
    private List<ProjectIn2> in2List;//收入明细
    private List<ProjectOut2> out2List;//支出明细
}
