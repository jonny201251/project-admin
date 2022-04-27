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
 * 项目收支-往来款inout
 * </p>
 *
 * @author 作者
 * @since 2022-04-27
 */
@Getter
@Setter
@TableName("project_io")
public class ProjectIo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer budgetId;

    private Integer projectId;

    /**
     * wbs编号
     */
    private String wbs;

    private String taskCode;

    private String name;

    private String property;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDatetime;

    private String remarkk;

    /**
     * 日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate ioDate;

    /**
     * 摘要
     */
    private String remark;

    /**
     * 金额
     */
    private Double money;

    private Integer providerId;

    private String providerName;

    /**
     * 是否影响收支
     */
    private String haveInOut;

    /**
     * 收款，付款
     */
    private String type2;

    private Integer sort;

    private String displayName;

    private String loginName;

    private Integer deptId;

    private String deptName;


}
