package com.haiying.project.controller.base;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.haiying.project.common.utils.SpringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BaseController<T> {
    protected IService<T> service;
    @Autowired
    SpringUtil springUtil;

    public BaseController() {
        //哪个子类调用此方法，得到的class就是子类处理的类型
        Class clazz = this.getClass();
        ParameterizedType pt = (ParameterizedType) clazz.getGenericSuperclass();
        clazz = (Class) pt.getActualTypeArguments()[0];
        //SysDic,首字母变为小写
        String name = StringUtils.uncapitalize(clazz.getSimpleName());
        service = (IService<T>) springUtil.getBean(name + "ServiceImpl");
    }

    @PostMapping("list")
    public IPage<T> list(@RequestBody Map<String,Object> paramMap) {
        Integer current= (Integer) paramMap.get("current");
        Integer pageSize= (Integer) paramMap.get("pageSize");
        return service.page(new Page<>(current, pageSize), new LambdaQueryWrapper<>());
    }

    @PostMapping("add")
    public boolean add(@RequestBody T t) {
        return service.save(t);
    }

    @GetMapping("get")
    public T get(String id) {
        return service.getById(id);
    }

    @PostMapping("edit")
    public boolean edit(@RequestBody T t) {
        return service.updateById(t);
    }

    @GetMapping("delete")
    public boolean delete(String[] arr) {
        List<String> list = Stream.of(arr).collect(Collectors.toList());
        return service.removeByIds(list);
    }
}
