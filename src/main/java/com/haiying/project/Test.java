package com.haiying.project;

import cn.hutool.core.text.TextSimilarity;
import cn.hutool.core.util.NumberUtil;

public class Test {
    public static void main(String[] args) {
        String db = "七室光学红外材料实验室";
        String page = "七室光学红外材料实验室-我是一只小小鸟";
        double d = TextSimilarity.similar(db, page);
        System.out.println(d);
        int d2= NumberUtil.round(d*100,0).intValue();
        System.out.println(d2);
//        Integer d22=d2.intValue();
        System.out.println();
        System.out.println(d2 + "%");
    }
}
