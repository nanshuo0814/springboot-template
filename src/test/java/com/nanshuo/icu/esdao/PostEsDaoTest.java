package com.nanshuo.icu.esdao;

import com.nanshuo.icu.model.domain.Post;
import com.nanshuo.icu.model.dto.post.PostEsRequest;
import com.nanshuo.icu.model.dto.post.PostQueryRequest;
import com.nanshuo.icu.service.PostService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 帖子 ES 操作测试
 *
 * @author <a href="https://github.com/nanshuo0814">nanshuo(南烁)</a>
 * @date 2024/04/09 22:35:58
 */
@SpringBootTest
public class PostEsDaoTest {

    @Resource
    private PostEsDao postEsDao;
    @Resource
    private PostService postService;

    @Test
    void test() {
        PostQueryRequest postQueryRequest = new PostQueryRequest();
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Post> page =
                postService.searchFromEs(postQueryRequest);
        System.out.println(page);
    }

    @Test
    void testSelect() {
        System.out.println(postEsDao.count());
        Page<PostEsRequest> PostPage = postEsDao.findAll(
                PageRequest.of(0, 5, Sort.by("createTime")));
        List<PostEsRequest> postList = PostPage.getContent();
        System.out.println(postList);
    }

    @Test
    void testAdd() {
        PostEsRequest postEsRequest = new PostEsRequest();
        postEsRequest.setId(1L);
        postEsRequest.setTitle("test");
        postEsRequest.setContent("test");
        postEsRequest.setTags(Arrays.asList("java", "python"));
        postEsRequest.setThumbNum(1);
        postEsRequest.setFavourNum(1);
        postEsRequest.setCreateBy(1L);
        postEsRequest.setCreateTime(new Date());
        postEsRequest.setUpdateTime(new Date());
        postEsRequest.setIsDelete(0);
        postEsDao.save(postEsRequest);
        System.out.println(postEsRequest.getId());
    }

    @Test
    void testFindById() {
        Optional<PostEsRequest> postEsDTO = postEsDao.findById(1L);
        System.out.println(postEsDTO);
    }

    @Test
    void testCount() {
        System.out.println(postEsDao.count());
    }

    @Test
    void testFindByCategory() {
        List<PostEsRequest> postEsDaoTestList = postEsDao.findByCreateBy(1L);
        System.out.println(postEsDaoTestList);
    }
}
