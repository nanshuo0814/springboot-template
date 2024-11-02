package icu.nanshuo.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import icu.nanshuo.common.ErrorCode;
import icu.nanshuo.exception.BusinessException;
import icu.nanshuo.mapper.PostCollectMapper;
import icu.nanshuo.model.domain.Post;
import icu.nanshuo.model.domain.PostCollect;
import icu.nanshuo.model.domain.User;
import icu.nanshuo.service.PostCollectService;
import icu.nanshuo.service.PostService;
import icu.nanshuo.utils.SpringBeanContextUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * 帖子收藏服务实现类
 *
 * @author <a href="https://github.com/nanshuo0814">nanshuo(南烁)</a>
 * @date 2024/07/26
 */
@Service
public class PostCollectServiceImpl extends ServiceImpl<PostCollectMapper, PostCollect>
    implements PostCollectService {

    @Resource
    private PostService postService;

    /**
     * 帖子收藏
     *
     * @param postId    帖子id
     * @param loginUser 登录用户
     * @return int
     */
    @Override
    public int doPostCollect(long postId, User loginUser) {
        // 判断是否存在
        Post post = postService.getById(postId);
        if (post == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 是否已帖子收藏
        long userId = loginUser.getId();
        // 每个用户串行帖子收藏
        // 锁必须要包裹住事务方法
        PostCollectService postCollectService = SpringBeanContextUtils.getBeanByClass(PostCollectService.class);
        synchronized (String.valueOf(userId).intern()) {
            return postCollectService.doPostCollectInner(userId, postId);
        }
    }

    /**
     * 帖子收藏（内部服务）
     * 封装了事务的方法
     * @param userId 用户id
     * @param postId 帖子id
     * @return int
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int doPostCollectInner(long userId, long postId) {
        PostCollect postCollect = new PostCollect();
        postCollect.setCreateBy(userId);
        postCollect.setUpdateBy(userId);
        postCollect.setPostId(postId);
        QueryWrapper<PostCollect> postCollectQueryWrapper = new QueryWrapper<>(postCollect);
        PostCollect oldPostCollect = this.getOne(postCollectQueryWrapper);
        boolean result;
        int collectNumChange; // 用于记录收藏数的变化，1表示增加，-1表示减少
        // 已收藏，执行取消收藏操作
        if (oldPostCollect != null) {
            result = this.remove(postCollectQueryWrapper);
            collectNumChange = -1; // 取消收藏，收藏数减少
        } else {
            // 未收藏，执行收藏操作
            result = this.save(postCollect);
            collectNumChange = 1; // 收藏，收藏数增加
        }
        if (result) {
            // 更新帖子收藏数
            Post post = postService.getById(postId);
            if (post != null) {
                int newCollectNum = post.getCollectNum() + collectNumChange;
                // 判断收藏数是否为负数
                if (newCollectNum >= 0) {
                    // 使用Lambda表达式更新帖子收藏数
                    boolean updateResult = postService.lambdaUpdate()
                            .eq(Post::getId, postId)
                            .set(Post::getCollectNum, newCollectNum) // 直接设置为新的收藏数
                            .update();
                    return updateResult ? collectNumChange : 0; // 返回变化的值，1或-1
                } else {
                    // 如果收藏数为负数，则抛出异常或进行其他处理
                    throw new BusinessException(ErrorCode.OPERATION_ERROR);
                }
            } else {
                // 如果未找到帖子，则抛出异常或进行其他处理
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
            }
        } else {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
    }

    /**
     * 分页获取用户收藏的帖子列表
     *
     * @param page         第页
     * @param queryWrapper 查询包装器
     * @param collectUserId 收藏用户id
     * @return {@code Page<Post>}
     */
    @Override
    public Page<Post> listCollectPostByPage(IPage<Post> page, Wrapper<Post> queryWrapper, long collectUserId) {
        if (collectUserId <= 0) {
            return new Page<>();
        }
        return baseMapper.listCollectPostByPage(page, queryWrapper, collectUserId);
    }

}




