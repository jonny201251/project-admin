package com.haiying.project.common.result;

import lombok.AllArgsConstructor;
import lombok.Data;

//pro-table需要的数据结构
@Data
@AllArgsConstructor
public class PageData {
    //当前页
    private Integer currentPage;
    //每页显示几条
    private Integer pageSize;
    //总记录数
    private Integer total;
    //总分页数
    private Integer totalPage;
    //具体数据
    Object dataList;
}
