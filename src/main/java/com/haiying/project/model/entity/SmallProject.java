package com.haiying.project.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
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
 * 一般项目立项
 * </p>
 *
 * @author 作者
 * @since 2022-03-28
 */
@Getter
@Setter
@TableName("small_project")
public class SmallProject implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;

    private Integer baseId;

    private Integer beforeId;

    private String haveDisplay;

    private Integer version;

    private String displayName;

    private String loginName;

    private Integer deptId;

    private String deptName;

    /**
     * 项目
     */
    private String name;

    private String wbs;

    /**
     * 任务号
     */
    private String taskCode;

    /**
     * 项目地点
     */
    private String location;

    /**
     * 项目性质
     */
    private String property;

    private Integer customerId;

    private String customerName;

    private Integer providerId;

    private String providerName;

    private String providerUsee;

    /**
     * 法人身份证类型
     */
    @TableField(exist = false)
    private List<String> idTypeListTmp;
    private String idType;

    /**
     * 预计签约金额
     */
    private Double expectMoney;

    /**
     * 预计签约日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expectDate;

    /**
     * 发票类型
     */
    private String invoiceType;

    /**
     * 发票税率
     */
    private String invoiceRate;

    /**
     * 项目毛利率
     */
    private String projectRate;

    /**
     * 是否投标
     */
    private String haveBid;

    /**
     * 投标截止日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate bidDate;

    /**
     * 开竣工日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate workDate;

    /**
     * 是否垫资
     */
    private String haveGiveMoney;

    /**
     * 垫资额度
     */
    private Double giveMoney;

    /**
     * 垫资周期
     */
    private String giveMoneyCycle;

    /**
     * 项目承揽历史
     */
    private String history1;

    /**
     * 是否存在纠纷
     */
    private String haveProblem1;

    /**
     * 以往项目付款情况
     */
    private String payStatus;

    /**
     * 甲方目前的资信及履约能力综合评价
     */
    private String evaluate1;

    /**
     * 战略伙伴方保证人
     */
    private String protectPerson;

    /**
     * 项目合作历史
     */
    private String history2;

    /**
     * 是否存在纠纷
     */
    private String haveProblem2;

    /**
     * 战略伙伴方目前的资信及综合能力综合评价
     */
    private String evaluate2;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDatetime;

    private Integer processInstId;

    @TableField(exist = false)
    private List<SmallProtect> list;

    @TableField(exist = false)
    private ProcessInst processInst;

    @TableField(exist = false)
    private List<FileVO> fileList;

    private String remark;
    //流程-财务部
    private String userNamee;

    private String projectType;

    private String projectStatus;
}
