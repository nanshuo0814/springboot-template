package com.nanshuo.project.esdao;

import com.nanshuo.project.model.dto.post.PostEsDTO;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import java.util.List;

/**
 * 帖子 ES 操作
 *
 * @author nanshuo
 * @date 2024/04/09 22:31:38
 */
public interface PostEsDao extends ElasticsearchRepository<PostEsDTO, Long> {

    List<PostEsDTO> findByUserId(Long userId);
}