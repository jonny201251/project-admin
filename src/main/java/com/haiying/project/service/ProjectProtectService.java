package com.haiying.project.service;

import com.haiying.project.model.entity.ProjectProtect;
import com.baomidou.mybatisplus.extension.service.IService;
import com.haiying.project.model.vo.ProjectProtectAfter;

/**
 * <p>
 * 一般和重大项目的保证金登记表 服务类
 * </p>
 *
 * @author 作者
 * @since 2023-03-22
 */
public interface ProjectProtectService extends IService<ProjectProtect> {

    boolean btnHandle(ProjectProtectAfter after);
}
