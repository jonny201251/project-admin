package com.haiying.project.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 一般和重大项目预算-项目
 * </p>
 *
 * @author 作者
 * @since 2022-04-07
 */
@Getter
@Setter
@TableName("budget_project")
public class BudgetProject implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer baseId;

    private Integer beforeId;

    private String haveDisplay;

    private Integer version;

    private String displayName;

    private String loginName;

    private Integer deptId;

    private String deptName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDatetime;

    /**
     * 一般和重大项目的id
     */
    private Integer projectId;

    private String projectType;

    private String name;

    private String wbs;

    /**
     * 任务号
     */
    private String taskCode;

    private String property;

    private Integer customerId;

    private String customerName;

    /**
     * 合同编号
     */
    private String contractCode;

    /**
     * 合同金额
     */
    private Double contractMoney;

    /**
     * 合同名称
     */
    private String contractName;

    /**
     * 成本总预算
     */
    private Double totalCost;

    /**
     * 开工时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    /**
     * 预计完工时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    /**
     * 质保金比例
     */
    private String protectRate;

    /**
     * 税率
     */
    private String invoiceRate;

    //预计毛利率
    private String projectRatee;
    /**
     * 实时毛利率
     */
    private String projectRate;

    /**
     * 结算金额
     */
    private Double endMoney;

    /**
     * 收入调整金额
     */
    private Double inChangeMoney;

    /**
     * 支出调整金额
     */
    private Double outChangeMoney;

    private String remark;

    /**
     * 项目负责人
     */
    private String projectDisplayName;

    private String projectLoginName;

    @TableField(exist = false)
    private List<BudgetProtect> list;

    @TableField(exist = false)
    private ProcessInst processInst;
}
