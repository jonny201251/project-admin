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
 * 一般项目预算-预计支出
 * </p>
 *
 * @author 作者
 * @since 2022-04-02
 */
@Getter
@Setter
@TableName("small_budget_out")
public class SmallBudgetOut implements Serializable {

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

    private String wbs;

    /**
     * 项目任务号
     */
    private String taskCode;

    private String costType;

    private String costRate;

    private Double sort;

    private String outDate;

    //年月，生成预算表时排序
    @TableField(exist = false)
    private Integer outDateInt;

    private Double money;

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
