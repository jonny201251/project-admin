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
 * 流程设计
 * </p>
 *
 * @author 作者
 * @since 2022-02-14
 */
@Getter
@Setter
@TableName("process_design")
public class ProcessDesign implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer baseId;

    private Integer beforeId;

    private String haveDisplay;

    private String name;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDatetime;

    private String remark;

    private String bpmnXml;

    private String deployId;

    private String path;
    //新增流程、变更流程
    private String processType;
}
