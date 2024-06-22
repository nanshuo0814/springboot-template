package com.nanshuo.project.model.dto.file;

import lombok.Data;

import java.io.Serializable;

/**
 * 上传文件Request
 *
 * @author <a href="https://github.com/nanshuo0814">nanshuo(南烁)</a>
 * @date 2024/01/26 14:21:07
 */
@Data
public class UploadFileRequest implements Serializable {

    private static final long serialVersionUID = -2790684919067584112L;

    /**
     * 业务类型
     */
    private String type;
}
