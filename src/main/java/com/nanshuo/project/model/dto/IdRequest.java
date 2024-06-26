package com.nanshuo.project.model.dto;

import com.nanshuo.project.annotation.CheckParam;
import lombok.Data;

import java.io.Serializable;

/**
 * id请求
 *
 * @author <a href="https://github.com/nanshuo0814">nanshuo(南烁)</a>
 * @date 2024/03/31 12:12:29
 */
@Data
public class IdRequest implements Serializable {

    /**
     * id
     */
    @CheckParam(alias = "id", minValue = 1)
    private Long id;

    private static final long serialVersionUID = 1L;
}