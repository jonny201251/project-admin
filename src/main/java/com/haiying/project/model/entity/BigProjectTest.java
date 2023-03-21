package com.haiying.project.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>
 * 重大项目----项目评估-甲方评估-战略伙伴评估
 * </p>
 *
 * @author 作者
 * @since 2023-01-11
 */
@Getter
@Setter
@TableName("big_project_test")
public class BigProjectTest implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer projectId;

    private String desc1;

    private String desc2;

    private String standard;

    private Integer score;

    private String type;

    @TableField(exist = false)
    private String scoreDesc;
}
