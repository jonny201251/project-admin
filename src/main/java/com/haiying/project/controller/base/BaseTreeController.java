package com.haiying.project.controller.base;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.haiying.project.common.utils.SpringUtil;
import com.haiying.project.common.utils.TreeUtil;
import com.haiying.project.model.vo.TreeSelect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BaseTreeController<T> {
    protected IService<T> service;
    @Autowired
    SpringUtil springUtil;

    public BaseTreeController() {
        //哪个子类调用此方法，得到的class就是子类处理的类型
        Class clazz = this.getClass();
        ParameterizedType pt = (ParameterizedType) clazz.getGenericSuperclass();
        clazz = (Class) pt.getActualTypeArguments()[0];
        //SysDic,首字母变为小写
        String name = StringUtils.uncapitalize(clazz.getSimpleName());
        service = (IService<T>) springUtil.getBean(name + "ServiceImpl");
    }

    @PostMapping("list")
    public List<T> list(@RequestBody Map<String,Object> paramMap) {
        Integer current= (Integer) paramMap.get("current");
        Integer pageSize= (Integer) paramMap.get("pageSize");
        List<T> list = service.list();
        return TreeUtil.getTree(list);
    }

    @PostMapping("add")
    public boolean add(@RequestBody T t) {
        return service.save(t);
    }

    @GetMapping("get")
    public T getById(String id) {
        return service.getById(id);
    }

    @PostMapping("edit")
    public boolean edit(@RequestBody T t) {
        return service.updateById(t);
    }

    @GetMapping("delete")
    public boolean delete(Integer[] arr) {
        List<Integer> idList = Stream.of(arr).collect(Collectors.toList());
        //根据idList，取出所有的子节点
        List<Integer> list = new ArrayList<>(idList);
        while (true) {
            List<T> tmp = service.list(new QueryWrapper<T>().in("pid", idList));
            if (ObjectUtil.isEmpty(tmp)) {
                break;
            } else {
                for (T t : tmp) {
                    Integer id = (Integer) ReflectUtil.getFieldValue(t, "id");
                    list.add(id);
                }
            }
        }
        return service.remove(new QueryWrapper<T>().in("id", list));
    }

    @GetMapping("getTreeSelect")
    public List<TreeSelect> getTreeSelect() {
        List<T> list = service.list(new QueryWrapper<T>().orderByAsc("sort"));
        return TreeUtil.getTreeSelect(list);
    }
}
