package com.haiying.project.controller;

import cn.hutool.core.date.DateUtil;
import com.haiying.project.common.result.Wrapper;
import com.haiying.project.model.vo.UploadVO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping
@Wrapper
public class Test {
    public static void main(String[] args) {
        String simpleYear = DateUtil.format(DateUtil.date(), "yy");
        System.out.println(LocalDate.now().toString());
        Double outMoney2Total = 0.0;
        if(outMoney2Total==0.0){
            outMoney2Total=null;
        }
        System.out.println(outMoney2Total);


        String s="尽职调查部门[郑雷]";
        String name="郑雷";
        String[] tmp = s.split("\\[");
        System.out.println();

        System.out.println(Integer.parseInt("020"));
    }
    @PostMapping("test")
    public boolean a(@RequestBody UploadVO uploadVO){
        System.out.println("5");
       return true;
    }
}
