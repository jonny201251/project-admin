package com.haiying.project.model.vo;

import lombok.Data;

//收支明细表
@Data
public class ProjectInOut3VO {
    //类型1
    private String type1;
    //类型2
    private String type2;
    //合同编号
    private String contractCode;
    //相对方名称
    private String name;
    //合同金额
    private Double contractMoney;
    //结算金额
    private String endMoney;
    //税率
    private String rate;
    //备注
    private String remarkk;

    //日期、摘要
    private String inOutDate;
    private String remark;
    //往来款-不影响收支的金额、影响收支的金额
    private Double ioMoney1;
    private Double ioMoney2;
    //开票、收付款方式、收付款金额、到期日
    private Double inOutmoney1;
    private String inOutStyle;
    private Double inOutmoney2;
    private String arriveDate;
    //累计收支额
    private Double inOutmoney2Total;

}
