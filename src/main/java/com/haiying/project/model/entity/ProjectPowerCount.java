package com.haiying.project.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 项目立项授权，其他授权
 * </p>
 *
 * @author 作者
 * @since 2022-03-30
 */
@Getter
@Setter
@TableName("project_power_count")
public class ProjectPowerCount implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String year;

    private Integer count;


}
