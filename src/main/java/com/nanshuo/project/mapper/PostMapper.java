package com.nanshuo.project.mapper;

import com.nanshuo.project.model.domain.Post;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.Date;
import java.util.List;

/**
* @author nanshuo
* @description 针对表【post(帖子)】的数据库操作Mapper
* @createDate 2024-03-31 11:48:14
* @Entity com.nanshuo.springboot.model.domain.Post
*/
public interface PostMapper extends BaseMapper<Post> {

    /**
     * 查询帖子列表（包括已被删除的数据）
     *
     * @param minUpdateTime 最小更新时间
     * @return {@code List<Post>}
     */
    List<Post> listPostWithDelete(Date minUpdateTime);

}




