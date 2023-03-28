package com.haiying.project.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.haiying.project.model.vo.FileVO;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 项目预算的流程
 * </p>
 *
 * @author 作者
 * @since 2023-03-25
 */
@Getter
@Setter
@TableName("small_budget_run")
public class SmallBudgetRun implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.INPUT)
    private Integer id;

    private Integer baseId;

    private Integer beforeId;

    private String haveDisplay;

    private Integer version;

    private Integer projectId;

    private String projectType;

    /**
     * 项目名称
     */
    private String name;

    /**
     * 项目任务号
     */
    private String taskCode;

    private String remark;

    private String loginName;

    private String displayName;

    private Integer deptId;

    private String deptName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDatetime;

    private Integer processInstId;

    //流程-财务部
    private String userNamee;
    //排他网关
    private String haveThree;

    //显示预算表
    private String viewBudget;

    @TableField(exist = false)
    private ProcessInst processInst;

    @TableField(exist = false)
    private List<FileVO> fileList;


    @TableField(exist = false)
    private BudgetProject newBudgetProject;
}
