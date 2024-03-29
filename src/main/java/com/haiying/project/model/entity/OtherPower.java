package com.haiying.project.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.haiying.project.model.vo.FileVO;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 其他授权
 * </p>
 *
 * @author 作者
 * @since 2022-04-08
 */
@Getter
@Setter
@TableName("other_power")
public class OtherPower implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer baseId;

    private Integer beforeId;

    private String haveDisplay;

    private Integer version;

    private String displayName;

    private String loginName;

    private Integer deptId;

    private String deptName;

    /**
     * 被授权人
     */
    private String displayNamee;

    /**
     * 被授权人
     */
    private String loginNamee;

    /**
     * 职务
     */
    private String job;

    /**
     * 被授权人部门
     */
    private Integer deptIdd;

    /**
     * 被授权人部门
     */
    private String deptNamee;

    /**
     * 授权号
     */
    private String code;

    /**
     * 申请事项及权限
     */
    private String descc;

    @TableField(exist = false)
    private List<String> timeLimitTmp;
    /**
     * 申请期限
     */
    private String timeLimit;

    /**
     * 流程最后一个审批人，公司主管领导，董事长
     */
    private String endType;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDatetime;

    private Integer processInstId;

    private Integer year;

    private String status;

    @TableField(exist = false)
    private ProcessInst processInst;

    @TableField(exist = false)
    private List<FileVO> fileList;

    //归口管理部门
    @TableField(exist = false)
    private List<String> userNameeList;
    private String userNamee;
}
