package icu.nanshuo.model.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * ids请求
 *
 * @author <a href="https://github.com/nanshuo0814">nanshuo(南烁)</a>
 * @date 2024/11/28
 */
@Data
@ApiModel(value = "IdsRequest", description = "批量id")
public class IdsRequest implements Serializable {

    private List<Long> ids; // 批量删除用的ID集合

    private static final long serialVersionUID = 1L;
}