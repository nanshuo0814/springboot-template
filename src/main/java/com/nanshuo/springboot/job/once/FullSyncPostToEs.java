package com.nanshuo.springboot.job.once;

import cn.hutool.core.collection.CollUtil;
import com.nanshuo.springboot.esdao.PostEsDao;
import com.nanshuo.springboot.model.domain.Post;
import com.nanshuo.springboot.model.dto.post.PostEsDTO;
import com.nanshuo.springboot.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 全量同步帖子到 es
 *
 * @author nanshuo
 * @date 2024/04/09 22:34:39
 */
// todo 取消注释开启任务
//@Component
@Slf4j
@RequiredArgsConstructor
public class FullSyncPostToEs implements CommandLineRunner {

    private final PostService postService;
    private final PostEsDao postEsDao;

    /**
     * 运行
     *
     * @param args args
     */
    @Override
    public void run(String... args) {
        List<Post> postList = postService.list();
        if (CollUtil.isEmpty(postList)) {
            return;
        }
        List<PostEsDTO> postEsDTOList = postList.stream().map(PostEsDTO::objToDto).collect(Collectors.toList());
        final int pageSize = 500;
        int total = postEsDTOList.size();
        log.info("FullSyncPostToEs start, total {}", total);
        for (int i = 0; i < total; i += pageSize) {
            int end = Math.min(i + pageSize, total);
            log.info("sync from {} to {}", i, end);
            postEsDao.saveAll(postEsDTOList.subList(i, end));
        }
        log.info("FullSyncPostToEs end, total {}", total);
    }
}
