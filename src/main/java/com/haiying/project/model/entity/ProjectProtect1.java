package com.haiying.project.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 一般和重大项目的保证金登记表1
 * </p>
 *
 * @author 作者
 * @since 2022-11-21
 */
@Getter
@Setter
@TableName("project_protect1")
public class ProjectProtect1 implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String name;

    private String taskCode;

    private String property;

    /**
     * 中标，未中标，终止
     */
    private String status;

    private String displayName;

    private String loginName;

    private Integer deptId;

    private String deptName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDatetime;

    @TableField(exist = false)
    private List<ProjectProtect2> list;

}
