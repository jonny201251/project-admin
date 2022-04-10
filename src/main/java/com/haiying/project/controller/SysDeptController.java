package com.haiying.project.controller;


import cn.hutool.core.io.FileUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.haiying.project.common.result.Wrapper;
import com.haiying.project.common.utils.TreeUtil;
import com.haiying.project.controller.base.BaseTreeController;
import com.haiying.project.model.entity.SysDept;
import com.haiying.project.model.entity.SysUser;
import com.haiying.project.model.vo.TreeSelect;
import com.haiying.project.service.SysDeptService;
import com.haiying.project.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 部门 前端控制器
 * </p>
 *
 * @author 作者
 * @since 2022-02-15
 */
@RestController
@RequestMapping("/sysDept")
@Wrapper
public class SysDeptController extends BaseTreeController<SysDept> {
    @Autowired
    SysDeptService sysDeptService;
    @Autowired
    SysUserService sysUserService;

    @GetMapping("getTreeSelect")
    public List<TreeSelect> getTreeSelect() {
        List<SysDept> list = sysDeptService.list();
        return TreeUtil.getTreeSelect(list);
    }

    @GetMapping("aa")
    public boolean aa(){
        List<SysUser> data=new ArrayList<>();
        List<String> list = FileUtil.readLines("d:/部门.txt", "utf-8");
        for (String s : list) {
           SysUser user=new SysUser();
            String[] arr = s.split(",");
            user.setDisplayName(arr[0]);
            user.setLoginName(arr[0]);
            SysDept dept=sysDeptService.getOne(new QueryWrapper<SysDept>().eq("name",arr[1]));
            if(dept!=null){
                user.setDeptId(dept.getId());
                user.setDeptName(dept.getName());
                user.setGender(arr[2]);
                user.setPassword(SecureUtil.md5("1"));
                user.setStatus("禁用");
                data.add(user);
            }else{
                System.out.println(arr[1]);
            }

        }
        sysUserService.saveBatch(data);
        return true;
    }

    @GetMapping("bb")
    public boolean bb(){
        List<SysUser> data=sysUserService.list();
        Map<String,String> map=new HashMap<>();
        List<String> list = FileUtil.readLines("d:/部门.txt", "utf-8");
        for (String s : list) {
            String[] arr = s.split(",");
            map.put(arr[0],arr[1]);
        }
        for (SysUser user : data) {
            user.setPosition(map.get(user.getDisplayName()));
        }
        sysUserService.updateBatchById(data);
        return true;
    }
}
