package com.haiying.project.model.vo;

import com.haiying.project.model.entity.ProjectPower;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ProjectPowerAfter extends ProcessFormAfter {
    private ProjectPower formValue;
}
