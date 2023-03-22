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
 * 重大项目立项
 * </p>
 *
 * @author 作者
 * @since 2023-01-11
 */
@Getter
@Setter
@TableName("big_project")
public class BigProject implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
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
     * 预计投标日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expectDate;

    /**
     *  发票类型
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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDatetime;

    private Integer processInstId;

    @TableField(exist = false)
    private List<SmallProtect> list;

    @TableField(exist = false)
    private List<BigProjectTest> list2;

    @TableField(exist = false)
    private List<BigProjectTest> list3;

    @TableField(exist = false)
    private List<BigProjectTest> list4;

    @TableField(exist = false)
    private ProcessInst processInst;

    @TableField(exist = false)
    private List<FileVO> fileList;

    private String remark;

    private String projectType;

    private String projectStatus;
}
