package com.haiying.project.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>
 * 一般和重大项目预算-预计收入
 * </p>
 *
 * @author 作者
 * @since 2023-03-23
 */
@Getter
@Setter
@TableName("budget_in2")
public class BudgetIn2 implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer budgetInId;

    /**
     * 预计回款日期
     */
    @JsonFormat(pattern = "yyyy-M-d")
    private String inDate;

    //年月，生成预算表时排序
    @TableField(exist = false)
    private Integer inDateInt;

    /**
     * 金额
     */
    private Double money;

    private Double sort;


}
