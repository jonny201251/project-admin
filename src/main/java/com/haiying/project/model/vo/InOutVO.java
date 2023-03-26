package com.haiying.project.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InOutVO {
    private Integer id;
    private Integer budgetId;
    private Integer projectId;
    private String name;
    private String wbs;
    private String taskCode;
    //合同类型：收款合同、付款合同
    private String contractType;
    private String contractName;
    private String contractCode;
    private Double contractMoney;
    private Double endMoney;
    //客户名称、供方名称
    private String name2;

    private String costType;
    private String costRate;

    private String displayName;
    private String deptName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDatetime;
}
