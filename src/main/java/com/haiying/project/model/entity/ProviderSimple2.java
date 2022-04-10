package com.haiying.project.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 供方情况简表
 * </p>
 *
 * @author 作者
 * @since 2022-04-09
 */
@Getter
@Setter
@TableName("provider_simple2")
public class ProviderSimple2 implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer providerSimpleId;

    private String type;

    private String name;

    private String education;

    private String job;


}
