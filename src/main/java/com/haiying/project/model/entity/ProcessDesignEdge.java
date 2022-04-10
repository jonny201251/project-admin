package com.haiying.project.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>
 * 流程设计中的连线
 * </p>
 *
 * @author 作者
 * @since 2022-02-14
 */
@Getter
@Setter
@TableName("process_design_edge")
public class ProcessDesignEdge implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String edgeId;

    private String edgeName;

    private String sourceTaskKey;

    private String targetTaskKey;

    private String direction;

    private String buttonName;

    private Integer buttonSort;

    private String javaVarName;

    private String conditionExpression;

    private Integer processDesignId;

    private String remark;
}
