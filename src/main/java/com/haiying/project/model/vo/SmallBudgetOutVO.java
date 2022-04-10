package com.haiying.project.model.vo;

import com.haiying.project.model.entity.SmallBudgetOut;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class SmallBudgetOutVO extends SmallBudgetOut {
    private List<SmallBudgetOut> list;
}
