package com.haiying.project.model.vo;

import lombok.Data;

//一般项目预算调整表-纵向几列具体数据，收入明细+支出明细
@Data
public class SmallBudgetMoney4VO {
    //原预算额-a2
    private Double a2;
    //已签合同额-a4
    private Double a4;
    //截止xxxx年xx月已(收入/支出)额度-a5
    private Double a5;
}
