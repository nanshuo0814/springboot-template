package com.nanshuo.icu.esdao;

import com.nanshuo.icu.model.dto.post.PostEsRequest;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import java.util.List;

/**
 * 帖子 ES 操作
 *
 * @author <a href="https://github.com/nanshuo0814">nanshuo(南烁)</a>
 * @date 2024/07/26
 */
public interface PostEsDao extends ElasticsearchRepository<PostEsRequest, Long> {

    /**
     * 通过创建来查找
     *
     * @param createBy 创建人
     * @return {@link List }<{@link PostEsRequest }>
     */
    List<PostEsRequest> findByCreateBy(Long createBy);
}