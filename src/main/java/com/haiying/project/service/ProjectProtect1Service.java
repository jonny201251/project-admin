package com.haiying.project.service;

import com.haiying.project.model.entity.ProjectProtect1;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 一般和重大项目的保证金登记表1 服务类
 * </p>
 *
 * @author 作者
 * @since 2022-11-21
 */
public interface ProjectProtect1Service extends IService<ProjectProtect1> {

    boolean add(ProjectProtect1 projectProtect1);

    boolean edit(ProjectProtect1 projectProtect1);
}
