package com.haiying.project.service;

import com.haiying.project.model.entity.ProjectPower;
import com.baomidou.mybatisplus.extension.service.IService;
import com.haiying.project.model.vo.ProjectPowerAfter;

/**
 * <p>
 * 一般和重大项目立项时，授权信息 服务类
 * </p>
 *
 * @author 作者
 * @since 2022-03-24
 */
public interface ProjectPowerService extends IService<ProjectPower> {

    boolean btnHandle(ProjectPowerAfter after);
}
