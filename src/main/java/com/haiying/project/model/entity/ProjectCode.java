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
import java.util.List;

/**
 * <p>
 * 一般和重大项目立项任务号
 * </p>
 *
 * @author 作者
 * @since 2022-03-30
 */
@Getter
@Setter
@TableName("project_code")
public class ProjectCode implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String displayName;

    private String loginName;

    private Integer deptId;

    private String deptName;

    /**
     * 部门类别
     */
    private String deptType;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDatetime;

    private String remark;

    /**
     * 项目名称
     */
    private String projectName;

    private Double projectMoney;
    private String projectLocation;

    /**
     * 项目性质
     */
    private String projectProperty;

    private Integer customerId;
    private String customerName;
    private String customerProperty;

    private Integer providerId;
    private String providerName;
    private String providerProperty;
    private String providerUsee;

    @TableField(exist = false)
    private List<String> businessTypeTmp;
    /**
     * 业务类别
     */
    private String businessType;

    /**
     * 任务号
     */
    private String taskCode;

    private String status;

    private Integer year;

    @TableField(exist = false)
    private String likeValue;
    //一个任务号是否有多个预算表
    private String haveMoreBudget;

}
