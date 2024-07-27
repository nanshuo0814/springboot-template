package com.nanshuo.icu.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.nanshuo.icu.model.domain.Post;
import com.nanshuo.icu.model.domain.PostFavour;
import com.nanshuo.icu.model.domain.User;

/**
 * 帖子收藏服务
 *
 * @author <a href="https://github.com/nanshuo0814">nanshuo(南烁)</a>
 * @date 2024/07/26
 */
public interface PostFavourService extends IService<PostFavour> {

    /**
     * 帖子收藏
     *
     * @param postId    帖子id
     * @param loginUser 登录用户
     * @return int
     */
    int doPostFavour(long postId, User loginUser);

    /**
     * 帖子收藏（内部服务）
     *
     * @param userId 用户id
     * @param postId 帖子id
     * @return int
     */
    int doPostFavourInner(long userId, long postId);


    /**
     * 分页获取用户收藏的帖子列表
     *
     * @param queryWrapper 查询包装器
     * @param page         第页
     * @param favourUserId 收藏用户id
     * @return {@code Page<Post>}
     */
    Page<Post> listFavourPostByPage(IPage<Post> page, Wrapper<Post> queryWrapper,
                                    long favourUserId);
}
