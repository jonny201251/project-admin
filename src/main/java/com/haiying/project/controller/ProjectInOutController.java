package com.haiying.project.controller;


import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haiying.project.common.result.Wrapper;
import com.haiying.project.model.entity.ProjectIn;
import com.haiying.project.model.entity.ProjectIo;
import com.haiying.project.model.entity.ProjectOut;
import com.haiying.project.model.vo.ProjectInOut2VO;
import com.haiying.project.model.vo.ProjectInOut3VO;
import com.haiying.project.model.vo.ProjectInOutVO;
import com.haiying.project.service.ProjectInService;
import com.haiying.project.service.ProjectIoService;
import com.haiying.project.service.ProjectOutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

/**
 * <p>
 * 项目收支-收入明细 前端控制器
 * </p>
 *
 * @author 作者
 * @since 2022-04-07
 */
@RestController
@RequestMapping("/projectInOut")
public class ProjectInOutController {
    @Autowired
    ProjectInService projectInService;
    @Autowired
    ProjectOutService projectOutService;
    @Autowired
    ProjectIoService projectIoService;

    //收支明细表、项目收支表
    @PostMapping("list")
    @Wrapper
    public IPage<ProjectIn> list(@RequestBody Map<String, Object> paramMap) {
        QueryWrapper<ProjectIn> wrapper = new QueryWrapper<ProjectIn>()
                .select("distinct budget_id,project_id,task_code,name,property,wbs,customer_name,contract_code,contract_name,contract_money,end_money");
        Integer current = (Integer) paramMap.get("current");
        Integer pageSize = (Integer) paramMap.get("pageSize");
        Object name = paramMap.get("name");
        if (ObjectUtil.isNotEmpty(name)) {
            wrapper.like("name", name);
        }
        return projectInService.page(new Page<>(current, pageSize), wrapper);
    }

    //收支明细表、项目收支表
    @GetMapping("getInOut1")
    public synchronized Map<String, List<ProjectInOutVO>> getInOut1(Integer projectId) {
        ProjectInOutVO projectInOutVO = new ProjectInOutVO();
        List<ProjectIn> inList = projectInService.list(new LambdaQueryWrapper<ProjectIn>().eq(ProjectIn::getProjectId, projectId));

        ProjectIn projectIn = inList.get(0);
        //
        projectInOutVO.setName(projectIn.getName());
        projectInOutVO.setTaskCode(projectIn.getTaskCode());
        projectInOutVO.setProperty(projectIn.getProperty());
        projectInOutVO.setWbs(projectIn.getWbs());
        projectInOutVO.setContractCode(projectIn.getContractCode());

        Double inMoneyTotal = 0.0;
        for (ProjectIn in : inList) {
            inMoneyTotal += ofNullable(in.getMoney2()).orElse(0.0);
        }

        projectInOutVO.setInMoneyTotal(inMoneyTotal == 0.0 ? 0 : inMoneyTotal);

        Map<String, List<ProjectInOutVO>> map = new HashMap<>();
        List<ProjectInOutVO> listt = new ArrayList<>();
        listt.add(projectInOutVO);
        map.put("data", listt);
        return map;
    }

