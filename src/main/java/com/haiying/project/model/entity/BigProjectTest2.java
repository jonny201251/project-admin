package com.haiying.project.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 重大项目----项目评估-甲方评估-战略伙伴评估的描述
 * </p>
 *
 * @author 作者
 * @since 2023-03-21
 */
@Getter
@Setter
@TableName("big_project_test2")
public class BigProjectTest2 implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String type;

    private String desc1;

    private String scoreDesc;


}
