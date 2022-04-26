package com.haiying.project.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>
 * 重大项目预算-费用类型下的公司
 * </p>
 *
 * @author 作者
 * @since 2022-04-07
 */
@Getter
@Setter
@TableName("big_budget_company")
public class BigBudgetCompany implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer budgetId;

    private Integer projectId;

    /**
     * 项目名称
     */
    private String name;

    /**
     * 项目任务号
     */
    private String taskCode;

    private String costType;

    private String costRate;

    private Double sort;
    private String companyName;

    private String remark;


}