    //项目收支表
    @GetMapping("getInOut2")
    public synchronized Map<String, List<ProjectInOut2VO>> getInOut2(Integer projectId) {
        List<ProjectIn> inList = projectInService.list(new LambdaQueryWrapper<ProjectIn>().eq(ProjectIn::getProjectId, projectId));
        //
        List<ProjectOut> outList = projectOutService.list(new LambdaQueryWrapper<ProjectOut>().eq(ProjectOut::getProjectId, projectId));
        List<ProjectIo> ioList = projectIoService.list(new LambdaQueryWrapper<ProjectIo>().eq(ProjectIo::getProjectId, projectId));
        /*
            第一步：遍历，放入ProjectInOut2VO，生成list
            第二步：遍历list,生成累计
        */
        List<ProjectInOut2VO> list = new ArrayList<>();
        if (ObjectUtil.isNotEmpty(outList)) {
            for (ProjectOut out : outList) {
                ProjectInOut2VO tmp = new ProjectInOut2VO();
                tmp.setInOutDate(out.getOutDate().toString());
                tmp.setRemark(out.getRemark());
                //成本类型，付款金额
                tmp.setCostType(out.getCostType());
                tmp.setOutMoney2(out.getMoney2());
                tmp.setSort(out.getSort());
                list.add(tmp);
            }
        }
        for (ProjectIn in : inList) {
            ProjectInOut2VO tmp = new ProjectInOut2VO();
            tmp.setInOutDate(in.getInDate().toString());
            tmp.setRemark(in.getRemark());
            //开票金额或者收款金额
            tmp.setInMoney1(in.getMoney1());
            tmp.setInMoney2(in.getMoney2());
            tmp.setSort(in.getSort());
            list.add(tmp);
        }
        if (ObjectUtil.isNotEmpty(ioList)) {
            for (ProjectIo io : ioList) {
                ProjectInOut2VO tmp = new ProjectInOut2VO();
                tmp.setInOutDate(io.getIoDate().toString());
                tmp.setRemark(io.getRemark());
                //不影响收支的金额、影响收支的金额
                if (io.getHaveInOut().equals("否")) {
                    tmp.setIoMoney1(io.getMoney());
                } else {
                    tmp.setIoMoney2(io.getMoney());
                }
                tmp.setSort(io.getSort());
                list.add(tmp);
            }
        }
        //按照sort升序
        list = list.stream().sorted(Comparator.comparingDouble(ProjectInOut2VO::getSort)).collect(Collectors.toList());

        //累计往来款
        Double ioMoney2Total = 0.0;
        //收款-累计开票、累计收款
        Double inMoney1Total = 0.0;
        Double inMoney2Total = 0.0;

        Double outMoney2Total = 0.0;//付款-累计付款
        Double deviceTotal = 0.0;//材料及设备费
        Double labourTotal = 0.0;//劳务费
        Double techTotal = 0.0;//技术服务费
        Double engTotal = 0.0;//工程款
        Double taxTotal = 0.0;//税费
        Double otherTotal = 0.0;//其他费用
        Double moreTotal = 0.0;//项目结余
        for (ProjectInOut2VO vo : list) {
            ioMoney2Total += ofNullable(vo.getIoMoney2()).orElse(0.0);
            inMoney1Total += ofNullable(vo.getInMoney1()).orElse(0.0);
            inMoney2Total += ofNullable(vo.getInMoney2()).orElse(0.0);

            String costType = vo.getCostType();
            if (ObjectUtil.isNotEmpty(costType)) {
                if (costType.equals("材料及设备费")) {
                    deviceTotal += ofNullable(vo.getOutMoney2()).orElse(0.0);
                } else if (costType.equals("劳务费")) {
                    labourTotal += ofNullable(vo.getOutMoney2()).orElse(0.0);
                } else if (costType.equals("技术服务费")) {
                    techTotal += ofNullable(vo.getOutMoney2()).orElse(0.0);
                } else if (costType.equals("工程款")) {
                    engTotal += ofNullable(vo.getOutMoney2()).orElse(0.0);
                } else if (costType.equals("税费")) {
                    taxTotal += ofNullable(vo.getOutMoney2()).orElse(0.0);
                } else {
                    otherTotal += ofNullable(vo.getOutMoney2()).orElse(0.0);
                }
            }

            outMoney2Total = ofNullable(deviceTotal).orElse(0.0)
                    + ofNullable(labourTotal).orElse(0.0)
                    + ofNullable(techTotal).orElse(0.0)
                    + ofNullable(engTotal).orElse(0.0)
                    + ofNullable(taxTotal).orElse(0.0)
                    + ofNullable(otherTotal).orElse(0.0);

            moreTotal = ofNullable(ioMoney2Total).orElse(0.0)
                    + ofNullable(inMoney2Total).orElse(0.0)
                    - ofNullable(outMoney2Total).orElse(0.0);

            vo.setIoMoney2Total(ioMoney2Total == 0.0 ? null : ioMoney2Total);
            vo.setInMoney1Total(inMoney1Total == 0.0 ? null : inMoney1Total);
            vo.setInMoney2Total(inMoney2Total == 0.0 ? null : inMoney2Total);
            vo.setOutMoney2Total(outMoney2Total == 0.0 ? null : outMoney2Total);
            vo.setDeviceTotal(deviceTotal == 0.0 ? null : deviceTotal);
            vo.setLabourTotal(labourTotal == 0.0 ? null : labourTotal);
            vo.setTechTotal(techTotal == 0.0 ? null : techTotal);
            vo.setEngTotal(engTotal == 0.0 ? null : engTotal);
            vo.setTaxTotal(taxTotal == 0.0 ? null : taxTotal);
            vo.setOtherTotal(otherTotal == 0.0 ? null : otherTotal);
            vo.setMoreTotal(moreTotal == 0.0 ? 0 : moreTotal);
        }


        Map<String, List<ProjectInOut2VO>> map = new HashMap<>();
        map.put("data", list);
        return map;
    }

    //项目明细表-收款明细
    @GetMapping("getInDetail")
    public synchronized Map<String, List<ProjectInOut3VO>> getInDetail(Integer projectId) {
        Map<String, List<ProjectInOut3VO>> map = new HashMap<>();
        List<ProjectInOut3VO> list = new ArrayList<>();

        List<ProjectIn> inList = projectInService.list(new LambdaQueryWrapper<ProjectIn>().eq(ProjectIn::getProjectId, projectId));
        Double inOutmoney2Total = 0.0;
        for (ProjectIn item : inList) {
            ProjectInOut3VO vo = new ProjectInOut3VO();
            vo.setType1("收款明细");
            vo.setType2("收款1");
            vo.setContractCode(item.getContractCode());
            vo.setName(item.getCustomerName());
            vo.setContractMoney(item.getContractMoney());
            vo.setEndMoney(item.getEndMoney());
            vo.setRemarkk(item.getRemarkk());
            vo.setInOutDate(item.getInDate().toString());
            vo.setRemark(item.getRemark());
            vo.setInOutmoney1(item.getMoney1());
            vo.setInOutStyle(item.getInStyle());
            vo.setInOutmoney2(item.getMoney2());
            vo.setArriveDate(item.getArriveDate());


            inOutmoney2Total += ofNullable(item.getMoney2()).orElse(0.0);

            vo.setInOutmoney2Total(inOutmoney2Total);
            list.add(vo);
        }

        map.put("data", list);
        return map;
    }
    //项目明细表-付款明细
    //项目明细表-往来款
}
