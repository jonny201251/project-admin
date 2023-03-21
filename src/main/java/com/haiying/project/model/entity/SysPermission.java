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
 * 权限表
 * </p>
 *
 * @author 作者
 * @since 2023-01-12
 */
@Getter
@Setter
@TableName("sys_permission")
public class SysPermission implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 父级节点
     */
    private Integer pid;

    /**
     * 类型,菜单，叶子菜单，按钮
     */
    private String type;

    /**
     * 按钮类型,add,edit,delete,preview
     */
    private String buttonType;

    /**
     * 按钮位置,工具条，操作
     */
    private String buttonPosition;

    /**
     * 名称
     */
    private String name;

    /**
     * 前端path
     */
    private String path;

    /**
     * 图标名称
     */
    private String icon;

    /**
     * 排序
     */
    private Double sort;

    /**
     * 备注
     */
    private String remark;

    @TableField(exist = false)
    private List<SysPermission> children;

}
