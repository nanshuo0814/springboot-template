package com.nanshuo.springboot.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.nanshuo.springboot.common.ErrorCode;
import com.nanshuo.springboot.constant.PageConstant;
import com.nanshuo.springboot.exception.BusinessException;
import com.nanshuo.springboot.mapper.PostFavourMapper;
import com.nanshuo.springboot.mapper.PostMapper;
import com.nanshuo.springboot.mapper.PostThumbMapper;
import com.nanshuo.springboot.model.domain.Post;
import com.nanshuo.springboot.model.domain.PostFavour;
import com.nanshuo.springboot.model.domain.PostThumb;
import com.nanshuo.springboot.model.domain.User;
import com.nanshuo.springboot.model.dto.post.PostEsDTO;
import com.nanshuo.springboot.model.dto.post.PostQueryRequest;
import com.nanshuo.springboot.model.enums.sort.CommonSortFieldEnums;
import com.nanshuo.springboot.model.enums.sort.PostSortFieldEnums;
import com.nanshuo.springboot.model.vo.post.PostVO;
import com.nanshuo.springboot.model.vo.user.UserSafetyVO;
import com.nanshuo.springboot.service.PostService;
import com.nanshuo.springboot.service.UserService;
import com.nanshuo.springboot.utils.SqlUtils;
import com.nanshuo.springboot.utils.ThrowUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
* @author nanshuo
* @description 针对表【post(帖子)】的数据库操作Service实现
* @createDate 2024-03-31 11:48:14
*/
@Slf4j
@Service
@RequiredArgsConstructor
public class PostServiceImpl extends ServiceImpl<PostMapper, Post>
    implements PostService {

    private final UserService userService;
    private final PostThumbMapper postThumbMapper;
    private final PostFavourMapper postFavourMapper;
    private final ElasticsearchRestTemplate elasticsearchRestTemplate;

    /**
     * 有效帖子
     *
     * @param post post
     * @param add  添加
     */
    @Override
    public void validPost(Post post, boolean add) {
        if (post == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String title = post.getTitle();
        String content = post.getContent();
        String tags = post.getTags();
        // 创建时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(StringUtils.isAnyBlank(title, content, tags), ErrorCode.PARAMS_ERROR);
        }
        // 有参数则校验
        if (StringUtils.isNotBlank(title) && title.length() > 80) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标题过长");
        }
        if (StringUtils.isNotBlank(content) && content.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "内容过长");
        }
    }

    /**
     * 获取帖子封装
     *
     * @param post    post
     * @param request 请求
     * @return {@code PostVO}
     */
    @Override
    public PostVO getPostVO(Post post, HttpServletRequest request) {
        PostVO postVO = PostVO.objToVo(post);
        long postId = post.getId();
        // 1. 关联查询用户信息
        Long userId = post.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserSafetyVO userVO = userService.getUserSafeVO(user);
        postVO.setUser(userVO);
        // 2. 已登录，获取用户点赞、收藏状态
        User loginUser = userService.getLoginUser(request);
        if (loginUser != null) {
            // 获取点赞
            LambdaQueryWrapper<PostThumb> postThumbQueryWrapper = new LambdaQueryWrapper<>();
            postThumbQueryWrapper.in(PostThumb::getPostId, postId);
            postThumbQueryWrapper.eq(PostThumb::getUserId, loginUser.getId());
            PostThumb postThumb = postThumbMapper.selectOne(postThumbQueryWrapper);
            postVO.setHasThumb(postThumb != null);
            // 获取收藏
            LambdaQueryWrapper<PostFavour> postFavourQueryWrapper = new LambdaQueryWrapper<>();
            postFavourQueryWrapper.in(PostFavour::getPostId, postId);
            postFavourQueryWrapper.eq(PostFavour::getUserId, loginUser.getId());
            PostFavour postFavour = postFavourMapper.selectOne(postFavourQueryWrapper);
            postVO.setHasFavour(postFavour != null);
        }
        return postVO;
    }

    /**
     * 获取查询包装器
     *
     * @param postQueryRequest post查询请求
     * @return {@code QueryWrapper<Post>}
     */
    @Override
    public LambdaQueryWrapper<Post> getQueryWrapper(PostQueryRequest postQueryRequest) {
        LambdaQueryWrapper<Post> queryWrapper = new LambdaQueryWrapper<>();
        if (postQueryRequest == null) {
            return queryWrapper;
        }
        String searchText = postQueryRequest.getSearchText();
        String sortField = postQueryRequest.getSortField();
        String sortOrder = postQueryRequest.getSortOrder();
        Long id = postQueryRequest.getId();
        String title = postQueryRequest.getTitle();
        String content = postQueryRequest.getContent();
        List<String> tagList = postQueryRequest.getTags();
        Long userId = postQueryRequest.getUserId();
        Long notId = postQueryRequest.getNotId();
        // 拼接查询条件
        if (StringUtils.isNotBlank(searchText)) {
            queryWrapper.and(qw -> qw.like(Post::getTitle, searchText).or().like(Post::getContent, searchText));
        }
        queryWrapper.like(StringUtils.isNotBlank(title), Post::getTitle, title);
        queryWrapper.like(StringUtils.isNotBlank(content), Post::getContent, content);
        if (CollUtil.isNotEmpty(tagList)) {
            for (String tag : tagList) {
                queryWrapper.like(Post::getTags, "\"" + tag + "\"");
            }
        }
        queryWrapper.ne(ObjectUtils.isNotEmpty(notId), Post::getId, notId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), Post::getId, id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), Post::getUserId, userId);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(PageConstant.SORT_ORDER_ASC),
                isSortField(sortField));
        return queryWrapper;
    }

    /**
     * 分页获取帖子封装
     *
     * @param postPage 帖子页面
     * @param request  请求
     * @return {@code Page<PostVO>}
     */
    @Override
    public Page<PostVO> getPostVOPage(Page<Post> postPage, HttpServletRequest request) {
        List<Post> postList = postPage.getRecords();
        Page<PostVO> postVOPage = new Page<>(postPage.getCurrent(), postPage.getSize(), postPage.getTotal());
        if (CollUtil.isEmpty(postList)) {
            return postVOPage;
        }
        // 1. 关联查询用户信息
        Set<Long> userIdSet = postList.stream().map(Post::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 2. 已登录，获取用户点赞、收藏状态
        Map<Long, Boolean> postIdHasThumbMap = new HashMap<>();
        Map<Long, Boolean> postIdHasFavourMap = new HashMap<>();
        User loginUser = userService.getLoginUser(request);
        if (loginUser != null) {
            Set<Long> postIdSet = postList.stream().map(Post::getId).collect(Collectors.toSet());
            loginUser = userService.getLoginUser(request);
            // 获取点赞
            LambdaQueryWrapper<PostThumb> postThumbQueryWrapper = new LambdaQueryWrapper<>();
            postThumbQueryWrapper.in(PostThumb::getId, postIdSet);
            postThumbQueryWrapper.eq(PostThumb::getUserId, loginUser.getId());
            List<PostThumb> postPostThumbList = postThumbMapper.selectList(postThumbQueryWrapper);
            postPostThumbList.forEach(postPostThumb -> postIdHasThumbMap.put(postPostThumb.getPostId(), true));
            // 获取收藏
            LambdaQueryWrapper<PostFavour> postFavourQueryWrapper = new LambdaQueryWrapper<>();
            postFavourQueryWrapper.in(PostFavour::getPostId, postIdSet);
            postFavourQueryWrapper.eq(PostFavour::getUserId, loginUser.getId());
            List<PostFavour> postFavourList = postFavourMapper.selectList(postFavourQueryWrapper);
            postFavourList.forEach(postFavour -> postIdHasFavourMap.put(postFavour.getPostId(), true));
        }
        // 填充信息
        List<PostVO> postVOList = postList.stream().map(post -> {
            PostVO postVO = PostVO.objToVo(post);
            Long userId = post.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            postVO.setUser(userService.getUserSafeVO(user));
            postVO.setHasThumb(postIdHasThumbMap.getOrDefault(post.getId(), false));
            postVO.setHasFavour(postIdHasFavourMap.getOrDefault(post.getId(), false));
            return postVO;
        }).collect(Collectors.toList());
        postVOPage.setRecords(postVOList);
        return postVOPage;
    }

    /**
     * 从es搜索
     *
     * @param postQueryRequest post查询请求
     * @return {@code Page<Post>}
     */
    @Override
    public Page<Post> searchFromEs(PostQueryRequest postQueryRequest) {
        Long id = postQueryRequest.getId();
        Long notId = postQueryRequest.getNotId();
        String searchText = postQueryRequest.getSearchText();
        String title = postQueryRequest.getTitle();
        String content = postQueryRequest.getContent();
        List<String> tagList = postQueryRequest.getTags();
        List<String> orTagList = postQueryRequest.getOrTags();
        Long userId = postQueryRequest.getUserId();
        // es 起始页为 0
        long current = postQueryRequest.getCurrent() - 1;
        long pageSize = postQueryRequest.getPageSize();
        String sortField = postQueryRequest.getSortField();
        String sortOrder = postQueryRequest.getSortOrder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        // 过滤
        boolQueryBuilder.filter(QueryBuilders.termQuery("isDelete", 0));
        if (id != null) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("id", id));
        }
        if (notId != null) {
            boolQueryBuilder.mustNot(QueryBuilders.termQuery("id", notId));
        }
        if (userId != null) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("userId", userId));
        }
        // 必须包含所有标签
        if (CollUtil.isNotEmpty(tagList)) {
            for (String tag : tagList) {
                boolQueryBuilder.filter(QueryBuilders.termQuery("tags", tag));
            }
        }
        // 包含任何一个标签即可
        if (CollUtil.isNotEmpty(orTagList)) {
            BoolQueryBuilder orTagBoolQueryBuilder = QueryBuilders.boolQuery();
            for (String tag : orTagList) {
                orTagBoolQueryBuilder.should(QueryBuilders.termQuery("tags", tag));
            }
            orTagBoolQueryBuilder.minimumShouldMatch(1);
            boolQueryBuilder.filter(orTagBoolQueryBuilder);
        }
        // 按关键词检索
        if (StringUtils.isNotBlank(searchText)) {
            boolQueryBuilder.should(QueryBuilders.matchQuery("title", searchText));
            boolQueryBuilder.should(QueryBuilders.matchQuery("description", searchText));
            boolQueryBuilder.should(QueryBuilders.matchQuery("content", searchText));
            boolQueryBuilder.minimumShouldMatch(1);
        }
        // 按标题检索
        if (StringUtils.isNotBlank(title)) {
            boolQueryBuilder.should(QueryBuilders.matchQuery("title", title));
            boolQueryBuilder.minimumShouldMatch(1);
        }
        // 按内容检索
        if (StringUtils.isNotBlank(content)) {
            boolQueryBuilder.should(QueryBuilders.matchQuery("content", content));
            boolQueryBuilder.minimumShouldMatch(1);
        }
        // 排序
        SortBuilder<?> sortBuilder = SortBuilders.scoreSort();
        if (StringUtils.isNotBlank(sortField)) {
            sortBuilder = SortBuilders.fieldSort(sortField);
            sortBuilder.order(PageConstant.SORT_ORDER_ASC.equals(sortOrder) ? SortOrder.ASC : SortOrder.DESC);
        }
        // 分页
        PageRequest pageRequest = PageRequest.of((int) current, (int) pageSize);
        // 构造查询
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder().withQuery(boolQueryBuilder)
                .withPageable(pageRequest).withSorts(sortBuilder).build();
        SearchHits<PostEsDTO> searchHits = elasticsearchRestTemplate.search(searchQuery, PostEsDTO.class);
        Page<Post> page = new Page<>();
        page.setTotal(searchHits.getTotalHits());
        List<Post> resourceList = new ArrayList<>();
        // 查出结果后，从 db 获取最新动态数据（比如点赞数）
        if (searchHits.hasSearchHits()) {
            List<SearchHit<PostEsDTO>> searchHitList = searchHits.getSearchHits();
            List<Long> postIdList = searchHitList.stream().map(searchHit -> searchHit.getContent().getId())
                    .collect(Collectors.toList());
            List<Post> postList = baseMapper.selectBatchIds(postIdList);
            if (postList != null) {
                Map<Long, List<Post>> idPostMap = postList.stream().collect(Collectors.groupingBy(Post::getId));
                postIdList.forEach(postId -> {
                    if (idPostMap.containsKey(postId)) {
                        resourceList.add(idPostMap.get(postId).get(0));
                    } else {
                        // 从 es 清空 db 已物理删除的数据
                        String delete = elasticsearchRestTemplate.delete(String.valueOf(postId), PostEsDTO.class);
                        log.info("delete post {}", delete);
                    }
                });
            }
        }
        page.setRecords(resourceList);
        return page;
    }

    /**
     * 是否为排序字段
     *
     * @param sortField 排序字段
     * @return {@code SFunction<Post, ?>}
     */
    private SFunction<Post, ?> isSortField(String sortField) {
        if (SqlUtils.validSortField(sortField)) {
            Optional<? extends SFunction<Post, ?>> commonSortField = (Optional<? extends SFunction<Post, ?>>) CommonSortFieldEnums.fromString(sortField)
                    .map(CommonSortFieldEnums::getFieldGetter);
            if (commonSortField.isPresent()) {
                return commonSortField.get();
            }
            return PostSortFieldEnums.fromString(sortField)
                    .map(PostSortFieldEnums::getFieldGetter)
                    .orElseThrow(() -> new BusinessException(ErrorCode.PARAMS_ERROR, "错误的排序字段"));
        } else {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "排序字段无效");
        }
    }


}




