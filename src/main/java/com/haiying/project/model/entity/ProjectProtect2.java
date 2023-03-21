package com.haiying.project.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 一般和重大项目的保证金登记表2
 * </p>
 *
 * @author 作者
 * @since 2022-11-21
 */
@Getter
@Setter
@TableName("project_protect2")
public class ProjectProtect2 implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer protect1Id;

    /**
     * 日期
     */
    private LocalDate registeDate;

    /**
     * 付款单位
     */
    private String outName;

    /**
     * 收款单位
     */
    private String inName;

    /**
     * 金额
     */
    private Double money;

    private String remark;


}
