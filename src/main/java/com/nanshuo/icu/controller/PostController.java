package com.nanshuo.icu.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nanshuo.icu.annotation.Verify;
import com.nanshuo.icu.common.ApiResponse;
import com.nanshuo.icu.common.ApiResult;
import com.nanshuo.icu.common.ErrorCode;
import com.nanshuo.icu.constant.UserConstant;
import com.nanshuo.icu.exception.BusinessException;
import com.nanshuo.icu.model.domain.Post;
import com.nanshuo.icu.model.domain.User;
import com.nanshuo.icu.model.dto.IdRequest;
import com.nanshuo.icu.model.dto.post.PostAddRequest;
import com.nanshuo.icu.model.dto.post.PostEditRequest;
import com.nanshuo.icu.model.dto.post.PostQueryRequest;
import com.nanshuo.icu.model.dto.post.PostUpdateRequest;
import com.nanshuo.icu.model.vo.post.PostVO;
import com.nanshuo.icu.service.PostService;
import com.nanshuo.icu.service.UserService;
import com.nanshuo.icu.utils.JsonUtils;
import com.nanshuo.icu.utils.ThrowUtils;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 帖子接口
 *
 * @author <a href="https://github.com/nanshuo0814">nanshuo(南烁)</a>
 * @date 2024/03/31
 */
@RestController
@RequestMapping("/post")
@Slf4j
//@Api(tags = "帖子模块")
public class PostController {

    @Resource
    private PostService postService;
    @Resource
    private UserService userService;

    // region 增删改查

