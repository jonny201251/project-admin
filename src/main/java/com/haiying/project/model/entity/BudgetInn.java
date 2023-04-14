package com.haiying.project.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 一般和重大项目预算-预计收入
 * </p>
 *
 * @author 作者
 * @since 2023-04-13
 */
@Getter
@Setter
@TableName("budget_inn")
public class BudgetInn implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer budgetId;

    private Integer projectId;

    private String projectType;

    /**
     * 担保名称
     */
    private String inType;

    /**
     * 金额
     */
    private Double money;


}
