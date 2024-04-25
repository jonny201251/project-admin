package com.haiying.project.service;

import com.haiying.project.model.entity.Price4;
import com.baomidou.mybatisplus.extension.service.IService;
import com.haiying.project.model.vo.Price4After;

/**
 * <p>
 * 采购方式-招标预案审批表 服务类
 * </p>
 *
 * @author 作者
 * @since 2024-04-25
 */
public interface Price4Service extends IService<Price4> {

    boolean btnHandle(Price4After after);
}
