package com.nanshuo.springboot.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nanshuo.springboot.model.domain.PostThumb;
import com.nanshuo.springboot.model.domain.User;

/**
* @author nanshuo
* @description 针对表【post_thumb(帖子点赞)】的数据库操作Service
* @createDate 2024-03-31 11:50:35
*/
public interface PostThumbService extends IService<PostThumb> {

    /**
     * 点赞 / 取消点赞
     *
     * @param postId    帖子id
     * @param loginUser 登录用户
     * @return int
     */
    int doPostThumb(long postId, User loginUser);

    /**
     * 帖子点赞（内部服务）
     *
     * @param userId 用户id
     * @param postId 帖子id
     * @return int
     */
    int doPostThumbInner(long userId, long postId);

}
