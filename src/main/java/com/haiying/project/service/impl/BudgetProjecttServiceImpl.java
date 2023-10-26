package com.haiying.project.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haiying.project.bean.ButtonHandleBean;
import com.haiying.project.common.exception.PageTipException;
import com.haiying.project.mapper.BudgetProjecttMapper;
import com.haiying.project.model.entity.*;
import com.haiying.project.model.vo.BudgetProjecttAfter;
import com.haiying.project.model.vo.FileVO;
import com.haiying.project.service.*;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 一般和重大项目预算-项目 服务实现类
 * </p>
 *
 * @author 作者
 * @since 2023-04-13
 */
@Service
public class BudgetProjecttServiceImpl extends ServiceImpl<BudgetProjecttMapper, BudgetProjectt> implements BudgetProjecttService {
    @Autowired
    ButtonHandleBean buttonHandleBean;
    @Autowired
    ProcessInstService processInstService;
    @Autowired
    FormFileService formFileService;
    @Autowired
    HttpSession httpSession;
    @Autowired
    BudgetProtectService budgetProtectService;
    @Autowired
    BudgetInnService budgetInnService;
    @Autowired
    BudgetOutService budgetOutService;
    @Autowired
    SmallProjectService smallProjectService;
    @Autowired
    SmallProjectNoService smallProjectNoService;
    @Autowired
    BigProjectService bigProjectService;
    @Autowired
    ProjectCodeService projectCodeService;

    private void validate(BudgetProjectt formValue) {
        //页面的毛利率>立项时的毛利率
        double page = Double.parseDouble(formValue.getProjectRate().replaceAll("%", ""));
        double build;
        String tmp = "";
        if (formValue.getProjectType().equals("一般项目")) {
            tmp = smallProjectService.getById(formValue.getProjectId()).getProjectRate();
        } else if (formValue.getProjectType().equals("重大项目")) {
            tmp = bigProjectService.getById(formValue.getProjectId()).getProjectRate();
        } else if (formValue.getProjectType().equals("一般项目非")) {
            tmp = smallProjectNoService.getById(formValue.getProjectId()).getProjectRate();
        }
        if (ObjectUtil.isEmpty(tmp)) {
            throw new PageTipException("立项时的项目毛利率为空，联系管理员补全");
        }
        build = Double.parseDouble(tmp.replaceAll("%", ""));
        if (page < build) {
            if(!"101101323001".equals(formValue.getTaskCode())){
                throw new PageTipException("预计毛利率低于立项时的毛利率");
            }
        }

        //合同金额>=预计收入累计
        if (formValue.getContractMoney() < formValue.getInSum()) {
            throw new PageTipException("合同金额 必须 大于等于 预计收入累计");
        }

        if (!formValue.getProtectRate().endsWith("%")) {
            formValue.setProtectRate(formValue.getProtectRate() + "%");
        }
        List<String> rateList = new ArrayList<>();
        if (ObjectUtil.isEmpty(formValue.getInvoiceRate())) {
            throw new PageTipException("预计收入.税率 不能为空");
        }
        String[] arr = formValue.getInvoiceRate().split(",|，");
        for (String str : arr) {
            if (!str.endsWith("%")) {
                rateList.add(str + "%");
            } else {
                rateList.add(str);
            }
        }
        formValue.setInvoiceRate(Strings.join(rateList, '，'));
    }

    //备案号
    public void taskCode(BudgetProjectt formValue) {
        ProjectCode code = projectCodeService.getOne(new LambdaQueryWrapper<ProjectCode>().eq(ProjectCode::getTaskCode, formValue.getTaskCode()));
        if (code == null) {
            throw new PageTipException("项目备案里没有备案号，联系管理员(张强)");
        }
        if (code.getHaveMoreBudget().equals("是")) {
            LambdaQueryWrapper<BudgetProjectt> wrapper = new LambdaQueryWrapper<BudgetProjectt>().likeRight(BudgetProjectt::getTaskCode, formValue.getTaskCode()).orderByDesc(BudgetProjectt::getId);
            List<BudgetProjectt> list = this.list(wrapper);
            if (list.size() > 0) {
                BudgetProjectt tmp = list.get(0);
                String taskCode = tmp.getTaskCode();
                String[] arr = taskCode.split("-");
                String index = (Integer.parseInt(arr[1]) + 1) + "";
                if (index.length() == 1) {
                    formValue.setTaskCode(formValue.getTaskCode() + "-00" + index);
                } else if (index.length() == 2) {
                    formValue.setTaskCode(formValue.getTaskCode() + "-0" + index);
                } else {
                    formValue.setTaskCode(formValue.getTaskCode() + "-" + index);
                }
            } else {
                formValue.setTaskCode(formValue.getTaskCode() + "-001");
            }
        }
    }

