package com.haiying.project.service;

import com.haiying.project.model.entity.Price1;
import com.baomidou.mybatisplus.extension.service.IService;
import com.haiying.project.model.vo.Price1After;

/**
 * <p>
 * 采购方式-比价单 服务类
 * </p>
 *
 * @author 作者
 * @since 2023-08-18
 */
public interface Price1Service extends IService<Price1> {

    boolean btnHandle(Price1After after);
}
