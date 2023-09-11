package com.haiying.project.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BudgetProjecttOutVO {
    private Integer budgetId;
    private String projectType;
    private String name;
    private String wbs;
    private String taskCode;
    //
    private String outType;
    private String rate;
    private Double money;
    private Integer outId;
    //
    private String displayName;
    private String loginName;

    private Integer deptId;

    private String deptName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDatetime;

}
