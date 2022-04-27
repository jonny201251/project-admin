package com.haiying.project.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * <p>
 * 项目收支-支出明细
 * </p>
 *
 * @author 作者
 * @since 2022-04-27
 */
@Getter
@Setter
@TableName("project_out")
public class ProjectOut implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer budgetId;

    private Integer projectId;

    private String taskCode;

    private String name;

    private String property;

    /**
     * 有无合同
     */
    private String haveContract;

    private Integer providerId;

    private String providerName;

    private String contractCode;

    private Double contractMoney;

    private Double endMoney;

    private String contractName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDatetime;

    /**
     * wbs编号
     */
    private String wbs;

    private String remarkk;

    /**
     * 费用类型
     */
    private String costType;

    private String costRate;

    /**
     * 日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate outDate;

    /**
     * 摘要
     */
    private String remark;

    /**
     * 开票金额
     */
    private Double money1;

    private Double money2;

    /**
     * 付款方式
     */
    private String outStyle;

    /**
     * 到期日
     */
    private String arriveDate;

    private Integer sort;

    private String displayName;

    private String loginName;

    private Integer deptId;

    private String deptName;


}
