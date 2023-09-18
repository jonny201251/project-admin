package com.haiying.project.service;

import com.haiying.project.model.entity.Price3;
import com.baomidou.mybatisplus.extension.service.IService;
import com.haiying.project.model.vo.Price3After;

/**
 * <p>
 * 采购方式-评审方案审批表 服务类
 * </p>
 *
 * @author 作者
 * @since 2023-08-18
 */
public interface Price3Service extends IService<Price3> {

    boolean btnHandle(Price3After after);
}
