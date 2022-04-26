package com.haiying.project.service;

import com.haiying.project.model.entity.InContract;
import com.baomidou.mybatisplus.extension.service.IService;
import com.haiying.project.model.vo.InContractVO;

/**
 * <p>
 * 收款合同 服务类
 * </p>
 *
 * @author 作者
 * @since 2022-04-21
 */
public interface InContractService extends IService<InContract> {

    boolean btnHandle(InContractVO inContractVO);
}
