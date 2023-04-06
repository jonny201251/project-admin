package com.haiying.project.model.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class Customer2Excel {
    @ExcelProperty("院所")
    private String name;

    @ExcelProperty("二级")
    private String b;

    @ExcelProperty("三级")
    private String c;

    @ExcelProperty("四级")
    private String d;

}
