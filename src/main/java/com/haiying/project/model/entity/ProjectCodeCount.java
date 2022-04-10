package com.haiying.project.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * <p>
 * 项目任务号-部门年度编号
 * </p>
 *
 * @author 作者
 * @since 2022-03-30
 */
@Getter
@Setter
@TableName("project_code_count")
public class ProjectCodeCount implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer deptId;

    private String deptName;
    private String deptType;

    private String year;

    private Integer count;


}
