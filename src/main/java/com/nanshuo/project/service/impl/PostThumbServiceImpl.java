package com.nanshuo.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nanshuo.project.common.ErrorCode;
import com.nanshuo.project.exception.BusinessException;
import com.nanshuo.project.mapper.PostThumbMapper;
import com.nanshuo.project.model.domain.Post;
import com.nanshuo.project.model.domain.PostThumb;
import com.nanshuo.project.model.domain.User;
import com.nanshuo.project.service.PostService;
import com.nanshuo.project.service.PostThumbService;
import com.nanshuo.project.utils.SpringBeanContextUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @author nanshuo
 * @description 针对表【post_thumb(帖子点赞)】的数据库操作Service实现
 * @createDate 2024-03-31 11:50:35
 */
@Service
public class PostThumbServiceImpl extends ServiceImpl<PostThumbMapper, PostThumb>
        implements PostThumbService {

    @Resource
    private PostService postService;

    /**
     * 点赞 / 取消点赞
     *
     * @param postId    帖子id
     * @param loginUser 登录用户
     * @return int
     */
    @Override
    public int doPostThumb(long postId, User loginUser) {
        // 判断实体是否存在，根据类别获取实体
        Post post = postService.getById(postId);
        if (post == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 是否已点赞
        long userId = loginUser.getId();
        // 每个用户串行点赞
        // 锁必须要包裹住事务方法
        PostThumbService postThumbService = SpringBeanContextUtils.getBeanByClass(PostThumbService.class);
        synchronized (String.valueOf(userId).intern()) {
            return postThumbService.doPostThumbInner(userId, postId);
        }
    }

    /**
     * 点赞 / 取消点赞
     * 封装了事务的方法
     * @param userId 用户id
     * @param postId 帖子id
     * @return int
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int doPostThumbInner(long userId, long postId) {
        PostThumb postThumb = new PostThumb();
        postThumb.setUserId(userId);
        postThumb.setPostId(postId);
        QueryWrapper<PostThumb> postThumbQueryWrapper = new QueryWrapper<>(postThumb);
        PostThumb oldPostThumb =  this.getOne(postThumbQueryWrapper);
        boolean result;
        int thumbNumChange = 0; // 用于记录点赞数的变化，1表示增加，-1表示减少
        // 已点赞，执行取消点赞操作
        if (oldPostThumb != null) {
            result = this.remove(postThumbQueryWrapper);
            thumbNumChange = -1; // 取消点赞，点赞数减少
        } else {
            // 未点赞，执行点赞操作
            result = this.save(postThumb);
            thumbNumChange = 1; // 点赞，点赞数增加
        }
        if (result) {
            // 更新帖子点赞数
            Post post = postService.getById(postId);
            if (post != null && post.getFavourNum() > Math.abs(thumbNumChange)) {
                // 使用Lambda表达式更新帖子点赞数
                boolean updateResult = postService.lambdaUpdate()
                        .eq(Post::getId, postId)
                        .set(Post::getThumbNum, post.getThumbNum() + thumbNumChange) // 根据thumbNumChange的值增加或减少点赞数
                        .update();
                return updateResult ? thumbNumChange : 0; // 返回变化的值，1或-1
            } else {
                // 如果当前点赞数不足以执行减少操作，则抛出异常或进行其他处理
                throw new BusinessException(ErrorCode.OPERATION_ERROR);
            }
        } else {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
    }

}




