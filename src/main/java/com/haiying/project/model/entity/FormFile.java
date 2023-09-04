package com.haiying.project.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>
 * 表单文件
 * </p>
 *
 * @author 作者
 * @since 2022-04-09
 */
@Getter
@Setter
@TableName("form_file")
public class FormFile implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 对应类的名称
     */
    private String type;

    /**
     * 具体设备表的id
     */
    private Integer businessId;

    //用于 合同签订情况--收款合同，付款合同
    private String taskCode;

    private String name;

    private String url;



}
