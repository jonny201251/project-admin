package com.haiying.project.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 一般和重大项目的保证金登记表
 * </p>
 *
 * @author 作者
 * @since 2023-03-22
 */
@Getter
@Setter
@TableName("project_protect")
public class ProjectProtect implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer projectId;

    private String type;

    private String name;

    private String wbs;

    private String taskCode;

    private String property;

    /**
     * 中标，未中标，终止
     */
    private String status;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate registeDate;

    private Double money;

    private String outName;

    private String inName;

    private String code;

    private String displayName;

    private String loginName;

    private Integer deptId;

    private String deptName;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDatetime;

    private Integer processInstId;
    @TableField(exist = false)
    private ProcessInst processInst;

    @TableField(exist = false)
    private List<String> userNameeList;
    private String userNamee;

    private String remark;


}