    private void add(BudgetProjectt formValue) {
        //任务号
        taskCode(formValue);
        //判断是否重复添加
        List<BudgetProjectt> ll = this.list(new LambdaQueryWrapper<BudgetProjectt>().eq(BudgetProjectt::getHaveDisplay, "是").eq(BudgetProjectt::getTaskCode, formValue.getTaskCode()));
        if (ObjectUtil.isNotEmpty(ll)) {
            throw new PageTipException("备案号   已存在");
        }
        formValue.setHaveDisplay("是");
        formValue.setVersion(0);
        //
        validate(formValue);

        formValue.setName(formValue.getName().replaceAll("\\s+",""));
        this.save(formValue);
        //
        List<BudgetProtect> protectList = formValue.getProtectList();
        protectList.forEach(item -> {
            item.setBudgetId(formValue.getId());
            item.setProjectId(formValue.getProjectId());
            item.setProjectType(formValue.getProjectType());
        });
        budgetProtectService.saveBatch(protectList);
        //
        List<BudgetInn> innList = formValue.getInnList();
        innList.forEach(item -> {
            item.setBudgetId(formValue.getId());
            item.setProjectId(formValue.getProjectId());
            item.setProjectType(formValue.getProjectType());
        });
        budgetInnService.saveBatch(innList);
        //
        List<BudgetOut> outList = formValue.getOutList();
        outList.forEach(item -> {
            item.setBudgetId(formValue.getId());
            item.setProjectId(formValue.getProjectId());
            item.setProjectType(formValue.getProjectType());
        });
        budgetOutService.saveBatch(outList);
        //文件
        List<FormFile> list = new ArrayList<>();
        List<FileVO> fileList = formValue.getFileList();
        if (ObjectUtil.isNotEmpty(fileList)) {
            for (FileVO fileVO : fileList) {
                FormFile formFile = new FormFile();
                formFile.setType("BudgetProjectt");
                formFile.setBusinessId(formValue.getId());
                formFile.setName(fileVO.getName());
                formFile.setUrl(fileVO.getUrl());
                list.add(formFile);
            }
            formFileService.saveBatch(list);
        }


    }

    private void edit(BudgetProjectt formValue) {
        //
        validate(formValue);

        formValue.setName(formValue.getName().replaceAll("\\s+",""));
        this.updateById(formValue);
        //
        budgetProtectService.remove(new LambdaQueryWrapper<BudgetProtect>().eq(BudgetProtect::getBudgetId, formValue.getId()));
        List<BudgetProtect> protectList = formValue.getProtectList();
        protectList.forEach(item -> {
            item.setBudgetId(formValue.getId());
            item.setProjectId(formValue.getProjectId());
            item.setProjectType(formValue.getProjectType());
        });
        budgetProtectService.saveBatch(protectList);
        //
        budgetInnService.remove(new LambdaQueryWrapper<BudgetInn>().eq(BudgetInn::getBudgetId, formValue.getId()));
        List<BudgetInn> innList = formValue.getInnList();
        innList.forEach(item -> {
            item.setBudgetId(formValue.getId());
            item.setProjectId(formValue.getProjectId());
            item.setProjectType(formValue.getProjectType());
        });
        budgetInnService.saveBatch(innList);
        //
        budgetOutService.remove(new LambdaQueryWrapper<BudgetOut>().eq(BudgetOut::getBudgetId, formValue.getId()));
        List<BudgetOut> outList = formValue.getOutList();
        outList.forEach(item -> {
            item.setBudgetId(formValue.getId());
            item.setProjectId(formValue.getProjectId());
            item.setProjectType(formValue.getProjectType());
        });
        budgetOutService.saveBatch(outList);
        //文件
        formFileService.remove(new LambdaQueryWrapper<FormFile>().eq(FormFile::getType, "BudgetProjectt").eq(FormFile::getBusinessId, formValue.getId()));
        List<FileVO> fileList = formValue.getFileList();
        if (ObjectUtil.isNotEmpty(fileList)) {
            List<FormFile> list = new ArrayList<>();
            for (FileVO fileVO : fileList) {
                FormFile formFile = new FormFile();
                formFile.setType("BudgetProjectt");
                formFile.setBusinessId(formValue.getId());
                formFile.setName(fileVO.getName());
                formFile.setUrl(fileVO.getUrl());
                list.add(formFile);
            }
            formFileService.saveBatch(list);
        }
    }

    private void delete(BudgetProjectt formValue) {
        this.removeById(formValue.getId());
        budgetProtectService.remove(new LambdaQueryWrapper<BudgetProtect>().eq(BudgetProtect::getBudgetId, formValue.getId()));
        budgetInnService.remove(new LambdaQueryWrapper<BudgetInn>().eq(BudgetInn::getBudgetId, formValue.getId()));
        budgetOutService.remove(new LambdaQueryWrapper<BudgetOut>().eq(BudgetOut::getBudgetId, formValue.getId()));
        formFileService.remove(new LambdaQueryWrapper<FormFile>().eq(FormFile::getType, "BudgetProjectt").eq(FormFile::getBusinessId, formValue.getId()));
        Integer beforeId = formValue.getBeforeId();
        if (beforeId != null) {
            BudgetProjectt before = this.getById(beforeId);
            before.setHaveDisplay("是");
            this.updateById(before);
        }
    }

