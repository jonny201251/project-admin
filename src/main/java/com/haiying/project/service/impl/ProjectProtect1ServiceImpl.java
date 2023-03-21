package com.haiying.project.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haiying.project.mapper.ProjectProtect1Mapper;
import com.haiying.project.model.entity.ProjectProtect1;
import com.haiying.project.model.entity.ProjectProtect2;
import com.haiying.project.service.ProjectProtect1Service;
import com.haiying.project.service.ProjectProtect2Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 一般和重大项目的保证金登记表1 服务实现类
 * </p>
 *
 * @author 作者
 * @since 2022-11-21
 */
@Service
public class ProjectProtect1ServiceImpl extends ServiceImpl<ProjectProtect1Mapper, ProjectProtect1> implements ProjectProtect1Service {
    @Autowired
    ProjectProtect2Service projectProtect2Service;

    @Override
    public boolean add(ProjectProtect1 projectProtect1) {
        this.save(projectProtect1);
        List<ProjectProtect2> list = projectProtect1.getList();
        if (ObjectUtil.isNotEmpty(list)) {
            list.forEach(item->item.setProtect1Id(projectProtect1.getId()));
            projectProtect2Service.saveBatch(list);
        }
        return true;
    }

    @Override
    public boolean edit(ProjectProtect1 projectProtect1) {
        this.updateById(projectProtect1);
        //
        projectProtect2Service.remove(new LambdaQueryWrapper<ProjectProtect2>().eq(ProjectProtect2::getProtect1Id, projectProtect1.getId()));
        List<ProjectProtect2> list = projectProtect1.getList();
        if (ObjectUtil.isNotEmpty(list)) {
            list.forEach(item->item.setProtect1Id(projectProtect1.getId()));
            projectProtect2Service.saveBatch(list);
        }
        return true;
    }
}
