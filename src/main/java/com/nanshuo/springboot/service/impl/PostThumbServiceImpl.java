package com.nanshuo.springboot.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nanshuo.springboot.model.domain.PostThumb;
import generator.service.PostThumbService;
import com.nanshuo.springboot.mapper.PostThumbMapper;
import org.springframework.stereotype.Service;

/**
* @author nanshuo
* @description 针对表【post_thumb(帖子点赞)】的数据库操作Service实现
* @createDate 2024-03-31 11:50:35
*/
@Service
public class PostThumbServiceImpl extends ServiceImpl<PostThumbMapper, PostThumb>
    implements PostThumbService{

}




