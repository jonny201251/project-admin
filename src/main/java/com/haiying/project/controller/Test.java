package com.haiying.project.controller;

import cn.hutool.core.date.DateUtil;
import com.haiying.project.common.result.Wrapper;
import com.haiying.project.model.vo.UploadVO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@Wrapper
public class Test {
    public static void main(String[] args) {
        String simpleYear = DateUtil.format(DateUtil.date(), "yy");
        System.out.println(simpleYear);
    }
    @PostMapping("test")
    public boolean a(@RequestBody UploadVO uploadVO){
        System.out.println();
       return true;
    }
}
