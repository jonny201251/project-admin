package com.haiying.project.model.vo;

import com.haiying.project.model.entity.ProviderScore1;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ProviderScore1After extends ProcessFormAfter {
    private ProviderScore1 formValue;
}
