package com.haiying.project.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haiying.project.common.exception.PageTipException;
import com.haiying.project.mapper.OutContractMapper;
import com.haiying.project.model.entity.*;
import com.haiying.project.model.vo.FileVO;
import com.haiying.project.model.vo.InOutVO;
import com.haiying.project.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.Optional.ofNullable;

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
    BudgetProjecttService budgetProjecttService;
    @Autowired
    BudgetOutService budgetOutService;
    @Autowired
    ContractMoneyService contractMoneyService;
    @Autowired
    HttpSession httpSession;


    //收款合同金额
    public void contractMoney(OutContract page, OutContract db) {
        if (!page.getContractMoney().equals(db.getContractMoney())) {
            ContractMoney tmp = new ContractMoney();
            tmp.setType("付款合同");
            tmp.setContractCode(db.getContractCode());
            tmp.setContractMoney(db.getContractMoney());
            tmp.setCreateDatetime(LocalDateTime.now());

            SysUser user = (SysUser) httpSession.getAttribute("user");
            tmp.setLoginName(user.getLoginName());
            tmp.setDisplayName(user.getDisplayName());
            tmp.setDeptId(user.getDeptId());
            tmp.setDeptName(user.getDeptName());

            contractMoneyService.save(tmp);
        }
    }

    @Override
    public boolean edit(OutContract outContract) {
        validate(outContract);

        contractMoney(outContract, this.getById(outContract.getId()));

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
        if (ObjectUtil.isNotEmpty(inOutVO.getContractCode())) {
            outcontract.setContractCode(inOutVO.getContractCode());
        }
        if (ObjectUtil.isNotEmpty(inOutVO.getWbs())) {
            outcontract.setWbs(inOutVO.getWbs());
        }
        this.updateById(outcontract);

        List<BudgetProjectt> list1 = budgetProjecttService.list(new LambdaQueryWrapper<BudgetProjectt>().eq(BudgetProjectt::getTaskCode, inOutVO.getTaskCode()));
        if (ObjectUtil.isNotEmpty(list1)) {
            list1.forEach(item -> {
                item.setWbs(inOutVO.getWbs());
            });
            budgetProjecttService.updateBatchById(list1);
        }
    }

    private void validate(OutContract outContract) {
        if (ObjectUtil.isEmpty(outContract.getWbs())) {
            throw new PageTipException("必须有WBS编号，如果没有，合同签署情况->合同号和WBS号,进行补全");
        }
        //先有收款合同，才能进行付款合同
        List<InContract> ll = inContractService.list(new LambdaQueryWrapper<InContract>().eq(InContract::getTaskCode, outContract.getTaskCode()));
        if (ObjectUtil.isEmpty(ll)) {
            throw new PageTipException("必须先有收款合同，才能进行付款合同");
        }
        //有、无合同的，跟 预算中的费用比较
        LambdaQueryWrapper<BudgetProjectt> wrapper = new LambdaQueryWrapper<BudgetProjectt>().eq(BudgetProjectt::getHaveDisplay, "是").eq(BudgetProjectt::getTaskCode, outContract.getTaskCode());
        BudgetProjectt b = budgetProjecttService.getOne(wrapper);
        LambdaQueryWrapper<BudgetOut> wrapper2=new LambdaQueryWrapper<>();
        if (ObjectUtil.isNotEmpty(outContract.getCostRate())) {
            wrapper2.eq(BudgetOut::getRate, outContract.getCostRate());
        }
        List<BudgetOut> ll2 = budgetOutService.list(wrapper2);
        if (ObjectUtil.isNotEmpty(ll2)) {
            double totalCost = 0.0;
            for (BudgetOut out : ll2) {
                totalCost += ofNullable(out.getMoney()).orElse(0.0);
            }
            if (outContract.getContractMoney() > totalCost) {
                throw new PageTipException("付款金额:" + outContract.getContractMoney() + " ,超出预算额:" + totalCost);
            }
        } else {
            throw new PageTipException(outContract.getName() + "没有做预算");
        }
    }

    @Override
    public boolean add(OutContract outContract) {
        validate(outContract);

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
