package com.haiying.project.model.vo;

import com.haiying.project.model.entity.CustomerScore1;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class CustomerScore1After extends ProcessFormAfter {
    private CustomerScore1 formValue;
}
