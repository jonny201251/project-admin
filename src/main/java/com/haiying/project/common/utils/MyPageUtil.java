package com.haiying.project.common.utils;


import com.haiying.project.common.result.PageData;
import com.haiying.project.common.result.ResponseResult;

public class MyPageUtil {
    //获取分页数据
    public static ResponseResult get(Integer currentPage, Integer pageSize, Integer total, Object dataList) {
        ResponseResult responseResult = ResponseResult.success();
        //计算总页数
        int totalPage = total / pageSize + ((total % pageSize == 0) ? 0 : 1);
        PageData pageData = new PageData(currentPage, pageSize, total, totalPage, dataList);
        responseResult.setData(pageData);
        return responseResult;
    }
}
