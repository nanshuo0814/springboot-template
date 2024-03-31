package com.nanshuo.springboot.model.dto;

import com.nanshuo.springboot.annotation.CheckParam;
import lombok.Data;

import java.io.Serializable;

/**
 * id请求
 *
 * @author nanshuo
 * @date 2024/03/31 12:12:29
 */
@Data
public class IdRequest implements Serializable {

    /**
     * id
     */
    @CheckParam(alias = "id", minValue = 0)
    private Long id;

    private static final long serialVersionUID = 1L;
}