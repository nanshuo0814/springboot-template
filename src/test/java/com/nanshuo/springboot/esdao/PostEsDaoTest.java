package com.nanshuo.springboot.esdao;

import com.nanshuo.springboot.model.domain.Post;
import com.nanshuo.springboot.model.dto.post.PostEsDTO;
import com.nanshuo.springboot.model.dto.post.PostQueryRequest;
import com.nanshuo.springboot.service.PostService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 帖子 ES 操作测试
 *
 * @author nanshuo
 * @date 2024/04/09 22:35:58
 */
@SpringBootTest
@RequiredArgsConstructor
public class PostEsDaoTest {

    private final PostEsDao postEsDao;
    private final PostService postService;

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
        Page<PostEsDTO> PostPage = postEsDao.findAll(
                PageRequest.of(0, 5, Sort.by("createTime")));
        List<PostEsDTO> postList = PostPage.getContent();
        System.out.println(postList);
    }

    @Test
    void testAdd() {
        PostEsDTO postEsDTO = new PostEsDTO();
        postEsDTO.setId(1L);
        postEsDTO.setTitle("test");
        postEsDTO.setContent("test");
        postEsDTO.setTags(Arrays.asList("java", "python"));
        postEsDTO.setThumbNum(1);
        postEsDTO.setFavourNum(1);
        postEsDTO.setUserId(1L);
        postEsDTO.setCreateTime(new Date());
        postEsDTO.setUpdateTime(new Date());
        postEsDTO.setIsDelete(0);
        postEsDao.save(postEsDTO);
        System.out.println(postEsDTO.getId());
    }

    @Test
    void testFindById() {
        Optional<PostEsDTO> postEsDTO = postEsDao.findById(1L);
        System.out.println(postEsDTO);
    }

    @Test
    void testCount() {
        System.out.println(postEsDao.count());
    }

    @Test
    void testFindByCategory() {
        List<PostEsDTO> postEsDaoTestList = postEsDao.findByUserId(1L);
        System.out.println(postEsDaoTestList);
    }
}
