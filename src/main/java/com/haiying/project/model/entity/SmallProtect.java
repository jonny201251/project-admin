package com.haiying.project.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 一般项目的保证金(函)
 * </p>
 *
 * @author 作者
 * @since 2022-03-28
 */
@Getter
@Setter
@TableName("small_protect")
public class SmallProtect implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String type;

    private Double money;

    private Integer smallProjectId;


}
