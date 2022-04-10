package com.haiying.project.service;

import com.haiying.project.model.entity.OtherPower;
import com.baomidou.mybatisplus.extension.service.IService;
import com.haiying.project.model.vo.OtherPowerAfter;

/**
 * <p>
 *  其他授权 服务类
 * </p>
 *
 * @author 作者
 * @since 2022-04-08
 */
public interface OtherPowerService extends IService<OtherPower> {

    boolean btnHandle(OtherPowerAfter after);
}
