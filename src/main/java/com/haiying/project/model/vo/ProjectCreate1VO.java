package com.haiying.project.model.vo;

import lombok.Data;

@Data
public class ProjectCreate1VO {
    //序号
    private Integer num;
    //申请部门
    private String deptName;
    //1月
    private Integer a1;
    //2月
    private Integer a2;
    //3月
    private Integer a3;
    //4月
    private Integer a4;
    //5月
    private Integer a5;
    //6月
    private Integer a6;
    //7月
    private Integer a7;
    //8月
    private Integer a8;
    //9月
    private Integer a9;
    //10月
    private Integer a10;
    //11月
    private Integer a11;
    //12月
    private Integer a12;
    //立项总数
    private Integer a;
    //一类
    private Integer b1;
    //二类
    private Integer b2;
    //三类
    private Integer b3;
    //投标
    private Integer c1;
    //直签
    private Integer c2;

    //一类中标
    private Integer d1;
    //二类中标
    private Integer d2;
    //三类中标
    private Integer d3;
    //总中标
    private Integer d;

    //一类中标率
    private String e1;
    //二类中标率
    private String e2;
    //三类中标率
    private String e3;
    //总中标比例
    private String e;

    //投标签约
    private Integer f1;
    //直接签约
    private Integer f2;
    //21年立项22年签约
    private Integer f3;
    //22年立项22年签约
    private Integer f4;
    //总签约数
    private Integer f;

    //投标签约率
    private String g1;
    //立项签约率
    private String g2;
    //备注
}
