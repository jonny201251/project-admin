package com.haiying.project.model.vo;

import lombok.Data;

@Data
public class ProjectInOut2VO {
    //日期、摘要
    private String inOutDate;
    private String remark;
    //往来款-类型、影响收支、金额
    private String ioType;
    private String ioHave;
    private Double ioMoney;
    //收款合同-开票金额
    private Double inMoney1;
    //收款合同-收款金额
    private Double inMoney2;
    //付款合同-成本类型
    private String costType;
    //付款合同-付款金额
    private Double outMoney2;
    /*
        往来款小计
        收款-是-金额
        收款-否-金额
        付款-是-金额
        付款-否-金额
     */
    private Double ioInYesMoney;
    private Double ioInNoMoney;
    private Double ioOutYesMoney;
    private Double ioOutNoMoney;
    //
    private Double inTotal;//累计收款
    private Double outTotal;//累计付款
    private Double moreTotal;//项目结余
    private Double deviceTotal;//材料及设备费
    private Double labourTotal;//劳务费
    private Double techTotal;//技术服务费
    private Double engTotal;//工程款
    private Double taxTotal;//税费
    private Double otherTotal;//其他费用
}
