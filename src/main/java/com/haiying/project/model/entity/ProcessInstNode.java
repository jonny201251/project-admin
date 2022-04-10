package com.haiying.project.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 流程实例节点
 * </p>
 *
 * @author 作者
 * @since 2022-02-14
 */
@Getter
@Setter
@TableName("process_inst_node")
public class ProcessInstNode implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer processInstId;

    private String taskKey;

    private String taskName;

    private String displayName;

    private String loginName;

    private Integer deptId;

    private String deptName;

    private String buttonName;

    private String comment;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDatetime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endDatetime;


}
