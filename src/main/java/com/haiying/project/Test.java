package com.haiying.project;

import cn.hutool.core.text.TextSimilarity;

public class Test {
    public static void main(String[] args) {
        String db = "七室光学红外材料实验室";
        String page = "我是一只小小鸟";
        double d = TextSimilarity.similar(db, page);
        System.out.println(d);
    }
}
