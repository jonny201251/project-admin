package com.haiying.project.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>
 * 用户
 * </p>
 *
 * @author 作者
 * @since 2022-02-15
 */
@Getter
@Setter
@TableName("sys_user")
public class SysUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String displayName;

    private String loginName;

    private String password;

    private String gender;

    private Double sort;

    private String remark;

    /**
     * 部门id
     */
    private Integer deptId;

    private String deptName;

    private String status;
    //职务，如部门领导
    private String position;
}
