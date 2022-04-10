package com.haiying.project.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>
 * 流程设计中的任务的跳转节点
 * </p>
 *
 * @author 作者
 * @since 2022-03-01
 */
@Getter
@Setter
@TableName("process_design_jump")
public class ProcessDesignJump implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 退回，提交
     */
    private String direction;

    private String sourceTaskKey;

    private String targetTaskKey;

    private String buttonName;

    private Integer buttonSort;

    private Integer processDesignId;
}
