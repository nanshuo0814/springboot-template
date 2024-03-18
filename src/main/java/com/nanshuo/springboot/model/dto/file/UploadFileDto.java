package com.nanshuo.springboot.model.dto.file;

import lombok.Data;

import java.io.Serializable;

/**
 * 上传文件dto
 *
 * @author 小鱼儿
 * @date 2024/01/26 14:21:07
 */
@Data
public class UploadFileDto implements Serializable {

    private static final long serialVersionUID = -2790684919067584112L;

    /**
     * 业务类型
     */
    private String type;
}
