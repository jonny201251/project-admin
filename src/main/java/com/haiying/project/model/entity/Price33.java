package com.haiying.project.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>
 * 采购方式-评审方案审批表-子表
 * </p>
 *
 * @author 作者
 * @since 2023-08-18
 */
@Getter
@Setter
public class Price33 implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer price3Id;

    private Integer providerId;

    private String providerName;

    private String descc;


}
