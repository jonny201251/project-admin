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
 * 采购方式-竞争性谈判表
 * </p>
 *
 * @author 作者
 * @since 2023-08-18
 */
@Getter
@Setter
public class Price2 implements Serializable {

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

    private Integer providerId;

    private String providerName;

    /**
     * 符合条款
     */
    private String limit;

    /**
     * 发票种类
     */
    private String invoiceType;

    /**
     * 税率
     */
    private String rate;

    /**
     * 预算控制价
     */
    private Double price1;

    /**
     * 预期谈判底价
     */
    private Double price2;

    /**
     * 最终谈判价格
     */
    private String price3;

    /**
     * 竞争优势
     */
    private String good;

    private String descc;

    private Integer processInstId;

    //流程-归口管理部门
    private String userNamee;

    @TableField(exist = false)
    private ProcessInst processInst;

    @TableField(exist = false)
    private List<FileVO> fileList;


}
