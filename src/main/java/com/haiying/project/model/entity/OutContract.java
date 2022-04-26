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
 * 付款合同
 * </p>
 *
 * @author 作者
 * @since 2022-04-21
 */
@Getter
@Setter
@TableName("out_contract")
public class OutContract implements Serializable {

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
     * 一般项目，重大项目
     */
    private String type;

    /**
     * 一般和重大项目的id
     */
    private Integer projectId;

    private String name;

    /**
     * 任务号
     */
    private String taskCode;

    private String property;

    private String accountType;

    private Integer providerId;

    private String providerName;

    /**
     * 合同号
     */
    private String contractCode;

    /**
     * 合同金额
     */
    private Double contractMoney;

    //结算金额
    private Double endMoney;

    /**
     * 合同名称
     */
    private String contractName;

    private String remark;

    private Integer processInstId;

    private String costType;

    private String costRate;

    @TableField(exist = false)
    private ProcessInst processInst;
}
