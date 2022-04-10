package com.haiying.project.model.vo;

import com.haiying.project.model.entity.OtherPower;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class OtherPowerAfter extends ProcessFormAfter {
    private OtherPower formValue;
}
