package com.haiying.project.model.vo;

import lombok.Data;

//一般项目预算表-纵向一列具体数据，收入明细+支出明细
@Data
public class SmallBudgetMoney3VO {
    //收支月份
    private String ioMonth;
    //项目收入、其他、材料费
    private String type;

    private String rate;

    private Double money;

    private Integer index;
}
