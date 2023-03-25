package com.haiying.project.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 一般和重大项目预算-预计收入
 * </p>
 *
 * @author 作者
 * @since 2022-04-07
 */
@Getter
@Setter
@TableName("budget_in")
public class BudgetIn implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String haveDisplay;

    private Integer version;

    private Integer budgetId;

    private Integer projectId;

    private String projectType;

    /**
     * 项目名称
     */
    private String name;

    /**
     * 项目任务号
     */
    private String taskCode;

    /**
     * 收入类型
     */
    private String inType;

    /**
     * 预计回款日期
     */
    private String inDate;

    //年月，生成预算表时排序
    @TableField(exist = false)
    private Integer inDateInt;

    /**
     * 金额
     */
    private Double money;

    private Double sort;

    private String remark;

    private String displayName;

    private String loginName;

    private Integer deptId;

    private String deptName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDatetime;

    @TableField(exist = false)
    private ProcessInst processInst;

}
