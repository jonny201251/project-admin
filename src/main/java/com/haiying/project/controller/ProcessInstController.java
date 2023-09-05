package com.haiying.project.controller;


import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haiying.project.bean.PageBean;
import com.haiying.project.bean.WorkFlowBean;
import com.haiying.project.common.result.ResponseResult;
import com.haiying.project.common.result.Wrapper;
import com.haiying.project.model.entity.BigProject;
import com.haiying.project.model.entity.ProcessInst;
import com.haiying.project.model.entity.SmallProject;
import com.haiying.project.model.entity.SysUser;
import com.haiying.project.model.vo.MyList2VO;
import com.haiying.project.service.BigProjectService;
import com.haiying.project.service.ProcessInstNodeService;
import com.haiying.project.service.ProcessInstService;
import com.haiying.project.service.SmallProjectService;
import org.activiti.engine.task.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * <p>
 * 流程实例 前端控制器
 * </p>
 *
 * @author 作者
 * @since 2022-02-14
 */
@RestController
@RequestMapping("/processInst")
@Wrapper
public class ProcessInstController {
    @Autowired
    ProcessInstService processInstService;
    @Autowired
    ProcessInstNodeService processInstNodeService;
    @Autowired
    WorkFlowBean workFlowBean;
    @Autowired
    HttpSession httpSession;
    @Autowired
    PageBean pageBean;
    @Autowired
    SmallProjectService smallProjectService;
    @Autowired
    BigProjectService bigProjectService;

    //待办任务
    @PostMapping("myList")
    public IPage<ProcessInst> list() {
        LambdaQueryWrapper<ProcessInst> wrapper = new LambdaQueryWrapper<>();
        SysUser user = (SysUser) httpSession.getAttribute("user");
        wrapper.ne(ProcessInst::getProcessStatus, "完成")
                .eq(ProcessInst::getLoginProcessStep, user.getLoginName()).or()
                .likeLeft(ProcessInst::getLoginProcessStep, "," + user.getLoginName()).or()
                .likeRight(ProcessInst::getLoginProcessStep, user.getLoginName() + ",").or()
                .like(ProcessInst::getLoginProcessStep, "," + user.getLoginName() + ",");
        return processInstService.page(new Page<>(1, 100), wrapper);
    }

    //补充数据
    @PostMapping("myList2")
    public ResponseResult list2() {
        List<MyList2VO> dataList = new ArrayList<>();
        Map<Integer, MyList2VO> map = new TreeMap<>();
        List<Integer> idList = new ArrayList<>();

        ResponseResult responseResult = ResponseResult.success();
        LambdaQueryWrapper<SmallProject> wrapper1 = new LambdaQueryWrapper<SmallProject>().eq(SmallProject::getHavePower, "是").isNull(SmallProject::getPowerCode);
        LambdaQueryWrapper<BigProject> wrapper2 = new LambdaQueryWrapper<BigProject>().eq(BigProject::getHavePower, "是").isNull(BigProject::getPowerCode);
        List<SmallProject> list1 = smallProjectService.list(wrapper1);
        List<BigProject> list2 = bigProjectService.list(wrapper2);
        if (ObjectUtil.isNotEmpty(list1)) {
            for (SmallProject tmp : list1) {
                MyList2VO vo = new MyList2VO();
                vo.setType("一般项目立项");
                vo.setId(tmp.getId());
                vo.setName(tmp.getName());
                vo.setDeptName(tmp.getDeptName());
                vo.setDisplayName(tmp.getDisplayName());
                vo.setCreateDatetime(tmp.getCreateDatetime());
                map.put(tmp.getProcessInstId(), vo);
                idList.add(tmp.getProcessInstId());
            }
        }
        if (ObjectUtil.isNotEmpty(list2)) {
            for (BigProject tmp : list2) {
                MyList2VO vo = new MyList2VO();
                vo.setType("重大项目评估");
                vo.setId(tmp.getId());
                vo.setName(tmp.getName());
                vo.setDeptName(tmp.getDeptName());
                vo.setDisplayName(tmp.getDisplayName());
                vo.setCreateDatetime(tmp.getCreateDatetime());
                map.put(tmp.getProcessInstId(), vo);
                idList.add(tmp.getProcessInstId());
            }
        }

        if (ObjectUtil.isNotEmpty(idList)) {
            List<ProcessInst> list = processInstService.list(new LambdaQueryWrapper<ProcessInst>().in(ProcessInst::getId, idList).eq(ProcessInst::getProcessStatus, "完成"));
            if (ObjectUtil.isNotEmpty(list)) {
                for (ProcessInst tmp : list) {
                    MyList2VO vo = map.get(tmp.getId());
                    if (vo != null) {
                        dataList.add(vo);
                    }
                }
            }
        }


        if (ObjectUtil.isNotEmpty(dataList)) {
            responseResult = pageBean.get(1, 100, dataList.size(), dataList);
        }
        return responseResult;
    }

    @GetMapping("getRunTaskKeyList")
    public List<String> getRunTaskList(String processInstId) {
        List<String> list = new ArrayList<>();
        ProcessInst processInst = processInstService.getById(processInstId);
        if (processInst != null && ObjectUtil.isNotEmpty(processInst.getActProcessInstanceId())) {
            List<Task> runTaskList = workFlowBean.getRunTaskList(processInst.getActProcessInstanceId());
            if (ObjectUtil.isNotEmpty(runTaskList)) {
                list = runTaskList.stream().map(Task::getTaskDefinitionKey).collect(Collectors.toList());
            }
        }
        return list;
    }
}
