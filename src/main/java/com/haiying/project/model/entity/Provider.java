package com.haiying.project.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.haiying.project.model.vo.FileVO;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

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

    private Integer baseId;

    private Integer beforeId;

    private String haveDisplay;

    private Integer version;

    //供方用途
    private String usee;
    private String type;
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

    private Integer deptId;

    private String deptName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDatetime;

    private String result;
    private Integer score;

    @TableField(exist = false)
    private List<FileVO> fileList;
}
