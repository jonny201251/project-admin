package com.haiying.project.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 供方评分1
 * </p>
 *
 * @author 作者
 * @since 2022-03-17
 */
@Getter
@Setter
@TableName("provider_score1")
public class ProviderScore1 implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer baseId;

    private Integer beforeId;

    private String haveDisplay;

    private Integer version;

    private String usee;

    private Integer providerId;

    private String providerName;

    /**
     * 项目类别
     */
    private String type;

    private String displayName;

    private String loginName;

    private Integer deptId;

    private String deptName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDatetime;

    /**
     *  初评得分
     */
    private Integer startScore;

    /**
     * 最终得分
     */
    private Integer endScore;

    /**
     * 结论
     */
    private String result;

    private String remark;

    private Integer processInstId;

    @TableField(exist = false)
    private List<ProviderScore2> providerScore2List;

    @TableField(exist = false)
    private ProcessInst processInst;

    private String contractName;

    //专业审查部门
    private String userNamee;
}
