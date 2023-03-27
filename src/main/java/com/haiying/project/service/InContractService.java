package com.haiying.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.haiying.project.model.entity.InContract;
import com.haiying.project.model.vo.InOutVO;

import java.io.InputStream;

/**
 * <p>
 * 收款合同 服务类
 * </p>
 *
 * @author 作者
 * @since 2023-03-25
 */
public interface InContractService extends IService<InContract> {

    boolean upload(InputStream inputStream);

    boolean edit(InContract inContract);

    boolean add(InContract inContract);

    boolean updateCode(InOutVO inOutVO);
}
