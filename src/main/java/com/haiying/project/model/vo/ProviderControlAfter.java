package com.haiying.project.model.vo;

import com.haiying.project.model.entity.ProviderControl;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ProviderControlAfter extends ProcessFormAfter {
    private ProviderControl formValue;
}
