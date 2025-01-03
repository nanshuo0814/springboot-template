package icu.nanshuo.controller;

import icu.nanshuo.annotation.Verify;
import icu.nanshuo.common.ApiResponse;
import icu.nanshuo.common.ApiResult;
import icu.nanshuo.common.ErrorCode;
import icu.nanshuo.constant.UserConstant;
import icu.nanshuo.exception.BusinessException;
import icu.nanshuo.model.domain.User;
import icu.nanshuo.model.dto.IdRequest;
import icu.nanshuo.service.PostPraiseService;
import icu.nanshuo.service.UserService;
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
 * @author <a href="https://github.com/nanshuo0814">nanshuo(南烁)</a>
 * @date 2024/03/31
 */
@RestController
@RequestMapping("/postPraise")
@Slf4j
//@Api(tags = "帖子点赞模块")
public class PostPraiseController {

    @Resource
    private PostPraiseService postPraiseService;
    @Resource
    private UserService userService;

    /**
     * 点赞 / 取消点赞
     *
     * @param postPraiseAddRequest 帖子点赞添加请求
     * @param request             请求
     * @return resultNum 本次点赞变化数
     */
    @PostMapping("/")
    //@ApiOperation(value = "点赞/取消点赞（需要 user 权限）")
    @Verify(checkAuth = UserConstant.USER_ROLE)
    public ApiResponse<Integer> doPraise(@RequestBody IdRequest postPraiseAddRequest,
                                        HttpServletRequest request) {
        if (postPraiseAddRequest == null || postPraiseAddRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 登录才能点赞
        final User loginUser = userService.getLoginUser(request);
        long postId = postPraiseAddRequest.getId();
        int result = postPraiseService.doPostPraise(postId, loginUser);
        return ApiResult.success(result);
    }

}
