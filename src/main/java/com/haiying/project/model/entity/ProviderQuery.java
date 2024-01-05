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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 供方尽职调查
 * </p>
 *
 * @author 作者
 * @since 2022-04-09
 */
@Getter
@Setter
@TableName("provider_query")
public class ProviderQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer baseId;

    private Integer beforeId;

    private String haveDisplay;

    private Integer version;

    private String usee;

    private String type;

    private Integer providerId;

    private String name;

    private String property;

    /**
     * 考察时间
     */
    @JsonFormat(pattern = "yyyy-M-d")
    private LocalDate queryDate;

    /**
     * 企业规模
     */
    private String scale;

    /**
     * 近年主要业绩
     */
    private String achievement;

    /**
     * 质量管理现状
     */
    private String quality;

    /**
     * 生产检测条件
     */
    private String product;

    /**
     * 业绩现场考察
     */
    private String descc;

    /**
     * 后期服务
     */
    private String service;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDatetime;

    private String displayName;

    private String loginName;

    private Integer deptId;

    private String deptName;

    private String remark;

    private String result;

    private Integer score;

    private Integer processInstId;

    @TableField(exist = false)
    private ProcessInst processInst;

    @TableField(exist = false)
    private List<FileVO> fileList;

    //尽职调查部门
    @TableField(exist = false)
    private List<String> userNameeList;
    private String userNamee;
}
