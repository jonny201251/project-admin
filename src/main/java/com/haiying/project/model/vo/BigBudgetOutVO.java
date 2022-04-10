package com.haiying.project.model.vo;

import com.haiying.project.model.entity.BigBudgetOut;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class BigBudgetOutVO extends BigBudgetOut {
    private List<BigBudgetOut> list;
}
