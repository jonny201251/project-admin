package com.haiying.project.model.vo;

import com.haiying.project.model.entity.ProjectOut;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ProjectOutAfter extends ProcessFormAfter {
    private ProjectOut formValue;
}
