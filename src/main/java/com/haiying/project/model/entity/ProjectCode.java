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

    /**
     * 项目性质
     */
    private String projectProperty;

    /**
     * 客户企业性质
     */
    private String customerProperty;

    /**
     * 战略伙伴企业性质
     */
    private String providerProperty;

    /**
     * 客户名称
     */
    private String customerName;

    private String providerName;
    @TableField(exist = false)
    private List<String> businessTypeList;
    /**
     * 业务类别
     */
    private String businessType;

    /**
     * 任务号
     */
    private String taskCode;

    private String status;

}
