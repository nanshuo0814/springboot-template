package icu.nanshuo.job.once;

import cn.hutool.core.collection.CollUtil;
import icu.nanshuo.esdao.PostEsDao;
import icu.nanshuo.model.domain.Post;
import icu.nanshuo.model.dto.post.PostEsRequest;
import icu.nanshuo.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 全量同步帖子到 es
 *
 * @author <a href="https://github.com/nanshuo0814">nanshuo(南烁)</a>
 * @date 2024/07/26
 */
// todo 取消注释开启任务
//@Component
@Slf4j
public class FullSyncPostToEs implements CommandLineRunner {

    @Resource
    private PostService postService;
    @Resource
    private PostEsDao postEsDao;

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
        List<PostEsRequest> postEsRequestList = postList.stream().map(PostEsRequest::objToDto).collect(Collectors.toList());
        final int pageSize = 500;
        int total = postEsRequestList.size();
        log.info("开始全量同步帖子到ES, 总共： {}", total);
        for (int i = 0; i < total; i += pageSize) {
            int end = Math.min(i + pageSize, total);
            log.info("同步从 {} 到 {}", i, end);
            postEsDao.saveAll(postEsRequestList.subList(i, end));
        }
        log.info("结束全量同步帖子到ES, 总共 {}", total);
    }
}
