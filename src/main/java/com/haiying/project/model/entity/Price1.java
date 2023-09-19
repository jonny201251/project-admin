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
 * 采购方式-比价单
 * </p>
 *
 * @author 作者
 * @since 2023-08-18
 */
@Getter
@Setter
public class Price1 implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer inContractId;

    private String inContractName;

    private String inContractCode;

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

    private String descc;

    private Integer processInstId;

    //流程-归口管理部门
    private String userNamee;

    private String projectLevel;

    @TableField(exist = false)
    private List<Price11> list;

    @TableField(exist = false)
    private ProcessInst processInst;

    @TableField(exist = false)
    private List<FileVO> fileList;
}