    public boolean change(BudgetProjectt current) {
        BudgetProjectt before = this.getById(current.getId());
        before.setHaveDisplay("否");
        //
        validate(current);

        this.updateById(before);
        //
        current.setId(null);
        current.setProcessInstId(null);
        current.setBeforeId(before.getId());
        current.setHaveDisplay("是");
        current.setVersion(current.getVersion() + 1);
        if (current.getBaseId() == null) {
            //第一次修改
            current.setBaseId(before.getId());
        } else {
            //第二、三、N次修改
            current.setBaseId(before.getBaseId());
        }
        this.save(current);
        //
        List<BudgetProtect> protectList = current.getProtectList();
        protectList.forEach(item -> {
            item.setId(null);
            item.setBudgetId(current.getId());
            item.setProjectId(current.getProjectId());
            item.setProjectType(current.getProjectType());
        });
        budgetProtectService.saveBatch(protectList);
        //
        List<BudgetInn> innList = current.getInnList();
        innList.forEach(item -> {
            item.setId(null);
            item.setBudgetId(current.getId());
            item.setProjectId(current.getProjectId());
            item.setProjectType(current.getProjectType());
        });
        budgetInnService.saveBatch(innList);
        //
        List<BudgetOut> outList = current.getOutList();
        outList.forEach(item -> {
            item.setId(null);
            item.setBudgetId(current.getId());
            item.setProjectId(current.getProjectId());
            item.setProjectType(current.getProjectType());
        });
        budgetOutService.saveBatch(outList);
        //文件
        List<FileVO> fileList = current.getFileList();
        if (ObjectUtil.isNotEmpty(fileList)) {
            List<FormFile> list = new ArrayList<>();
            for (FileVO fileVO : fileList) {
                FormFile formFile = new FormFile();
                formFile.setType("BudgetProjectt");
                formFile.setBusinessId(current.getId());
                formFile.setName(fileVO.getName());
                formFile.setUrl(fileVO.getUrl());
                list.add(formFile);
            }
            formFileService.saveBatch(list);
        }
        return true;
    }


    @Override
    public boolean btnHandle(BudgetProjecttAfter after) {
        BudgetProjectt formValue = after.getFormValue();
        String type = after.getType();
        String buttonName = after.getButtonName();
        String path = after.getPath();
        String comment = after.getComment();

        if (formValue.getProjectType().equals("重大项目")) {
            path = "bigBudgetProjecttPath";
        }

        if (type.equals("add")) {
            if (buttonName.equals("草稿")) {
                add(formValue);
            } else {
                add(formValue);
                Integer processInstId = buttonHandleBean.addEdit(path, formValue, buttonName, formValue.getId(), formValue.getName());
                //
                formValue.setProcessInstId(processInstId);
                this.updateById(formValue);
            }
        } else if (type.equals("edit")) {
            if (buttonName.equals("草稿")) {
                edit(formValue);
            } else {
                edit(formValue);
                Integer processInstId = buttonHandleBean.addEdit(path, formValue, buttonName, formValue.getId(), formValue.getName());
                //
                formValue.setProcessInstId(processInstId);
                this.updateById(formValue);
            }
        } else if (type.equals("check") || type.equals("reject")) {
            String haveEditForm = after.getHaveEditForm();
            if (haveEditForm.equals("是")) {
                edit(formValue);
            }
            //
            SysUser user = (SysUser) httpSession.getAttribute("user");
            if (user.getDisplayName().equals("于欣坤") && formValue.getHaveThree() != null) {
                BudgetProjectt tmp = this.getById(formValue.getId());
                tmp.setHaveThree(formValue.getHaveThree());
                this.updateById(tmp);
            }
            //
            ProcessInst processInst = processInstService.getById(formValue.getProcessInstId());
            String[] tmp = processInst.getLoginProcessStep().split(",");
            if (tmp.length > 1 && (buttonName.contains("同意") || buttonName.contains("提交业务主管领导"))) {
                buttonHandleBean.checkUpOne(formValue.getProcessInstId(), formValue, buttonName, comment);
            } else {
                buttonHandleBean.checkReject(formValue.getProcessInstId(), formValue, buttonName, comment);
            }
        } else if (type.equals("recall")) {
            buttonHandleBean.recall(formValue.getProcessInstId(), buttonName);
        } else if (type.equals("delete")) {
            delete(formValue);
            buttonHandleBean.delete(formValue.getProcessInstId());
        } else if (type.equals("change")) {
            ProcessInst before = processInstService.getById(formValue.getProcessInstId());
            before.setBusinessHaveDisplay("否");
            processInstService.updateById(before);
            change(formValue);
            Integer newProcessInstId = buttonHandleBean.change(before, path, formValue, buttonName, formValue.getId(), formValue.getName(), comment);
            formValue.setProcessInstId(newProcessInstId);
            this.updateById(formValue);
        }
        return true;
    }
}
