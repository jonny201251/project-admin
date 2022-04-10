package com.haiying.project.common.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReflectUtil;
import com.haiying.project.model.vo.TreeSelect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TreeUtil {
    //树形表格
    public static <T> List<T> getTree(List<T> list) {
        if (CollUtil.isEmpty(list)) return new ArrayList<>();
        List<T> resultList = new ArrayList<>();
        Map<Integer, T> map = new HashMap<>();
        for (T t : list) {
            Integer id = (Integer) ReflectUtil.getFieldValue(t, "id");
            map.put(id, t);
        }
        for (T t : list) {
            Integer id = (Integer) ReflectUtil.getFieldValue(t, "id");
            Integer pid = (Integer) ReflectUtil.getFieldValue(t, "pid");
            T parent = map.get(pid);
            if (parent != null) {
                Object childen = ReflectUtil.getFieldValue(parent, "children");
                if (childen != null) {
                    ReflectUtil.invoke(childen, "add", map.get(id));
                } else {
                    List<T> tmp = new ArrayList<>();
                    tmp.add(map.get(id));
                    ReflectUtil.setFieldValue(parent, "children", tmp);
                }
            } else {
                resultList.add(t);
            }
        }

        return resultList;
    }

    //下拉树
    public static <T> List<TreeSelect> getTreeSelect(List<T> list) {
        if (CollUtil.isEmpty(list)) return new ArrayList<>();
        List<TreeSelect> treeList = new ArrayList<>();
        Map<Integer, TreeSelect> map = new HashMap<>();

        for (T t : list) {
            Integer id = (Integer) ReflectUtil.getFieldValue(t, "id");
            String name = (String) ReflectUtil.getFieldValue(t, "name");
            TreeSelect treeSelect = new TreeSelect();
            treeSelect.setTitle(name);
            treeSelect.setKey(id);
            treeSelect.setValue(id);
            map.put(id, treeSelect);
        }

        for (T t : list) {
            Integer id = (Integer) ReflectUtil.getFieldValue(t, "id");
            Integer pid = (Integer) ReflectUtil.getFieldValue(t, "pid");
            TreeSelect parent = map.get(pid);
            if (parent != null) {
                if (parent.getChildren() != null) {
                    parent.getChildren().add(map.get(id));
                } else {
                    List<TreeSelect> tmp = new ArrayList<>();
                    tmp.add(map.get(id));
                    parent.setChildren(tmp);
                }
            } else {
                treeList.add(map.get(id));
            }
        }
        return treeList;
    }
}
