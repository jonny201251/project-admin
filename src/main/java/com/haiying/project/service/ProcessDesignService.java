package com.haiying.project.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.haiying.project.model.entity.ProcessDesign;
import com.haiying.project.model.vo.ProcessDesignVO;

import java.util.List;

/**
 * <p>
 * 流程设计 服务类
 * </p>
 *
 * @author 作者
 * @since 2022-02-14
 */
public interface ProcessDesignService extends IService<ProcessDesign> {

    boolean add(ProcessDesignVO processDesignVO);

    boolean edit(ProcessDesignVO processDesignVO);

    boolean delete(List<Integer> idList);
}
