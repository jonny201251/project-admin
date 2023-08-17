package com.haiying.project.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 采购方式-比价单-子表
 * </p>
 *
 * @author 作者
 * @since 2023-08-10
 */
@Getter
@Setter
public class Price11 implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer price1Id;

    private Integer providerId;

    private String providerName;

    private Double price;

    private String rate;

    private String invoiceType;

    private Integer sort;


}
