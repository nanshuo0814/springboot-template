package com.nanshuo.project.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.nanshuo.project.model.domain.Post;
import com.nanshuo.project.model.dto.post.PostQueryRequest;
import com.nanshuo.project.model.vo.post.PostVO;

import javax.servlet.http.HttpServletRequest;

/**
* @author nanshuo
* @description 针对表【post(帖子)】的数据库操作Service
* @createDate 2024-03-31 11:48:14
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
     * 分页获取帖子封装
     *
     * @param postPage 帖子页面
     * @param request  请求
     * @return {@code Page<PostVO>}
     */
    Page<PostVO> getPostVOPage(Page<Post> postPage, HttpServletRequest request);

    /**
     * 从es搜索
     *
     * @param postQueryRequest post查询请求
     * @return {@code Page<Post>}
     */
    Page<Post> searchFromEs(PostQueryRequest postQueryRequest);
}
