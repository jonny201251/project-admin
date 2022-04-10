package com.haiying.project.model.vo;

import com.haiying.project.model.entity.BigBudgetCompany;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class BigBudgetCompanyVO extends BigBudgetCompany {
    private List<BigBudgetCompany> list;
}
