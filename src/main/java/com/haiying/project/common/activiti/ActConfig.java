package com.haiying.project.common.activiti;

import org.activiti.engine.delegate.event.ActivitiEventListener;
import org.activiti.spring.SpringProcessEngineConfiguration;
import org.activiti.spring.boot.ProcessEngineConfigurationConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class ActConfig implements ProcessEngineConfigurationConfigurer {
    @Autowired
    ActivitiEventListener activitiEventListener;

    @Override
    public void configure(SpringProcessEngineConfiguration springProcessEngineConfiguration) {
        springProcessEngineConfiguration.setEventListeners(Collections.singletonList(activitiEventListener));
    }
}
