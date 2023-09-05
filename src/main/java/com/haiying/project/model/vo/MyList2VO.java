package com.haiying.project.model.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MyList2VO {
    private String type;
    private Integer id;
    private String name;
    private String deptName;
    private String displayName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDatetime;
}
