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
 * 流程实例
 * </p>
 *
 * @author 作者
 * @since 2022-02-14
 */
@Getter
@Setter
@TableName("process_inst")
public class ProcessInst implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer processDesignId;

    private String processName;

    private String businessName;
    private Integer businessId;
    private Integer businessBaseId;
    private Integer businessBeforeId;
    private String businessHaveDisplay;
    private Integer businessVersion;

    private String actProcessInstanceId;

    //草稿、审批中、退回、完成
    private String processStatus;

    private String displayProcessStep;

    private String loginProcessStep;

    private Integer deptId;

    private String deptName;

    private String displayName;

    private String loginName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDatetime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endDatetime;

    private String path;
}
