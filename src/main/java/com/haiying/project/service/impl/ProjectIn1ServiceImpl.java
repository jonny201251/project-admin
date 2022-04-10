package com.haiying.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haiying.project.mapper.ProjectIn1Mapper;
import com.haiying.project.model.entity.ProjectIn1;
import com.haiying.project.model.entity.ProjectIn2;
import com.haiying.project.service.ProjectIn1Service;
import com.haiying.project.service.ProjectIn2Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 项目收支-收入明细 服务实现类
 * </p>
 *
 * @author 作者
 * @since 2022-04-07
 */
@Service
public class ProjectIn1ServiceImpl extends ServiceImpl<ProjectIn1Mapper, ProjectIn1> implements ProjectIn1Service {
    @Autowired
    ProjectIn2Service projectIn2Service;

    @Override
    public boolean add(ProjectIn1 projectIn1) {
        projectIn1.setCreateDatetime(LocalDateTime.now());
        this.save(projectIn1);
        List<ProjectIn2> list = projectIn1.getList();
        list.forEach(item -> item.setProjectIn1Id(projectIn1.getId()));
        projectIn2Service.saveBatch(list);
        return true;
    }

    @Override
    public boolean edit(ProjectIn1 projectIn1) {
        this.updateById(projectIn1);
        projectIn2Service.remove(new LambdaQueryWrapper<ProjectIn2>().eq(ProjectIn2::getProjectIn1Id, projectIn1.getId()));
        List<ProjectIn2> list = projectIn1.getList();
        for (ProjectIn2 projectIn2 : list) {
            projectIn2.setId(null);
            projectIn2.setProjectIn1Id(projectIn1.getId());
        }
        projectIn2Service.saveBatch(list);
        return true;
    }
}
