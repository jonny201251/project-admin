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
 * 项目收支-支出明细
 * </p>
 *
 * @author 作者
 * @since 2022-04-07
 */
@Getter
@Setter
@TableName("project_out1")
public class ProjectOut1 implements Serializable {

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

    private String contractName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDatetime;

    /**
     * wbs编号
     */
    private String wbs;

    private String remark;

    @TableField(exist = false)
    private List<ProjectOut2> list;
}
