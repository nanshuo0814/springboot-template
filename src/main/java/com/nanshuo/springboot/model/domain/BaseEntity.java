package com.nanshuo.springboot.model.domain;


import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 基础实体
 *
 * @author nanshuo
 * @date 2024/03/31 17:11:03
 */
@Data
public class BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private Long id;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

}
