package icu.nanshuo.service;

import com.baomidou.mybatisplus.extension.service.IService;
import icu.nanshuo.model.domain.PostPraise;
import icu.nanshuo.model.domain.User;

/**
 * 帖子点赞服务
 *
 * @author <a href="https://github.com/nanshuo0814">nanshuo(南烁)</a>
 * @date 2024/07/26
 */
public interface PostPraiseService extends IService<PostPraise> {

    /**
     * 点赞 / 取消点赞
     *
     * @param postId    帖子id
     * @param loginUser 登录用户
     * @return int
     */
    int doPostPraise(long postId, User loginUser);

    /**
     * 帖子点赞（内部服务）
     *
     * @param userId 用户id
     * @param postId 帖子id
     * @return int
     */
    int doPostPraiseInner(long userId, long postId);

}
