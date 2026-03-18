package com.puppynoteserver.community.post.repository;

import com.puppynoteserver.community.post.document.PostDocument;
import org.springframework.context.annotation.Profile;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

@Profile("!test")
public interface PostSearchRepository extends ElasticsearchRepository<PostDocument, String> {
}