    /**
     * 添加帖子（需要 user 权限）
     *
     * @param postAddRequest post添加请求
     * @param request        请求
     * @return {@code ApiResponse<Long>}
     */
    @PostMapping("/add")
    @Verify(checkAuth = UserConstant.USER_ROLE)
    @ApiOperation(value = "添加帖子（需要 user 权限）")
    public ApiResponse<Long> addPost(@RequestBody PostAddRequest postAddRequest, HttpServletRequest request) {
        if (postAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Post post = new Post();
        BeanUtils.copyProperties(postAddRequest, post);
        List<String> tags = postAddRequest.getTags();
        if (tags != null) {
            post.setTags(JsonUtils.objToJson(tags));
        }
        postService.validPost(post, true);
        User loginUser = userService.getLoginUser(request);
        post.setCreateBy(loginUser.getId());
        post.setFavourNum(0);
        post.setThumbNum(0);
        boolean result = postService.save(post);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newPostId = post.getId();
        return ApiResult.success(newPostId, "添加成功！");
    }

    /**
     * 删除帖子
     *
     * @param idRequest 删除请求
     * @param request   请求
     * @return {@code ApiResponse<Boolean>}
     */
    @PostMapping("/delete")
    @Verify(checkAuth = UserConstant.USER_ROLE)
    @ApiOperation(value = "删除帖子（需要 user 权限）")
    public ApiResponse<Boolean> deletePost(@RequestBody IdRequest idRequest, HttpServletRequest request) {
        if (idRequest == null || idRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long id = idRequest.getId();
        onlyMeOrAdministratorCanDo(request, id);
        return ApiResult.success(postService.removeById(id), "删除成功！");
    }

    /**
     * 更新（需要 admin 权限）
     *
     * @param postUpdateRequest 更新后请求
     * @return {@code ApiResponse<Boolean>}
     */
    @PostMapping("/update")
    @Verify(checkAuth = UserConstant.ADMIN_ROLE)
    @ApiOperation(value = "更新帖子内容（需要 admin 权限）")
    public ApiResponse<Boolean> updatePost(@RequestBody PostUpdateRequest postUpdateRequest, HttpServletRequest request) {
        if (postUpdateRequest == null || postUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 判断管理员权限
        User user = userService.getLoginUser(request);
        if (!UserConstant.ADMIN_ROLE.equals(user.getUserRole())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限更新，只有管理员有权限");
        }
        Post post = new Post();
        BeanUtils.copyProperties(postUpdateRequest, post);
        List<String> tags = postUpdateRequest.getTags();
        if (tags != null) {
            post.setTags(JsonUtils.objToJson(tags));
        }
        // 参数校验
        postService.validPost(post, false);
        long id = postUpdateRequest.getId();
        // 判断是否存在
        Post oldPost = postService.getById(id);
        ThrowUtils.throwIf(oldPost == null, ErrorCode.NOT_FOUND_ERROR);
        return ApiResult.success(postService.updateById(post), "更新成功！");
    }

    /**
     * 编辑（需要 user 权限）
     *
     * @param postEditRequest post编辑请求
     * @param request         请求
     * @return {@code ApiResponse<Boolean>}
     */
    @PostMapping("/edit")
    @Verify(checkAuth = UserConstant.USER_ROLE)
    @ApiOperation(value = "编辑帖子内容（需要 user 权限）")
    public ApiResponse<Boolean> editPost(@RequestBody PostEditRequest postEditRequest, HttpServletRequest request) {
        if (postEditRequest == null || postEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Post post = new Post();
        BeanUtils.copyProperties(postEditRequest, post);
        List<String> tags = postEditRequest.getTags();
        if (tags != null) {
            post.setTags(JsonUtils.objToJson(tags));
        }
        // 参数校验
        postService.validPost(post, false);
        onlyMeOrAdministratorCanDo(request, postEditRequest.getId());
        boolean result = postService.updateById(post);
        return ApiResult.success(result, "编辑帖子成功！");
    }

    /**
     * 根据 id 获取
     *
     * @param request   请求
     * @param idRequest id请求
     * @return {@code ApiResponse<PostVO>}
     */
    @GetMapping("/get/vo")
    @ApiOperation(value = "根据帖子 id 获取")
    public ApiResponse<PostVO> getPostVoById(IdRequest idRequest, HttpServletRequest request) {
        if (idRequest == null || idRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = idRequest.getId();
        Post post = postService.getById(id);
        if (post == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "帖子不存在或已删除！");
        }
        return ApiResult.success(postService.getPostVO(post, request));
    }

    // endregion

    // region 分页查询

    /**
     * 分页获取列表（需要 admin 权限）
     *
     * @param postQueryRequest post查询请求
     * @return {@code ApiResponse<Page<Post>>}
     */
    @PostMapping("/list/page")
    @Verify(checkAuth = UserConstant.ADMIN_ROLE)
    @ApiOperation(value = "分页获取列表（需要 admin 权限）")
    public ApiResponse<Page<Post>> listPostByPage(@RequestBody PostQueryRequest postQueryRequest,HttpServletRequest request) {
        User user = userService.getLoginUser(request);
        if (!UserConstant.ADMIN_ROLE.equals(user.getUserRole())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限获取帖子列表数据，只有管理员有权限");
        }
        long current = postQueryRequest.getCurrent();
        long size = postQueryRequest.getPageSize();
        Page<Post> postPage = postService.page(new Page<>(current, size),
                postService.getQueryWrapper(postQueryRequest));
        return ApiResult.success(postPage);
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param postQueryRequest post查询请求
     * @param request          请求
     * @return {@code ApiResponse<Page<PostVO>>}
     */
    @PostMapping("/list/page/vo")
    @ApiOperation(value = "分页获取全部帖子")
    public ApiResponse<Page<PostVO>> listPostVoByPage(@RequestBody PostQueryRequest postQueryRequest,
                                                      HttpServletRequest request) {
        return ApiResult.success(handlePaginationAndValidation(postQueryRequest, request));
    }

    /**
     * 分页获取当前用户创建的帖子列表
     *
     * @param postQueryRequest post查询请求
     * @param request          请求
     * @return {@code ApiResponse<Page<PostVO>>}
     */
    @PostMapping("/my/list/page/vo")
    @ApiOperation(value = "分页获取当前登录用户创建的帖子")
    @Verify(checkAuth = UserConstant.USER_ROLE)
    public ApiResponse<Page<PostVO>> listMyPostVoByPage(@RequestBody PostQueryRequest postQueryRequest,
                                                        HttpServletRequest request) {
        if (postQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        postQueryRequest.setCreateBy(loginUser.getId());
        return ApiResult.success(handlePaginationAndValidation(postQueryRequest, request));
    }

    /**
     * 处理分页和验证
     *
     * @param postQueryRequest post查询请求
     * @param request          请求
     * @return {@code Page<PostVO>}
     */
    private Page<PostVO> handlePaginationAndValidation(PostQueryRequest postQueryRequest, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        long current = postQueryRequest.getCurrent();
        long size = postQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Post> postPage = postService.page(new Page<>(current, size), postService.getQueryWrapper(postQueryRequest));
        return postService.getPostVoPage(postPage, loginUser);
    }

    /**
     * ES分页搜索（需要 user 权限）
     *
     * @param postQueryRequest post查询请求
     * @param request          请求
     * @return {@code ApiResponse<Page<PostVO>>}
     */
    @PostMapping("/search/page/vo")
    @ApiOperation(value = "ES分页搜索帖子（需要 user 权限）")
    @Verify(checkAuth = UserConstant.USER_ROLE)
    public ApiResponse<Page<PostVO>> searchPostVoByPage(@RequestBody PostQueryRequest postQueryRequest,
                                                        HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        long size = postQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Post> postPage = postService.searchFromEs(postQueryRequest);
        return ApiResult.success(postService.getPostVoPage(postPage, loginUser));
    }

    // endregion

    // region 公用方法

    /**
     * 只有本人或管理员可以执行
     *
     * @param request 请求
     * @param id      id
     */
    private void onlyMeOrAdministratorCanDo(HttpServletRequest request, Long id) {
        User loginUser = userService.getLoginUser(request);
        // 判断是否存在
        Post oldPost = postService.getById(id);
        ThrowUtils.throwIf(oldPost == null, ErrorCode.NOT_FOUND_ERROR, "帖子不存在或已删除！");
        // 仅本人或管理员可操作（这里假设"编辑"和"删除"操作的权限是一样的）
        if (!oldPost.getCreateBy().equals(loginUser.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "你当前暂无该权限！");
        }
    }

    // endregion

}
