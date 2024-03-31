package com.nanshuo.springboot.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nanshuo.springboot.model.domain.PostFavour;
import generator.service.PostFavourService;
import com.nanshuo.springboot.mapper.PostFavourMapper;
import org.springframework.stereotype.Service;

/**
* @author nanshuo
* @description 针对表【post_favour(帖子收藏)】的数据库操作Service实现
* @createDate 2024-03-31 11:50:32
*/
@Service
public class PostFavourServiceImpl extends ServiceImpl<PostFavourMapper, PostFavour>
    implements PostFavourService{

}




