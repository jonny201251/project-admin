package com.haiying.project.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>
 * 供方信息
 * </p>
 *
 * @author 作者
 * @since 2022-03-17
 */
@Getter
@Setter
public class Provider implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    //供方用途
    private String usee;
    /**
     * 供方名称
     */
    private String name;

    private String property;

    /**
     * 注册地址
     */
    private String address;

    /**
     * 纳税人识别号
     */
    private String code;

    /**
     * 注册资本
     */
    private Double registerMoney;

    /**
     * 实缴资本
     */
    private Double realMoney;

    private String remark;

    private String displayName;

    private String loginName;

    private String result;
}
