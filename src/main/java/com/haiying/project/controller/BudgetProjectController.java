package com.haiying.project.controller;


import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haiying.project.common.result.PageData;
import com.haiying.project.common.result.ResponseResult;
import com.haiying.project.common.result.Wrapper;
import com.haiying.project.model.entity.BigBudgetOut;
import com.haiying.project.model.entity.BudgetProject;
import com.haiying.project.model.entity.SmallBudgetOut;
import com.haiying.project.service.BigBudgetOutService;
import com.haiying.project.service.BudgetProjectService;
import com.haiying.project.service.SmallBudgetOutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 一般和重大项目预算-项目 前端控制器
 * </p>
 *
 * @author 作者
 * @since 2022-04-07
 */
@RestController
@RequestMapping("/budgetProject")
@Wrapper
public class BudgetProjectController {
    @Autowired
    BudgetProjectService budgetProjectService;
    @Autowired
    SmallBudgetOutService smallBudgetOutService;
    @Autowired
    BigBudgetOutService bigBudgetOutService;

    @PostMapping("list")
    public IPage<BudgetProject> list(@RequestBody Map<String, Object> paramMap) {
        LambdaQueryWrapper<BudgetProject> wrapper = new LambdaQueryWrapper<>();
        Integer current = (Integer) paramMap.get("current");
        Integer pageSize = (Integer) paramMap.get("pageSize");
        Object type = paramMap.get("type");
        Object name = paramMap.get("name");
        if (ObjectUtil.isNotEmpty(type)) {
            wrapper.like(BudgetProject::getId, type);
        }
        return budgetProjectService.page(new Page<>(current, pageSize), wrapper);
    }

    @PostMapping("list2")
    public ResponseResult list2(@RequestBody Map<String, Object> paramMap) {
        ResponseResult responseResult = ResponseResult.success();

        Integer current = (Integer) paramMap.get("current");
        Integer pageSize = (Integer) paramMap.get("pageSize");
        String type = (String) paramMap.get("type");
        String name = (String) paramMap.get("name");
        String costType = (String) paramMap.get("costType");
        if (type.equals("一般项目")) {
            QueryWrapper<SmallBudgetOut> queryWrapper = new QueryWrapper<SmallBudgetOut>()
                    .eq("cost_type", costType).like("name", name)
                    .select("distinct budget_id,project_id,name,task_code,type,cost_type,cost_rate");
            List<SmallBudgetOut> dataList = smallBudgetOutService.list(queryWrapper);
            if (ObjectUtil.isNotEmpty(dataList)) {
                int total = dataList.size();
                //计算总页数
                int totalPage = total / pageSize + ((total % pageSize == 0) ? 0 : 1);
                PageData pageData = new PageData(current, pageSize, total, totalPage, dataList);
                responseResult.setData(pageData);
            }
        } else {
            QueryWrapper<BigBudgetOut> queryWrapper = new QueryWrapper<BigBudgetOut>()
                    .eq("cost_type", costType).like("name", name)
                    .select("distinct budget_id,project_id,name,task_code,type,cost_type,cost_rate,company_name");
            List<BigBudgetOut> dataList = bigBudgetOutService.list(queryWrapper);
            if (ObjectUtil.isNotEmpty(dataList)) {
                int total = dataList.size();
                //计算总页数
                int totalPage = total / pageSize + ((total % pageSize == 0) ? 0 : 1);
                PageData pageData = new PageData(current, pageSize, total, totalPage, dataList);
                responseResult.setData(pageData);
            }
        }
        return responseResult;
    }

}
