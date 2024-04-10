package com.nanshuo.springboot.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nanshuo.springboot.annotation.Check;
import com.nanshuo.springboot.common.ApiResponse;
import com.nanshuo.springboot.common.ApiResult;
import com.nanshuo.springboot.common.ErrorCode;
import com.nanshuo.springboot.constant.UserConstant;
import com.nanshuo.springboot.exception.BusinessException;
import com.nanshuo.springboot.model.domain.Post;
import com.nanshuo.springboot.model.domain.User;
import com.nanshuo.springboot.model.dto.IdRequest;
import com.nanshuo.springboot.model.dto.post.PostAddRequest;
import com.nanshuo.springboot.model.dto.post.PostEditRequest;
import com.nanshuo.springboot.model.dto.post.PostQueryRequest;
import com.nanshuo.springboot.model.dto.post.PostUpdateRequest;
import com.nanshuo.springboot.model.vo.post.PostVO;
import com.nanshuo.springboot.service.PostService;
import com.nanshuo.springboot.service.UserService;
import com.nanshuo.springboot.utils.JsonUtils;
import com.nanshuo.springboot.utils.ThrowUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 帖子接口
 *
 * @author nanshuo
 * @date 2024/03/31 11:40:08
 */
@RestController
@RequestMapping("/post")
@Slf4j
@RequiredArgsConstructor
@Api(tags = "帖子模块")
public class PostController {

    private final PostService postService;
    private final UserService userService;

    // region 增删改查

    /**
     * 添加帖子
     *
     * @param postAddRequest post添加请求
     * @param request        请求
     * @return {@code ApiResponse<Long>}
     */
    @PostMapping("/add")
    @Check(checkParam = true, checkAuth = UserConstant.USER_ROLE)
    @ApiOperation(value = "添加帖子", notes = "添加帖子")
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
        post.setUserId(loginUser.getId());
        post.setFavourNum(0);
        post.setThumbNum(0);
        boolean result = postService.save(post);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newPostId = post.getId();
        return ApiResult.success(newPostId);
    }

    /**
     * 删除帖子
     *
     * @param idRequest 删除请求
     * @param request   请求
     * @return {@code ApiResponse<Boolean>}
     */
    @PostMapping("/delete")
    @Check(checkAuth = UserConstant.USER_ROLE)
    @ApiOperation(value = "删除帖子", notes = "删除帖子")
    public ApiResponse<Boolean> deletePost(@RequestBody IdRequest idRequest, HttpServletRequest request) {
        if (idRequest == null || idRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long id = idRequest.getId();
        validateAndCheckAuthForPostOperation(request, id);
        return ApiResult.success(postService.removeById(id));
    }

    /**
     * 更新（仅管理员）
     *
     * @param postUpdateRequest 更新后请求
     * @return {@code ApiResponse<Boolean>}
     */
    @PostMapping("/update")
    @Check(checkAuth = UserConstant.ADMIN_ROLE)
    @ApiOperation(value = "更新帖子（仅管理员）", notes = "更新帖子（仅管理员）")
    public ApiResponse<Boolean> updatePost(@RequestBody PostUpdateRequest postUpdateRequest) {
        if (postUpdateRequest == null || postUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
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
        return ApiResult.success(postService.updateById(post));
    }

    /**
     * 编辑（用户）
     *
     * @param postEditRequest post编辑请求
     * @param request         请求
     * @return {@code ApiResponse<Boolean>}
     */
    @PostMapping("/edit")
    @Check(checkAuth = UserConstant.USER_ROLE, checkParam = true)
    @ApiOperation(value = "编辑帖子", notes = "编辑帖子")
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
        validateAndCheckAuthForPostOperation(request, postEditRequest.getId());
        boolean result = postService.updateById(post);
        return ApiResult.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param request   请求
     * @param idRequest id请求
     * @return {@code ApiResponse<PostVO>}
     */
    @GetMapping("/get/vo")
    @Check(checkParam = true)
    @ApiOperation(value = "根据 id 获取", notes = "根据 id 获取")
    public ApiResponse<PostVO> getPostVOById(IdRequest idRequest, HttpServletRequest request) {
        if (idRequest == null || idRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = idRequest.getId();
        Post post = postService.getById(id);
        if (post == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ApiResult.success(postService.getPostVO(post, request));
    }

    // endregion

    // region 分页查询

    /**
     * 分页获取列表（仅管理员）
     *
     * @param postQueryRequest post查询请求
     * @return {@code ApiResponse<Page<Post>>}
     */
    @PostMapping("/list/page")
    @Check(checkAuth = UserConstant.ADMIN_ROLE)
    @ApiOperation(value = "分页获取列表（仅管理员）", notes = "分页获取列表（仅管理员）")
    public ApiResponse<Page<Post>> listPostByPage(@RequestBody PostQueryRequest postQueryRequest) {
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
    @ApiOperation(value = "分页获取列表（封装类）", notes = "分页获取列表（封装类）")
    public ApiResponse<Page<PostVO>> listPostVOByPage(@RequestBody PostQueryRequest postQueryRequest,
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
    @ApiOperation(value = "分页获取用户创建的帖子", notes = "分页获取用户创建的帖子")
    public ApiResponse<Page<PostVO>> listMyPostVOByPage(@RequestBody PostQueryRequest postQueryRequest,
                                                        HttpServletRequest request) {
        if (postQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        postQueryRequest.setUserId(loginUser.getId());
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
        long current = postQueryRequest.getCurrent();
        long size = postQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Post> postPage = postService.page(new Page<>(current, size), postService.getQueryWrapper(postQueryRequest));
        return postService.getPostVOPage(postPage, request);
    }

    /**
     * 分页搜索（从 ES 查询，封装类）
     *
     * @param postQueryRequest post查询请求
     * @param request          请求
     * @return {@code ApiResponse<Page<PostVO>>}
     */
    @PostMapping("/search/page/vo")
    @ApiOperation(value = "分页搜索帖子", notes = "分页搜索帖子")
    public ApiResponse<Page<PostVO>> searchPostVOByPage(@RequestBody PostQueryRequest postQueryRequest,
                                                        HttpServletRequest request) {
        long size = postQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Post> postPage = postService.searchFromEs(postQueryRequest);
        return ApiResult.success(postService.getPostVOPage(postPage, request));
    }

    // endregion

    // region 公用方法

    /**
     * 验证并检查帖子操作权限
     *
     * @param request 请求
     * @param id      id
     */
    private void validateAndCheckAuthForPostOperation(HttpServletRequest request, Long id) {
        User loginUser = userService.getLoginUser(request);
        // 判断是否存在
        Post oldPost = postService.getById(id);
        ThrowUtils.throwIf(oldPost == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可操作（这里假设"编辑"和"删除"操作的权限是一样的）
        if (!oldPost.getUserId().equals(loginUser.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
    }

    // endregion

}
