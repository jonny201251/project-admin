package com.haiying.project;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.text.TextSimilarity;
import cn.hutool.core.util.NumberUtil;

import java.text.DecimalFormat;

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


        System.out.println("013121323001".substring(9));

        System.out.println("020009521053-002".split("-")[0]);

        System.out.println(!"非密".equals("非密"));
        System.out.println(!"非密".equals("内部"));

        System.out.println("a   aaa    aa    aa".replaceAll("\\s+",""));

        System.out.println("2023".substring(2));

        System.out.println(DateUtil.parse("2023-10-13 16:21:29").month()+1);

//        double result = (double) (1 / 3 );
//        DecimalFormat decimalFormat = new DecimalFormat("0.00%");
//        String formattedResult = decimalFormat.format(result);
//        System.out.println(formattedResult);

        int numerator = 5;
        int denominator = 3;

        double result = (double) 1/3 ;
        DecimalFormat decimalFormat = new DecimalFormat("0.00%");
        String formattedResult = decimalFormat.format(result);

        System.out.println("结果为: " + formattedResult);

    }
}
