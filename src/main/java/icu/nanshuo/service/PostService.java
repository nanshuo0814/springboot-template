package icu.nanshuo.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import icu.nanshuo.model.vo.post.PostVO;
import icu.nanshuo.model.domain.Post;
import icu.nanshuo.model.domain.User;
import icu.nanshuo.model.dto.post.PostQueryRequest;

import javax.servlet.http.HttpServletRequest;

/**
 * 帖子服务
 *
 * @author <a href="https://github.com/nanshuo0814">nanshuo(南烁)</a>
 * @date 2024/07/26
 */
public interface PostService extends IService<Post> {

    /**
     * 有效帖子
     *
     * @param post post
     * @param add  添加
     */
    void validPost(Post post, boolean add);

    /**
     * 获取帖子封装视图
     *
     * @param post    post
     * @param request 请求
     * @return {@code PostVO}
     */
    PostVO getPostVO(Post post, HttpServletRequest request);

    /**
     * 获取查询包装器
     *
     * @param postQueryRequest post查询请求
     * @return {@code QueryWrapper<Post>}
     */
    LambdaQueryWrapper<Post> getQueryWrapper(PostQueryRequest postQueryRequest);

    /**
     * 获取post vo页面
     *
     * @param postPage 帖子页面
     * @param user     用户
     * @return {@link Page }<{@link PostVO }>
     */
    Page<PostVO> getPostVoPage(Page<Post> postPage, User user);

    /**
     * 从es搜索
     *
     * @param postQueryRequest post查询请求
     * @return {@code Page<Post>}
     */
    Page<Post> searchFromEs(PostQueryRequest postQueryRequest);
}
