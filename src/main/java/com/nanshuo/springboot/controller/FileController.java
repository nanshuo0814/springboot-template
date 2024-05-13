package com.nanshuo.springboot.controller;

import cn.hutool.core.io.FileUtil;
import com.nanshuo.springboot.common.ApiResponse;
import com.nanshuo.springboot.common.ApiResult;
import com.nanshuo.springboot.common.ErrorCode;
import com.nanshuo.springboot.constant.FileConstant;
import com.nanshuo.springboot.exception.BusinessException;
import com.nanshuo.springboot.manager.CosManager;
import com.nanshuo.springboot.manager.OssManager;
import com.nanshuo.springboot.model.domain.User;
import com.nanshuo.springboot.model.dto.file.UploadFileRequest;
import com.nanshuo.springboot.model.enums.file.FileUploadTypeEnums;
import com.nanshuo.springboot.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.Arrays;

/**
 * 文件控制器
 *
 * @author nanshuo
 * @date 2024/01/26 13:27:14
 */
@Api(tags = "文件模块")
@RestController
@RequestMapping("/file")
@Slf4j
public class FileController {

    @Resource
    private UserService userService;
    // todo 阿里云OSS和腾讯云cos对象储存，二选一
    @Resource
    private OssManager ossManager;
    @Resource
    private CosManager cosManager;
    
    /**
     * 上传文件
     * 文件上传
     *
     * @param multipartFile 多部分文件
     * @param request       请求
     * @param uploadFileRequest 上传文件Request
     * @return {@code ApiResponse<String>}
     */
    @PostMapping("/upload")
    @ApiOperation(value = "文件上传", notes = "文件上传")
    public ApiResponse<String> uploadFile(@RequestPart("file") MultipartFile multipartFile,
                                          UploadFileRequest uploadFileRequest, HttpServletRequest request) {
        String type = uploadFileRequest.getType();
        FileUploadTypeEnums fileUploadTypeEnums = FileUploadTypeEnums.getEnumByValue(type);
        if (fileUploadTypeEnums == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "上传文件的参数类型错误");
        }
        validFile(multipartFile, fileUploadTypeEnums);
        User loginUser = userService.getLoginUser(request);
        // 文件目录：根据业务、用户来划分
        String uuid = String.valueOf(System.currentTimeMillis());
        String filename = uuid + "-" + multipartFile.getOriginalFilename();
        String filepath = String.format("%s/%s/%s", fileUploadTypeEnums.getValue(), loginUser.getId(), filename);
        File file = null;
        try {
            // 上传文件
            file = File.createTempFile(filepath, null);
            multipartFile.transferTo(file);
            // 阿里云OSS和腾讯云cos对象储存，二选一
            ossManager.putObject(filepath, file);
            cosManager.putObject(filepath, file);
            // 返回可访问地址
            return ApiResult.success(FileConstant.OSS_HOST_ADDRESS + filepath);
        } catch (Exception e) {
            log.error("file upload error, filepath = " + filepath, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传失败,请联系管理员");
        } finally {
            if (file != null) {
                // 删除临时文件
                boolean delete = file.delete();
                if (!delete) {
                    log.error("file delete error, filepath = {}", filepath);
                }
            }
        }
    }

    /**
     * 校验文件
     *
     * @param fileUploadBizEnum 业务类型
     * @param multipartFile     多部分文件
     */
    private void validFile(MultipartFile multipartFile, FileUploadTypeEnums fileUploadBizEnum) {
        // 文件大小
        long fileSize = multipartFile.getSize();
        // 文件后缀
        String fileSuffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());
        final long ONE_M = 1024 * 1024L;
        if (FileUploadTypeEnums.USER_AVATAR.equals(fileUploadBizEnum)) {
            if (fileSize > ONE_M) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件大小不能超过 1M");
            }
            if (!Arrays.asList("jpeg", "jpg", "svg", "png", "webp").contains(fileSuffix)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件类型错误");
            }
        }
    }
}
