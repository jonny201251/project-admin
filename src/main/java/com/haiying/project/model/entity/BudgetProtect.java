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
 * 一般和重大项目预算-担保
 * </p>
 *
 * @author 作者
 * @since 2022-04-07
 */
@Getter
@Setter
@TableName("budget_protect")
public class BudgetProtect implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer budgetId;

    private Integer projectId;

    private String projectType;

    /**
     * 担保名称
     */
    private String name;

    /**
     * 形式
     */
    private String style;

    /**
     * 金额
     */
    private Double money;

    /**
     * 支出日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private String outDate;

    /**
     * 收回日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private String inDate;
}
