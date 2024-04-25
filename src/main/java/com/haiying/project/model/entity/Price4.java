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
 * 采购方式-招标预案审批表
 * </p>
 *
 * @author 作者
 * @since 2024-04-25
 */
@Getter
@Setter
public class Price4 implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer inContractId;

    private String inContractName;

    private String inContractCode;

    /**
     * 一般项目，重大项目
     */
    private String projectName;

    /**
     * 任务号
     */
    private String taskCode;

    private String displayName;

    private String loginName;

    private Integer deptId;

    private String deptName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDatetime;

    private String remark;

    /**
     * 项目类别
     */
    private String projectType;

    /**
     * 合同类别
     */
    private String contractType;

    private Integer processInstId;

    /**
     * 项目密级
     */
    private String projectLevel;

    private String content;

    private Double money;

    private String style;

    private String descc;

    @TableField(exist = false)
    private List<String> requestList;
    private String request;

    private Integer providerId;

    private String providerName;

    private String method;

    private String plan;

    //流程-归口管理部门
    private String userNamee;

    @TableField(exist = false)
    private ProcessInst processInst;

    @TableField(exist = false)
    private List<FileVO> fileList;
}
