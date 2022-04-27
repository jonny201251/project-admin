package com.haiying.project.service;

import com.haiying.project.model.entity.ProjectOut;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 项目收支-支出明细 服务类
 * </p>
 *
 * @author 作者
 * @since 2022-04-27
 */
public interface ProjectOutService extends IService<ProjectOut> {

    boolean add(ProjectOut projectOut);
}
