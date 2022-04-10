package com.haiying.project.service;

import com.haiying.project.model.entity.ProjectOut1;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 项目收支-支出明细 服务类
 * </p>
 *
 * @author 作者
 * @since 2022-04-07
 */
public interface ProjectOut1Service extends IService<ProjectOut1> {

    boolean add(ProjectOut1 projectOut1);

    boolean edit(ProjectOut1 projectOut1);
}
