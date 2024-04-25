package com.haiying.project.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>
 * 无合同收款
 * </p>
 *
 * @author 作者
 * @since 2024-04-24
 */
@Getter
@Setter
@TableName("in_no_contract")
public class InNoContract implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

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

    private String remark;

    private Integer customerId;

    private String customerName;

    private Double contractMoney;


}
