package com.haiying.project.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 客户信用评分2
 * </p>
 *
 * @author 作者
 * @since 2022-03-22
 */
@Getter
@Setter
@TableName("customer_score2")
public class CustomerScore2 implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer customerScore1Id;

    private String kpi;

    private String item;

    private String standard;

    private Integer startScore;

    private Integer endScore;


}
