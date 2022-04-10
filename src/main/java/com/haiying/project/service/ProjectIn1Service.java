package com.haiying.project.service;

import com.haiying.project.model.entity.ProjectIn1;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 项目收支-收入明细 服务类
 * </p>
 *
 * @author 作者
 * @since 2022-04-07
 */
public interface ProjectIn1Service extends IService<ProjectIn1> {

    boolean add(ProjectIn1 projectIn1);

    boolean edit(ProjectIn1 projectIn1);
}
