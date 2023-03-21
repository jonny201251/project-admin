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
 * 供方动态监控
 * </p>
 *
 * @author 作者
 * @since 2023-03-17
 */
@Getter
@Setter
@TableName("provider_control")
public class ProviderControl implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer baseId;

    private Integer beforeId;

    private String haveDisplay;

    private Integer version;

    private String usee;

    private Integer providerId;

    private String name;

    /**
     * 变动说明
     */
    private String descc;

    private String displayName;

    private String loginName;

    private Integer deptId;

    private String deptName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDatetime;

    /**
     * 结论
     */
    private String result;

    private String remark;

    private Integer processInstId;

    @TableField(exist = false)
    private ProcessInst processInst;

    @TableField(exist = false)
    private List<FileVO> fileList;

    //评审部门
    @TableField(exist = false)
    private List<String> userNameeList;
    private String userNamee;

}
