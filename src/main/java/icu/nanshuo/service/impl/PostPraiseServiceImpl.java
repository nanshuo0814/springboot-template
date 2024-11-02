package icu.nanshuo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import icu.nanshuo.common.ErrorCode;
import icu.nanshuo.exception.BusinessException;
import icu.nanshuo.mapper.PostPraiseMapper;
import icu.nanshuo.model.domain.Post;
import icu.nanshuo.model.domain.PostPraise;
import icu.nanshuo.model.domain.User;
import icu.nanshuo.service.PostService;
import icu.nanshuo.service.PostPraiseService;
import icu.nanshuo.utils.SpringBeanContextUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * 帖子点赞服务实现类
 *
 * @author <a href="https://github.com/nanshuo0814">nanshuo(南烁)</a>
 * @date 2024/07/26
 */
@Service
public class PostPraiseServiceImpl extends ServiceImpl<PostPraiseMapper, PostPraise>
        implements PostPraiseService {

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
    public int doPostPraise(long postId, User loginUser) {
        // 判断实体是否存在，根据类别获取实体
        Post post = postService.getById(postId);
        if (post == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "帖子不存在或已删除！");
        }
        // 是否已点赞
        long userId = loginUser.getId();
        // 每个用户串行点赞
        // 锁必须要包裹住事务方法
        PostPraiseService postPraiseService = SpringBeanContextUtils.getBeanByClass(PostPraiseService.class);
        synchronized (String.valueOf(userId).intern()) {
            return postPraiseService.doPostPraiseInner(userId, postId);
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
    public int doPostPraiseInner(long userId, long postId) {
        PostPraise postPraise = new PostPraise();
        postPraise.setCreateBy(userId);
        postPraise.setUpdateBy(userId);
        postPraise.setPostId(postId);
        QueryWrapper<PostPraise> postPraiseQueryWrapper = new QueryWrapper<>(postPraise);
        PostPraise oldPostPraise =  this.getOne(postPraiseQueryWrapper);
        boolean result;
        int praiseNumChange = 0; // 用于记录点赞数的变化，1表示增加，-1表示减少
        // 已点赞，执行取消点赞操作
        if (oldPostPraise != null) {
            result = this.remove(postPraiseQueryWrapper);
            praiseNumChange = -1; // 取消点赞，点赞数减少
        } else {
            // 未点赞，执行点赞操作
            result = this.save(postPraise);
            praiseNumChange = 1; // 点赞，点赞数增加
        }
        if (result) {
            // 更新帖子点赞数
            Post post = postService.getById(postId);
            if (post != null) {
                // 使用Lambda表达式更新帖子点赞数
                boolean updateResult = postService.lambdaUpdate()
                        .eq(Post::getId, postId)
                        .set(Post::getPraiseNum, post.getPraiseNum() + praiseNumChange) // 根据praiseNumChange的值增加或减少点赞数
                        .update();
                return updateResult ? praiseNumChange : 0; // 返回变化的值，1或-1
            } else {
                // 如果当前点赞数不足以执行减少操作，则抛出异常或进行其他处理
                throw new BusinessException(ErrorCode.OPERATION_ERROR);
            }
        } else {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
    }

}




