package com.haiying.project.model.vo;

import com.haiying.project.model.entity.ProcessDesign;
import com.haiying.project.model.entity.ProcessDesignEdge;
import com.haiying.project.model.entity.ProcessDesignTask;
import lombok.Data;

import java.util.List;

@Data
public class ProcessDesignVO {
    private ProcessDesign processDesign;
    private List<ProcessDesignTask> taskList;
    private List<ProcessDesignEdge> edgeList;
}
