package com.haiying.project.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>
 * 一般项目预算-预计支出
 * </p>
 *
 * @author 作者
 * @since 2022-04-07
 */
@Getter
@Setter
@TableName("big_budget_out")
public class BigBudgetOut implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer budgetId;

    private Integer projectId;

    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 项目任务号
     */
    private String projectTaskCode;

    private String costType;

    private String costRate;

    private Double sort;

    private Integer companyId;

    private String companyName;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private String outDate;

    private Double money;

    private String remark;


}
