package com.haiying.project.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.haiying.project.model.vo.FileVO;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 供方情况简表
 * </p>
 *
 * @author 作者
 * @since 2022-04-09
 */
@Getter
@Setter
@TableName("provider_simple")
public class ProviderSimple implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer baseId;

    private Integer beforeId;

    private String haveDisplay;

    private Integer version;

    /**
     * 供方用途
     */
    private String usee;

    private Integer providerId;

    private String name;

    private String property;

    /**
     * 注册地址
     */
    private String address;

    /**
     * 生产经营地址
     */
    private String address2;

    /**
     * 电话及区号
     */
    private String telephone;

    /**
     * 邮政编码
     */
    private String code;

    /**
     * 经济性质
     */
    private String economy;

    /**
     * 注册资本
     */
    private Double registerMoney;

    /**
     * 职工总数
     */
    private Integer workCount;

    /**
     * 高工人数
     */
    private Integer highCount;

    /**
     * 技术人数
     */
    private Integer techCount;

    /**
     * 助工人数
     */
    private Integer helpCount;

    /**
     * 机械设备总台(件)数
     */
    private String device1;

    /**
     * 检验仪器(表)总台(件)数
     */
    private String device2;

    /**
     * 相关企业资质情况
     */
    private String descc;

    /**
     * 主要经营范围
     */
    private String scope;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDatetime;

    private String displayName;

    private String loginName;

    private Integer deptId;

    private String deptName;

    private String remark;

    @TableField(exist = false)
    private List<FileVO> fileList;

    @TableField(exist = false)
    List<ProviderSimple2> list;
}
