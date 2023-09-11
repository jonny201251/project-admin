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
    public ResponseResult list(@RequestBody Map<String, Object> paramMap) {
        ResponseResult responseResult = ResponseResult.success();
        List<InOutVO> dataList = new ArrayList<>();

        Integer current = (Integer) paramMap.get("current");
        Integer pageSize = (Integer) paramMap.get("pageSize");

        SysUser user = (SysUser) httpSession.getAttribute("user");


        LambdaQueryWrapper<InContract> wrapper1=new LambdaQueryWrapper<InContract>().and(qr->qr.isNull(InContract::getContractCode).or().isNull(InContract::getWbs));
        LambdaQueryWrapper<OutContract> wrapper2=new LambdaQueryWrapper<OutContract>().and(qr->qr.isNull(OutContract::getContractCode).or().isNull(OutContract::getWbs));

        if (!user.getDeptName().equals("综合计划部")) {
            wrapper1.eq(InContract::getDeptId, user.getDeptId());
            wrapper2.eq(OutContract::getDeptId, user.getDeptId());
        }

        Object name = paramMap.get("name");
        Object taskCode = paramMap.get("taskCode");
        Object wbs = paramMap.get("wbs");
        Object contractCode = paramMap.get("contractCode");
        Object contractName = paramMap.get("contractName");
        Object displayName = paramMap.get("displayName");
        Object deptName = paramMap.get("deptName");
        if (ObjectUtil.isNotEmpty(name)) {
            wrapper1.like(InContract::getName, name);
            wrapper2.like(OutContract::getName, name);
        }
        if (ObjectUtil.isNotEmpty(taskCode)) {
            wrapper1.like(InContract::getTaskCode, taskCode);
            wrapper2.like(OutContract::getTaskCode, taskCode);
        }
        if (ObjectUtil.isNotEmpty(wbs)) {
            wrapper1.like(InContract::getWbs, wbs);
            wrapper2.like(OutContract::getWbs, wbs);
        }
        if (ObjectUtil.isNotEmpty(contractCode)) {
            wrapper1.like(InContract::getContractCode, contractCode);
            wrapper2.like(OutContract::getContractCode, contractCode);
        }
        if (ObjectUtil.isNotEmpty(contractName)) {
            wrapper1.like(InContract::getContractName, contractName);
            wrapper2.like(OutContract::getContractName, contractName);
        }
        if (ObjectUtil.isNotEmpty(displayName)) {
            wrapper1.like(InContract::getDisplayName, displayName);
            wrapper2.like(OutContract::getDisplayName, displayName);
        }
        if (ObjectUtil.isNotEmpty(deptName)) {
            wrapper1.like(InContract::getDeptName, deptName);
            wrapper2.like(OutContract::getDeptName, deptName);
        }

        List<InContract> list1 = inContractService.list(wrapper1);
        List<OutContract> list2 = outContractService.list(wrapper2);

        int i=1;

        if (ObjectUtil.isNotEmpty(list1)) {
            for (InContract item : list1) {
                InOutVO vo = new InOutVO();
                vo.setIdd(i++);
                vo.setId(item.getId());
                vo.setBudgetId(item.getBudgetId());
                vo.setProjectId(item.getProjectId());
                vo.setName(item.getName());
                vo.setWbs(item.getWbs());
                vo.setTaskCode(item.getTaskCode());
                vo.setContractType("收款合同");
                vo.setContractName(item.getContractName());
                vo.setContractCode(item.getContractCode());
                vo.setContractMoney(item.getContractMoney());
                vo.setEndMoney(item.getEndMoney());
                vo.setName2(item.getCustomerName());

                vo.setDisplayName(item.getDisplayName());
                vo.setDeptName(item.getDeptName());
                vo.setCreateDatetime(item.getCreateDatetime());

                dataList.add(vo);
            }
        }
        if (ObjectUtil.isNotEmpty(list2)) {
            for (OutContract item : list2) {
                InOutVO vo = new InOutVO();
                vo.setIdd(i++);
                vo.setId(item.getId());
                vo.setBudgetId(item.getBudgetId());
                vo.setProjectId(item.getProjectId());
                vo.setName(item.getName());
                vo.setWbs(item.getWbs());
                vo.setTaskCode(item.getTaskCode());
                vo.setContractType("付款合同");
                vo.setContractName(item.getContractName());
                vo.setContractCode(item.getContractCode());
                vo.setContractMoney(item.getContractMoney());
                vo.setEndMoney(item.getEndMoney());
                vo.setName2(item.getProviderName());

                vo.setCostType(item.getCostType());
                vo.setCostRate(item.getCostRate());

                vo.setDisplayName(item.getDisplayName());
                vo.setDeptName(item.getDeptName());
                vo.setCreateDatetime(item.getCreateDatetime());

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

    @PostMapping("add")
    public boolean add(@RequestBody InOutVO inOutVO) {
        if (inOutVO.getContractType().equals("收款合同")) {
            inContractService.updateCode(inOutVO);
        } else {
            outContractService.updateCode(inOutVO);
        }
        return true;
    }


}
