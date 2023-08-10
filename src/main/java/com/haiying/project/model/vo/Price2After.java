package com.haiying.project.model.vo;

import com.haiying.project.model.entity.Price2;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class Price2After extends ProcessFormAfter {
    private Price2 formValue;
}
