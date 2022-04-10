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
 * 项目收支-支出明细
 * </p>
 *
 * @author 作者
 * @since 2022-04-07
 */
@Getter
@Setter
@TableName("project_out2")
public class ProjectOut2 implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer projectOut1Id;

    /**
     * 费用类型
     */
    private String costType;

    /**
     * 日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate outDate;

    /**
     * 摘要
     */
    private String remark;

    /**
     * 开票金额
     */
    private Double money1;

    /**
     * 付款金额
     */
    private Double money2;

    /**
     * 付款方式
     */
    private String outStyle;

    /**
     * 到期日
     */
    private String arriveDate;
}
