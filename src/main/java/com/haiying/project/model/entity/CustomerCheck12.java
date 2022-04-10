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
 * 客户信用审批
 * </p>
 *
 * @author 作者
 * @since 2022-03-22
 */
@Getter
@Setter
@TableName("customer_check12")
public class CustomerCheck12 implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer customerScore1Id;

    private Integer baseId;

    private Integer beforeId;

    private String haveDisplay;

    private Integer version;

    private Integer customerId;

    private String customerName;

    private String customerProperty;

    private Integer startScore;

    private String startResult;

    private Integer endScore;

    private String endResult;

    private String desc1;
    @TableField(exist = false)
    private List<String> desc2Tmp;
    private String desc2;

    private String desc3;

    private String desc4;

    private String displayName;

    private String loginName;

    private Integer deptId;

    private String deptName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDatetime;

    private Integer processInstId;

    @TableField(exist = false)
    private ProcessInst processInst;
}
