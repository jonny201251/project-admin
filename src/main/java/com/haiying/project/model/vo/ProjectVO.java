package com.haiying.project.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProjectVO {
    private Integer idd;
    private Integer id;
    private String projectType;
    private String name;
    private String taskCode;
    private String property;
    private Integer customerId;
    private String customerName;
    private String projectRate;
    private Integer version;

    private Double expectMoney;
    private String loginName;
    private String deptName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDatetime;
}
