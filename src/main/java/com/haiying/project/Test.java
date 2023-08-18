package com.haiying.project;

import cn.hutool.core.text.TextSimilarity;
import cn.hutool.core.util.NumberUtil;

public class Test {
    public static void main(String[] args) {
        System.out.println(200000/1.06 * 0.06);
        System.out.println(200000-11320.75);


        String db = "七室光学红外材料实验室";
        String page = "七室光学红外材料实验室七室光学红外材料实验室";
        double d = TextSimilarity.similar(db, page);
        System.out.println(d);
        int d2= NumberUtil.round(d*100,0).intValue();
        System.out.println(d2);
//        Integer d22=d2.intValue();
        System.out.println();
        System.out.println(d2 + "%");
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println(50000+228000+200000+120000+84000+19560+39120+84760);

        System.out.println(228000+6840+13680+29640);
        System.out.println(200000+6600+13200+28600);

        //
        String index="020009521053-013";
        System.out.println(Integer.parseInt(index)+1);
    }
}
