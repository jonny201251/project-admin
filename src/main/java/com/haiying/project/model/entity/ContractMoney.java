package com.haiying.project.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 收付款合同的合同金额的修改历史
 * </p>
 *
 * @author 作者
 * @since 2023-09-04
 */
@Getter
@Setter
@TableName("contract_money")
public class ContractMoney implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 收款合同，付款合同
     */
    private String type;

    /**
     * 合同号
     */
    private String contractCode;

    private Double contractMoney;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDatetime;

    private String loginName;

    private String displayName;

    private Integer deptId;

    private String deptName;


}
