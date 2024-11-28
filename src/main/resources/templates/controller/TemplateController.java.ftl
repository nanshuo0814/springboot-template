package ${packageName}.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import ${packageName}.annotation.Verify;
import ${packageName}.common.ApiResponse;
import ${packageName}.model.dto.IdRequest;
import ${packageName}.common.ErrorCode;
import ${packageName}.common.ApiResult;
import ${packageName}.constant.UserConstant;
import ${packageName}.exception.BusinessException;
import ${packageName}.utils.ThrowUtils;
import ${packageName}.constant.PageConstant;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import ${packageName}.model.dto.${dataKey}.${upperDataKey}AddRequest;
//import ${packageName}.model.dto.${dataKey}.${upperDataKey}EditRequest;
import ${packageName}.model.dto.${dataKey}.${upperDataKey}QueryRequest;
import ${packageName}.model.dto.${dataKey}.${upperDataKey}UpdateRequest;
import ${packageName}.model.domain.${upperDataKey};
import ${packageName}.model.domain.User;
import ${packageName}.model.vo.${dataKey}.${upperDataKey}VO;
import ${packageName}.service.${upperDataKey}Service;
import ${packageName}.service.UserService;
import ${packageName}.model.dto.IdsRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * ${dataName}接口
 *
 * @author ${author}
 * @Date ${date}
 */
@RestController
@RequestMapping("/${dataKey}")
@Slf4j
@Api(tags = "${dataName}接口")
public class ${upperDataKey}Controller {

    @Resource
    private ${upperDataKey}Service ${dataKey}Service;

    @Resource
    private UserService userService;

    // region 增删改查

    /**
     * 创建${dataName}（需要 user 权限）
     *
     * @param ${dataKey}AddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    @ApiOperation(value = "创建${dataName}（需要 user 权限）")
    @Verify(checkAuth = UserConstant.USER_ROLE)
    public ApiResponse<Long> add${upperDataKey}(@RequestBody ${upperDataKey}AddRequest ${dataKey}AddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(${dataKey}AddRequest == null, ErrorCode.PARAMS_ERROR);
        return ApiResult.success(${dataKey}Service.add${upperDataKey}(${dataKey}AddRequest, request));
    }

    /**
     * 删除${dataName}（需要 user 权限）
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    @ApiOperation(value = "删除${dataName}（需要 user 权限）")
    @Verify(checkAuth = UserConstant.USER_ROLE)
    public ApiResponse<Long> delete${upperDataKey}(@RequestBody IdRequest deleteRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(deleteRequest == null || deleteRequest.getId() <= 0, ErrorCode.PARAMS_ERROR);
        return ApiResult.success(${dataKey}Service.delete${upperDataKey}(deleteRequest, request), "删除成功！");
    }

    /**
    * 批量删除${dataName}
    *
    * @param idsRequest ids请求
    * @param request   请求
    * @return {@link ApiResponse }<{@link Long }>
    */
    @PostMapping("/delete/batch")
    @ApiOperation(value = "批量删除用户（需要 admin 权限）")
    @Verify(checkParam = true, checkAuth = UserConstant.ADMIN_ROLE)
    public ApiResponse<List<Long>> deleteUserBatch(@RequestBody IdsRequest idsRequest, HttpServletRequest request) {
        if (idsRequest == null || idsRequest.getIds().isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 执行批量删除
        List<Long> ids = ${dataKey}Service.delete${upperDataKey}Batch(idsRequest.getIds());
        return ApiResult.success(ids, "批量删除${dataName}成功！");
    }

    /**
     * 更新${dataName}（需要 user 权限）
     *
     * @param ${dataKey}UpdateRequest
     * @return
     */
    @PostMapping("/update")
    @ApiOperation(value = "更新${dataName}（需要 user 权限）")
    @Verify(checkAuth = UserConstant.USER_ROLE)
    public ApiResponse<Long> update${upperDataKey}(@RequestBody ${upperDataKey}UpdateRequest ${dataKey}UpdateRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(${dataKey}UpdateRequest == null || ${dataKey}UpdateRequest.getId() <= 0, ErrorCode.PARAMS_ERROR);
        return ApiResult.success(${dataKey}Service.update${upperDataKey}(${dataKey}UpdateRequest, request), "更新成功！");
    }

    /**
     * 根据 id 获取${dataName}（需要 user 权限）
     *
     * @param idRequest
     * @return
     */
    @GetMapping("/get/vo")
    @ApiOperation(value = "根据 id 获取${dataName}（需要 user 权限）")
    @Verify(checkAuth = UserConstant.USER_ROLE)
    public ApiResponse<${upperDataKey}VO> get${upperDataKey}VOById(IdRequest idRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(idRequest == null || idRequest.getId() <= 0, ErrorCode.PARAMS_ERROR);
        // 获取封装类
        return ApiResult.success(${dataKey}Service.get${upperDataKey}VO(idRequest, request));
    }

    /**
     * 分页获取${dataName}列表（仅管理员可用）
     *
     * @param ${dataKey}QueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @ApiOperation(value = "分页获取${dataName}列表（仅管理员可用）")
    @Verify(checkAuth = UserConstant.ADMIN_ROLE)
    public ApiResponse<Page<${upperDataKey}>> list${upperDataKey}ByPage(@RequestBody ${upperDataKey}QueryRequest ${dataKey}QueryRequest) {
        ThrowUtils.throwIf(${dataKey}QueryRequest == null, ErrorCode.PARAMS_ERROR);
        return ApiResult.success(${dataKey}Service.getListPage(${dataKey}QueryRequest));
    }

    /**
     * 分页获取${dataName}列表（封装类）
     *
     * @param ${dataKey}QueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    @ApiOperation(value = "分页获取${dataName}列表（封装类）")
    public ApiResponse<Page<${upperDataKey}VO>> list${upperDataKey}VOByPage(@RequestBody ${upperDataKey}QueryRequest ${dataKey}QueryRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(${dataKey}QueryRequest == null, ErrorCode.PARAMS_ERROR);
        return ApiResult.success(${dataKey}Service.handlePaginationAndValidation(${dataKey}QueryRequest, request));
    }

    /**
     * 分页获取当前登录用户创建的${dataName}列表
     *
     * @param ${dataKey}QueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    @ApiOperation(value = "分页获取当前登录用户创建的${dataName}列表")
    public ApiResponse<Page<${upperDataKey}VO>> listMy${upperDataKey}VOByPage(@RequestBody ${upperDataKey}QueryRequest ${dataKey}QueryRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(${dataKey}QueryRequest == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        ${dataKey}QueryRequest.setCreateBy(loginUser.getId());
        return ApiResult.success(${dataKey}Service.handlePaginationAndValidation(${dataKey}QueryRequest, request));
    }

    // endregion

}
