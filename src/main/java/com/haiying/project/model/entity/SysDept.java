package com.haiying.project.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 部门
 * </p>
 *
 * @author 作者
 * @since 2022-02-15
 */
@Getter
@Setter
@TableName("sys_dept")
public class SysDept implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer pid;

    private String name;

    private Double sort;

    private String remark;

    //部门类别，用于项目任务号
    private String type;

    @TableField(exist = false)
    private List<SysDept> children;
}
