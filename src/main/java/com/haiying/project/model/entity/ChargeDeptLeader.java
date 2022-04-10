package com.haiying.project.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>
 * 主管部门领导
 * </p>
 *
 * @author 作者
 * @since 2022-02-28
 */
@Getter
@Setter
@TableName("charge_dept_leader")
public class ChargeDeptLeader implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 姓名
     */
    private String loginName;

    /**
     * 部门id
     */
    private Integer deptId;


}
