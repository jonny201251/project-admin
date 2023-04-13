package com.haiying.project.model.vo;

import com.haiying.project.model.entity.BudgetProjectt;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class BudgetProjecttAfter extends ProcessFormAfter {
    private BudgetProjectt formValue;
}
