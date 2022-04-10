package com.haiying.project.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>
 * 数据字典
 * </p>
 *
 * @author 作者
 * @since 2021-12-27
 */
@Getter
@Setter
@TableName("sys_dic")
public class SysDic implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 类别
     */
    private String type;

    /**
     * 名称
     */
    private String name;

    /**
     * 排序
     */
    private Double sort;

    /**
     * 备注
     */
    private String remark;


}
