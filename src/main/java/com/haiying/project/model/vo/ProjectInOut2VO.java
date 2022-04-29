package com.haiying.project.model.vo;

import lombok.Data;

//项目收支表
@Data
public class ProjectInOut2VO {
    //日期、摘要
    private String inOutDate;
    private String remark;
    //往来款-不影响收支的金额、影响收支的金额
    private Double ioMoney1;
    private Double ioMoney2;
    //收款合同-开票金额、收款金额
    private Double inMoney1;
    private Double inMoney2;
    //付款合同-成本类型、付款金额
    private String costType;
    private Double outMoney2;

    private Double sort;

    //累计往来款
    private Double ioMoney2Total;
    //收款-累计开票、累计收款
    private Double inMoney1Total;
    private Double inMoney2Total;

    private Double outMoney1Total;//付款-累计开票
    private Double outMoney2Total;//付款-累计付款
    private Double deviceTotal;//材料及设备费
    private Double labourTotal;//劳务费
    private Double techTotal;//技术服务费
    private Double engTotal;//工程款
    private Double taxTotal;//税费
    private Double otherTotal;//其他费用
    private Double moreTotal;//项目结余
}
