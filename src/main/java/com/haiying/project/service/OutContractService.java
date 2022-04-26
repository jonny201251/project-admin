package com.haiying.project.service;

import com.haiying.project.model.entity.OutContract;
import com.baomidou.mybatisplus.extension.service.IService;
import com.haiying.project.model.vo.OutContractVO;

/**
 * <p>
 * 付款合同 服务类
 * </p>
 *
 * @author 作者
 * @since 2022-04-21
 */
public interface OutContractService extends IService<OutContract> {

    boolean btnHandle(OutContractVO outContractVO);
}
