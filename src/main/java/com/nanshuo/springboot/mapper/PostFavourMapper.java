package com.nanshuo.springboot.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nanshuo.springboot.model.domain.Post;
import com.nanshuo.springboot.model.domain.PostFavour;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
* @author nanshuo
* @description 针对表【post_favour(帖子收藏)】的数据库操作Mapper
* @createDate 2024-03-31 11:50:32
* @Entity com.nanshuo.springboot.model.domain.PostFavour
*/
public interface PostFavourMapper extends BaseMapper<PostFavour> {

    /**
     * 分页查询收藏帖子列表
     *
     * @param page         第页
     * @param queryWrapper 查询包装器
     * @param favourUserId 支持用户id
     * @return {@code Page<Post>}
     */
    Page<Post> listFavourPostByPage(IPage<Post> page, @Param(Constants.WRAPPER) Wrapper<Post> queryWrapper, long favourUserId);

}




