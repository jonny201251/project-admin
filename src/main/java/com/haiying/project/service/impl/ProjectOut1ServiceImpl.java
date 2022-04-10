package com.haiying.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haiying.project.mapper.ProjectOut1Mapper;
import com.haiying.project.model.entity.ProjectOut1;
import com.haiying.project.model.entity.ProjectOut2;
import com.haiying.project.service.ProjectOut1Service;
import com.haiying.project.service.ProjectOut2Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 项目收支-支出明细 服务实现类
 * </p>
 *
 * @author 作者
 * @since 2022-04-07
 */
@Service
public class ProjectOut1ServiceImpl extends ServiceImpl<ProjectOut1Mapper, ProjectOut1> implements ProjectOut1Service {
    @Autowired
    ProjectOut2Service projectOut2Service;

    @Override
    public boolean add(ProjectOut1 projectOut1) {
        projectOut1.setCreateDatetime(LocalDateTime.now());
        this.save(projectOut1);
        List<ProjectOut2> list = projectOut1.getList();
        list.forEach(item -> item.setProjectOut1Id(projectOut1.getId()));
        projectOut2Service.saveBatch(list);
        return true;
    }

    @Override
    public boolean edit(ProjectOut1 projectOut1) {
        this.updateById(projectOut1);
        projectOut2Service.remove(new LambdaQueryWrapper<ProjectOut2>().eq(ProjectOut2::getProjectOut1Id, projectOut1.getId()));
        List<ProjectOut2> list = projectOut1.getList();
        for (ProjectOut2 projectOut2 : list) {
            projectOut2.setId(null);
            projectOut2.setProjectOut1Id(projectOut1.getId());
        }
        projectOut2Service.saveBatch(list);
        return true;
    }
}
