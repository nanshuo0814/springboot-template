package icu.nanshuo.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import icu.nanshuo.annotation.Verify;
import icu.nanshuo.common.ApiResponse;
import icu.nanshuo.common.ApiResult;
import icu.nanshuo.common.ErrorCode;
import icu.nanshuo.constant.UserConstant;
import icu.nanshuo.exception.BusinessException;
import icu.nanshuo.model.domain.Post;
import icu.nanshuo.model.domain.User;
import icu.nanshuo.model.dto.IdRequest;
import icu.nanshuo.model.dto.post.PostQueryRequest;
import icu.nanshuo.model.dto.postfavour.PostFavourQueryRequest;
import icu.nanshuo.model.vo.post.PostVO;
import icu.nanshuo.service.PostFavourService;
import icu.nanshuo.service.PostService;
import icu.nanshuo.service.UserService;
import icu.nanshuo.utils.ThrowUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 帖子收藏接口
 *
 * @author <a href="https://github.com/nanshuo0814">nanshuo(南烁)</a>
 * @date 2024/03/31
 */
@RestController
@RequestMapping("/post_favour")
@Slf4j
//@Api(tags = "帖子收藏模块")
public class PostFavourController {

    @Resource
    private PostFavourService postFavourService;
    @Resource
    private PostService postService;
    @Resource
    private UserService userService;

    /**
     * 收藏 / 取消收藏（需要 user 权限）
     *
     * @param request   请求
     * @param idRequest id请求
     * @return resultNum 收藏变化数
     */
    @PostMapping("/")
    //@ApiOperation(value = "收藏/取消收藏（需要 user 权限）")
    @Verify(checkAuth = UserConstant.USER_ROLE)
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
     * 获取我收藏的帖子列表（需要 user 权限）
     *
     * @param postQueryRequest 帖子查询请求
     * @param request          请求
     * @return {@code ApiResponse<Page<PostVO>>}
     */
    @PostMapping("/my/list/page")
    //@ApiOperation(value = "获取当前登录用户自己收藏的帖子（需要 user 权限）")
    @Verify(checkAuth = UserConstant.USER_ROLE)
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
        return ApiResult.success(postService.getPostVoPage(postPage, loginUser));
    }

    /**
     * 获取用户收藏的帖子列表（需要 user 权限）
     *
     * @param postFavourQueryRequest 帖子收藏查询请求
     * @param request                请求
     * @return {@code ApiResponse<Page<PostVO>>}
     */
    //@ApiOperation(value = "获取用户收藏的帖子（需要 user 权限）")
    @PostMapping("/list/page")
    @Verify(checkAuth = UserConstant.USER_ROLE)
    public ApiResponse<Page<PostVO>> listFavourPostByPageAndUserId(@RequestBody PostFavourQueryRequest postFavourQueryRequest,
            HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        if (postFavourQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = postFavourQueryRequest.getCurrent();
        long size = postFavourQueryRequest.getPageSize();
        Long userId = postFavourQueryRequest.getCreateBy();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20 || userId == null, ErrorCode.PARAMS_ERROR);
        Page<Post> postPage = postFavourService.listFavourPostByPage(new Page<>(current, size),
                postService.getQueryWrapper(postFavourQueryRequest.getPostQueryRequest()), userId);
        return ApiResult.success(postService.getPostVoPage(postPage, loginUser));
    }

}
