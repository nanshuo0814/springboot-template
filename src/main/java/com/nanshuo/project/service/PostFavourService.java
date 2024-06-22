package com.nanshuo.project.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.nanshuo.project.model.domain.Post;
import com.nanshuo.project.model.domain.PostFavour;
import com.nanshuo.project.model.domain.User;

/**
* @author nanshuo
* @description 针对表【post_favour(帖子收藏)】的数据库操作Service
* @createDate 2024-03-31 11:50:32
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
