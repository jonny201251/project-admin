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
 * 供方资格取消
 * </p>
 *
 * @author 作者
 * @since 2023-08-10
 */
@Getter
@Setter
@TableName("provider_cancel")
public class ProviderCancel implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer baseId;

    private Integer beforeId;

    private String haveDisplay;

    private Integer version;

    private Integer providerId;

    private String usee;

    private String type;

    private String name;

    private String userName;

    private String tel;

    private String desc1;

    private String codeMoney;

    private String desc2;

    private Integer processInstId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDatetime;

    private String displayName;

    private String loginName;

    private Integer deptId;

    private String deptName;

    private String remark;

    @TableField(exist = false)
    private ProcessInst processInst;

    @TableField(exist = false)
    private List<FileVO> fileList;

}
