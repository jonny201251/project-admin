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
 * 一般和重大项目立项时，授权信息
 * </p>
 *
 * @author 作者
 * @since 2022-03-24
 */
@Getter
@Setter
@TableName("project_power")
public class ProjectPower implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 被授权人
     */
    private String displayNamee;

    /**
     * 被授权人
     */
    private String loginNamee;

    @TableField(exist = false)
    private List<String> timeLimitTmp;
    /**
     * 申请期限
     */
    private String timeLimit;

    /**
     * 授权事项及权限
     */
    private String descc;

    /**
     * 授权号
     */
    private String code;

    /**
     * 是否被使用
     */
    private String status;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDatetime;

    private String displayName;

    private String loginName;

    private Integer deptId;

    private String deptName;

    private String remark;

    /**
     * 该授权号用于哪个项目
     */
    private String projectName;

    private Integer processInstId;

    @TableField(exist = false)
    private ProcessInst processInst;
}
