package com.haiying.project.model.vo;

import com.haiying.project.model.entity.ProjectIn;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class ProjectInVO extends ProjectIn {
    private List<ProjectIn> list;
}
