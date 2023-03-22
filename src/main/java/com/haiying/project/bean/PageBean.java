package com.haiying.project.bean;

import com.haiying.project.common.result.PageData;
import com.haiying.project.common.result.ResponseResult;
import org.springframework.stereotype.Component;

@Component
public class PageBean {
    //获取分页数据
    public ResponseResult get(Integer currentPage, Integer pageSize, Integer total, Object dataList) {
        ResponseResult responseResult = ResponseResult.success();
        //计算总页数
        int totalPage = total / pageSize + ((total % pageSize == 0) ? 0 : 1);
        PageData pageData = new PageData(currentPage, pageSize, total, totalPage, dataList);
        responseResult.setData(pageData);
        return responseResult;
    }
}
