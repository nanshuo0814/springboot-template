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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

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
     * 创建${dataName}
     *
     * @param ${dataKey}AddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    @ApiOperation(value = "创建${dataName}")
    public ApiResponse<Long> add${upperDataKey}(@RequestBody ${upperDataKey}AddRequest ${dataKey}AddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(${dataKey}AddRequest == null, ErrorCode.PARAMS_ERROR);
        // todo 在此处将实体类和 DTO 进行转换
        ${upperDataKey} ${dataKey} = new ${upperDataKey}();
        BeanUtils.copyProperties(${dataKey}AddRequest, ${dataKey});
        // 数据校验
        ${dataKey}Service.valid${upperDataKey}(${dataKey}, true);
        // todo 填充默认值
        User loginUser = userService.getLoginUser(request);
        ${dataKey}.setCreateBy(loginUser.getId());
        // 写入数据库
        boolean result = ${dataKey}Service.save(${dataKey});
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        // 返回新写入的数据 id
        long new${upperDataKey}Id = ${dataKey}.getId();
        return ApiResult.success(new${upperDataKey}Id);
    }

    /**
     * 删除${dataName}
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    @ApiOperation(value = "删除${dataName}")
    public ApiResponse<Long> delete${upperDataKey}(@RequestBody IdRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long id = deleteRequest.getId();
        onlyMeOrAdministratorCanDo(request, id);
        ThrowUtils.throwIf(${dataKey}Service.removeById(id), ErrorCode.OPERATION_ERROR);
        return ApiResult.success(id, "删除成功！");
    }

    /**
     * 更新${dataName}（仅管理员可用）
     *
     * @param ${dataKey}UpdateRequest
     * @return
     */
    @PostMapping("/update")
    @ApiOperation(value = "更新${dataName}（需要 user 权限）")
    @Verify(checkAuth = UserConstant.USER_ROLE)
    public ApiResponse<Long> update${upperDataKey}(@RequestBody ${upperDataKey}UpdateRequest ${dataKey}UpdateRequest, HttpServletRequest request) {
        if (${dataKey}UpdateRequest == null || ${dataKey}UpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return ApiResult.success(${dataKey}Service.update${upperDataKey}(${dataKey}UpdateRequest, request), "更新成功！");
    }

    /**
     * 根据 id 获取${dataName}（封装类）
     *
     * @param idRequest
     * @return
     */
    @GetMapping("/get/vo")
    @ApiOperation(value = "根据 id 获取${dataName}（封装类）")
    public ApiResponse<${upperDataKey}VO> get${upperDataKey}VOById(IdRequest idRequest, HttpServletRequest request) {
        if (idRequest == null || idRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = idRequest.getId();
        // 查询数据库
        ${upperDataKey} ${dataKey} = ${dataKey}Service.getById(id);
        ThrowUtils.throwIf(${dataKey} == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取封装类
        return ApiResult.success(${dataKey}Service.get${upperDataKey}VO(${dataKey}, request));
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
        long current = ${dataKey}QueryRequest.getCurrent();
        long size = ${dataKey}QueryRequest.getPageSize();
        if (size == 0L) {
            size = PageConstant.PAGE_SIZE;
        }
        if (current == 0L) {
            current = PageConstant.CURRENT_PAGE;
        }
        // 查询数据库
        Page<${upperDataKey}> ${dataKey}Page = ${dataKey}Service.page(new Page<>(current, size),
                ${dataKey}Service.getQueryWrapper(${dataKey}QueryRequest));
        return ApiResult.success(${dataKey}Page);
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
    public ApiResponse<Page<${upperDataKey}VO>> list${upperDataKey}VOByPage(@RequestBody ${upperDataKey}QueryRequest ${dataKey}QueryRequest,
                                                               HttpServletRequest request) {
        return ApiResult.success(handlePaginationAndValidation(${dataKey}QueryRequest, request));
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
    public ApiResponse<Page<${upperDataKey}VO>> listMy${upperDataKey}VOByPage(@RequestBody ${upperDataKey}QueryRequest ${dataKey}QueryRequest,
                                                                 HttpServletRequest request) {
        if (${dataKey}QueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        ${dataKey}QueryRequest.setCreateBy(loginUser.getId());
        return ApiResult.success(handlePaginationAndValidation(${dataKey}QueryRequest, request));
    }

    // endregion


   // region 公用方法

    /**
    * 处理分页和验证
    *
    * @param ${dataKey}QueryRequest ${dataName}查询请求
    * @param request          请求
    * @return {@code Page<${upperDataKey}VO>}
    */
    private Page<${upperDataKey}VO> handlePaginationAndValidation(${upperDataKey}QueryRequest ${dataKey}QueryRequest, HttpServletRequest request) {
        long current = ${dataKey}QueryRequest.getCurrent();
        long size = ${dataKey}QueryRequest.getPageSize();
        if (size == 0L) {
            size = PageConstant.PAGE_SIZE;
        }
        if (current == 0L) {
            current = PageConstant.CURRENT_PAGE;
        }
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<${upperDataKey}> ${dataKey}Page = ${dataKey}Service.page(new Page<>(current, size), ${dataKey}Service.getQueryWrapper(${dataKey}QueryRequest));
        return ${dataKey}Service.get${upperDataKey}VOPage(${dataKey}Page, request);
    }

    /**
    * 只有本人或管理员可以执行
    *
    * @param request 请求
    * @param id      id
    */
    private void onlyMeOrAdministratorCanDo(HttpServletRequest request, Long id) {
        User loginUser = userService.getLoginUser(request);
        // 判断是否存在
        ${upperDataKey} old${upperDataKey} = ${dataKey}Service.getById(id);
        ThrowUtils.throwIf(old${upperDataKey} == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可操作（这里假设"编辑"和"删除"操作的权限是一样的）
        if (!old${upperDataKey}.getCreateBy().equals(loginUser.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "你当前暂无该权限！");
        }
    }

    // endregion

}
