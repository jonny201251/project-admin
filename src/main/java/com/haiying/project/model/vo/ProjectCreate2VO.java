package com.haiying.project.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ProjectCreate2VO {
    //申请部门
    private String deptName;
    //申请时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDatetime;
    //流程状态
    private String processStatus;
    //项目立项
    private String projectType;
    //项目类型
    private String projectTypee;
    //项目名称
    private String name;
    //备案号
    private String taskCode;
    private Integer sortt;
    //项目性质
    private String property;
    //项目毛利率
    private String projectRate;
    //预计签约金额(元)
    private Double expectMoney;
    //预计签约日期
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expectDate;
    //投标保证金/函(元)
    private String a1;
    //质量保证金/函(元)
    private String a2;
    //工资保证金/函(元)
    private String a3;
    //履约保证金/函(元)
    private String a4;
    //垫资额度(元)
    private String giveMoney;
    //项目地点
    private String location;
    //战略伙伴名称
    private String providerName;
    //业务状态
    private String bidStatus;
    //投标截止日期
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate bidDate;
    //备注
    private String remark;
}
