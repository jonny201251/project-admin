package com.haiying.project.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * <p>
 * 项目收支-收入明细
 * </p>
 *
 * @author 作者
 * @since 2022-04-07
 */
@Getter
@Setter
@TableName("project_in2")
public class ProjectIn2 implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer projectIn1Id;

    /**
     * 日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate inDate;

    /**
     * 摘要
     */
    private String remark;

    /**
     * 开票金额
     */
    private Double money1;

    /**
     * 收款金额
     */
    private Double money2;

    /**
     * 收款方式
     */
    private String inStyle;

    /**
     * 到期日
     */
    private String arriveDate;


}
