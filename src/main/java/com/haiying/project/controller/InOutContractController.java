package com.haiying.project.controller;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.haiying.project.common.result.PageData;
import com.haiying.project.common.result.ResponseResult;
import com.haiying.project.common.result.Wrapper;
import com.haiying.project.model.entity.InContract;
import com.haiying.project.model.entity.OutContract;
import com.haiying.project.model.entity.SysUser;
import com.haiying.project.model.vo.InOutVO;
import com.haiying.project.service.InContractService;
import com.haiying.project.service.OutContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//合同号和WBS号
@RestController
@RequestMapping("/inOutContract")
@Wrapper
public class InOutContractController {
    @Autowired
    InContractService inContractService;
    @Autowired
    OutContractService outContractService;
    @Autowired
    HttpSession httpSession;

    @PostMapping("list")
    public ResponseResult list2(@RequestBody Map<String, Object> paramMap) {
        ResponseResult responseResult = ResponseResult.success();
        List<InOutVO> dataList = new ArrayList<>();

        Integer current = (Integer) paramMap.get("current");
        Integer pageSize = (Integer) paramMap.get("pageSize");

        SysUser user = (SysUser) httpSession.getAttribute("user");
        List<InContract> list1 = inContractService.list(new LambdaQueryWrapper<InContract>().eq(InContract::getHaveDisplay, "是").eq(InContract::getDeptId, user.getDeptId()));
        List<OutContract> list2 = outContractService.list(new LambdaQueryWrapper<OutContract>().eq(OutContract::getHaveDisplay, "是").eq(OutContract::getDeptId, user.getDeptId()));

        if (ObjectUtil.isNotEmpty(list1)) {
            for (InContract item : list1) {
                InOutVO vo = new InOutVO();
                vo.setId(item.getId());
                vo.setBudgetId(item.getBudgetId());
                vo.setProjectId(item.getProjectId());
                vo.setName(item.getName());
                vo.setWbs(item.getWbs());
                vo.setContractType("收款合同");
                vo.setContractName(item.getContractName());
                vo.setContractMoney(item.getContractMoney());
                vo.setEndMoney(item.getEndMoney());
                vo.setName2(item.getCustomerName());

                vo.setDisplayName(item.getDisplayName());
                vo.setDeptName(item.getDeptName());

                dataList.add(vo);
            }
        }
        if (ObjectUtil.isNotEmpty(list2)) {
            for (OutContract item : list2) {
                InOutVO vo = new InOutVO();
                vo.setId(item.getId());
                vo.setBudgetId(item.getBudgetId());
                vo.setProjectId(item.getProjectId());
                vo.setName(item.getName());
                vo.setWbs(item.getWbs());
                vo.setContractType("付款合同");
                vo.setContractName(item.getContractName());
                vo.setContractMoney(item.getContractMoney());
                vo.setEndMoney(item.getEndMoney());
                vo.setName2(item.getProviderName());

                vo.setCostType(item.getCostType());
                vo.setCostRate(item.getCostRate());

                vo.setDisplayName(item.getDisplayName());
                vo.setDeptName(item.getDeptName());

                dataList.add(vo);
            }
        }

        if (ObjectUtil.isNotEmpty(dataList)) {
            int total = dataList.size();
            //计算总页数
            int totalPage = total / pageSize + ((total % pageSize == 0) ? 0 : 1);
            PageData pageData = new PageData(current, pageSize, total, totalPage, dataList);
            responseResult.setData(pageData);
        }
        return responseResult;
    }


}
