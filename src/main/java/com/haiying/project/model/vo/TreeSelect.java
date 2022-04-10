package com.haiying.project.model.vo;

import lombok.Data;

import java.util.List;

@Data
public class TreeSelect {
    private String title;
    private Object value;
    private Object key;
    private List<TreeSelect> children;
}
