package com.nanshuo.project.controller;

import com.nanshuo.project.common.ApiResponse;
import com.nanshuo.project.common.ApiResult;
import com.nanshuo.project.common.ErrorCode;
import com.nanshuo.project.exception.BusinessException;
import com.nanshuo.project.model.domain.User;
import com.nanshuo.project.model.dto.postthumb.PostThumbAddRequest;
import com.nanshuo.project.service.PostThumbService;
import com.nanshuo.project.service.UserService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 帖子点赞接口
 *
 * @author nanshuo
 * @date 2024/03/31 22:31:17
 */
@RestController
@RequestMapping("/post_thumb")
@Slf4j
//@Api(tags = "帖子点赞模块")
public class PostThumbController {

    @Resource
    private PostThumbService postThumbService;
    @Resource
    private UserService userService;

    /**
     * 点赞 / 取消点赞
     *
     * @param postThumbAddRequest 帖子点赞添加请求
     * @param request             请求
     * @return resultNum 本次点赞变化数
     */
    @PostMapping("/")
    @ApiOperation(value = "点赞/取消点赞")
    public ApiResponse<Integer> doThumb(@RequestBody PostThumbAddRequest postThumbAddRequest,
                                        HttpServletRequest request) {
        if (postThumbAddRequest == null || postThumbAddRequest.getPostId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 登录才能点赞
        final User loginUser = userService.getLoginUser(request);
        long postId = postThumbAddRequest.getPostId();
        int result = postThumbService.doPostThumb(postId, loginUser);
        return ApiResult.success(result);
    }

}
