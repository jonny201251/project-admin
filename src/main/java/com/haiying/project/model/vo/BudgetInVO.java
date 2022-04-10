package com.haiying.project.model.vo;

import com.haiying.project.model.entity.BudgetIn;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class BudgetInVO extends BudgetIn {
    private List<BudgetIn> list;
}
