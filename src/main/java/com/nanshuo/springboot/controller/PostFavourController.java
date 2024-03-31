package com.nanshuo.springboot.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nanshuo.springboot.common.ApiResponse;
import com.nanshuo.springboot.common.ApiResult;
import com.nanshuo.springboot.common.ErrorCode;
import com.nanshuo.springboot.exception.BusinessException;
import com.nanshuo.springboot.model.domain.Post;
import com.nanshuo.springboot.model.domain.User;
import com.nanshuo.springboot.model.dto.IdRequest;
import com.nanshuo.springboot.model.dto.post.PostQueryRequest;
import com.nanshuo.springboot.model.dto.postfavour.PostFavourQueryRequest;
import com.nanshuo.springboot.model.vo.post.PostVO;
import com.nanshuo.springboot.service.PostFavourService;
import com.nanshuo.springboot.service.PostService;
import com.nanshuo.springboot.service.UserService;
import com.nanshuo.springboot.utils.ThrowUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 帖子收藏接口
 *
 * @author nanshuo
 * @date 2024/03/31 21:00:17
 */
@RestController
@RequestMapping("/post_favour")
@Slf4j
@RequiredArgsConstructor
public class PostFavourController {

    private final PostFavourService postFavourService;
    private final PostService postService;
    private final UserService userService;

    /**
     * 收藏 / 取消收藏
     *
     * @param request   请求
     * @param idRequest id请求
     * @return resultNum 收藏变化数
     */
    @PostMapping("/")
    public ApiResponse<Integer> doPostFavour(@RequestBody IdRequest idRequest,
                                             HttpServletRequest request) {
        if (idRequest == null || idRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 登录才能操作
        final User loginUser = userService.getLoginUser(request);
        long postId = idRequest.getId();
        int result = postFavourService.doPostFavour(postId, loginUser);
        return ApiResult.success(result);
    }

    /**
     * 获取我收藏的帖子列表
     *
     * @param postQueryRequest 帖子查询请求
     * @param request          请求
     * @return {@code ApiResponse<Page<PostVO>>}
     */
    @PostMapping("/my/list/page")
    public ApiResponse<Page<PostVO>> listMyFavourPostByPage(@RequestBody PostQueryRequest postQueryRequest,
                                                            HttpServletRequest request) {
        if (postQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        long current = postQueryRequest.getCurrent();
        long size = postQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Post> postPage = postFavourService.listFavourPostByPage(new Page<>(current, size),
                postService.getQueryWrapper(postQueryRequest), loginUser.getId());
        return ApiResult.success(postService.getPostVOPage(postPage, request));
    }

    /**
     * 获取用户收藏的帖子列表
     *
     * @param postFavourQueryRequest 帖子收藏查询请求
     * @param request                请求
     * @return {@code ApiResponse<Page<PostVO>>}
     */
    @PostMapping("/list/page")
    public ApiResponse<Page<PostVO>> listFavourPostByPage(@RequestBody PostFavourQueryRequest postFavourQueryRequest,
            HttpServletRequest request) {
        if (postFavourQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = postFavourQueryRequest.getCurrent();
        long size = postFavourQueryRequest.getPageSize();
        Long userId = postFavourQueryRequest.getUserId();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20 || userId == null, ErrorCode.PARAMS_ERROR);
        Page<Post> postPage = postFavourService.listFavourPostByPage(new Page<>(current, size),
                postService.getQueryWrapper(postFavourQueryRequest.getPostQueryRequest()), userId);
        return ApiResult.success(postService.getPostVOPage(postPage, request));
    }

}
