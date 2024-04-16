package com.haiying.project.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 一般项目非立项
 * </p>
 *
 * @author 作者
 * @since 2022-11-21
 */
@Getter
@Setter
@TableName("small_project_no")
public class SmallProjectNo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String displayName;

    private String loginName;

    private Integer deptId;

    private String deptName;

    /**
     * 项目
     */
    private String name;

    /**
     * 任务号
     */
    private String taskCode;

    /**
     * 项目地点
     */
    private String location;

    /**
     * 项目性质
     */
    private String property;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDatetime;

    private String remark;

    private String wbs;

    private String projectRate;

    private String projectType;

    private String projectStatus;

    //
    private String projectLevel;

    private Integer customerId;
    private String customerName;
}
