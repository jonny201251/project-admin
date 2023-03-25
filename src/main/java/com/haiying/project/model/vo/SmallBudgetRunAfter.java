package com.haiying.project.model.vo;

import com.haiying.project.model.entity.SmallBudgetRun;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class SmallBudgetRunAfter extends ProcessFormAfter {
    private SmallBudgetRun formValue;
}
