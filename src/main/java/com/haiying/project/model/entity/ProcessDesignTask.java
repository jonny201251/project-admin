package com.haiying.project.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 流程设计中的任务
 * </p>
 *
 * @author 作者
 * @since 2022-02-15
 */
@Getter
@Setter
@TableName("process_design_task")
public class ProcessDesignTask implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String taskType;

    private String taskKey;

    private String taskName;

    private String type;

    private String typeIds;

    @TableField(exist = false)
    private List<Integer> typeIdList;

    private String haveEditForm;

    private Integer processDesignId;

    private String remark;

    @TableField(exist = false)
    private List<ProcessDesignJump> jumpList;
}
