package com.haiying.project.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haiying.project.common.exception.PageTipException;
import com.haiying.project.common.utils.SpringUtil;
import com.haiying.project.mapper.OutContractMapper;
import com.haiying.project.model.entity.BudgetProject;
import com.haiying.project.model.entity.FormFile;
import com.haiying.project.model.entity.InContract;
import com.haiying.project.model.entity.OutContract;
import com.haiying.project.model.vo.FileVO;
import com.haiying.project.model.vo.InOutVO;
import com.haiying.project.service.BudgetProjectService;
import com.haiying.project.service.FormFileService;
import com.haiying.project.service.InContractService;
import com.haiying.project.service.OutContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 付款合同 服务实现类
 * </p>
 *
 * @author 作者
 * @since 2023-03-26
 */
@Service
public class OutContractServiceImpl extends ServiceImpl<OutContractMapper, OutContract> implements OutContractService {
    @Autowired
    FormFileService formFileService;
    @Autowired
    InContractService inContractService;
    @Autowired
    BudgetProjectService budgetProjectService;

    @Override
    public boolean edit(OutContract outContract) {
        //先有收款合同，才能进行付款合同
        List<InContract> ll = inContractService.list(new LambdaQueryWrapper<InContract>().eq(InContract::getTaskCode, outContract.getTaskCode()));
        if (ObjectUtil.isNotEmpty(ll)) {
            throw new PageTipException("必须先有收款合同，才能进行付款合同");
        }
        this.updateById(outContract);
        formFileService.remove(new LambdaQueryWrapper<FormFile>().eq(FormFile::getType, "OutContract").eq(FormFile::getBusinessId, outContract.getId()));
        //文件
        List<FormFile> list = new ArrayList<>();
        List<FileVO> fileList = outContract.getFileList();
        if (ObjectUtil.isNotEmpty(fileList)) {
            for (FileVO fileVO : fileList) {
                FormFile formFile = new FormFile();
                formFile.setType("OutContract");
                formFile.setBusinessId(outContract.getId());
                formFile.setName(fileVO.getName());
                formFile.setUrl(fileVO.getUrl());
                list.add(formFile);
            }
            formFileService.saveBatch(list);
        }
        return true;
    }

    @Override
    public void updateCode(InOutVO inOutVO) {
        OutContract outcontract = this.getById(inOutVO.getId());
        outcontract.setContractCode(inOutVO.getContractCode());
        if (ObjectUtil.isEmpty(outcontract.getWbs())) {
            outcontract.setWbs(inOutVO.getWbs());

            List<BudgetProject> list = budgetProjectService.list(new LambdaQueryWrapper<BudgetProject>().eq(BudgetProject::getProjectId, inOutVO.getProjectId()));
            if (ObjectUtil.isNotEmpty(list)) {
                budgetProjectService.updateBatchById(list);
            }

            InContractService inContractService = SpringUtil.getBean(InContractService.class);
            List<InContract> list2 = inContractService.list(new LambdaQueryWrapper<InContract>().eq(InContract::getProjectId, inOutVO.getProjectId()));
            if (ObjectUtil.isNotEmpty(list2)) {
                list2.forEach(item -> item.setWbs(inOutVO.getWbs()));
                inContractService.updateBatchById(list2);
            }
        }
        this.updateById(outcontract);
    }

    @Override
    public boolean add(OutContract outContract) {
        if (ObjectUtil.isEmpty(outContract.getWbs())) {
            throw new PageTipException("必须有WBS编号，如果没有，合同签署情况->合同号和WBS号,进行补全");
        }
        //先有收款合同，才能进行付款合同
        List<InContract> ll = inContractService.list(new LambdaQueryWrapper<InContract>().eq(InContract::getTaskCode, outContract.getTaskCode()));
        if (ObjectUtil.isEmpty(ll)) {
            throw new PageTipException("必须先有收款合同，才能进行付款合同");
        }
        this.save(outContract);
        //文件
        List<FormFile> list = new ArrayList<>();
        List<FileVO> fileList = outContract.getFileList();
        if (ObjectUtil.isNotEmpty(fileList)) {
            for (FileVO fileVO : fileList) {
                FormFile formFile = new FormFile();
                formFile.setType("OutContract");
                formFile.setBusinessId(outContract.getId());
                formFile.setName(fileVO.getName());
                formFile.setUrl(fileVO.getUrl());
                list.add(formFile);
            }
            formFileService.saveBatch(list);
        }
        return true;
    }
}
