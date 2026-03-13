package com.puppynoteserver.community.post.service;

import com.puppynoteserver.community.post.document.PostDocument;
import com.puppynoteserver.community.post.entity.Post;
import com.puppynoteserver.community.post.repository.PostSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Profile("!test")
@RequiredArgsConstructor
public class PostSearchService {

    private final ElasticsearchOperations elasticsearchOperations;
    private final PostSearchRepository postSearchRepository;

    public void indexPost(Post post, List<String> hashtags) {
        PostDocument document = PostDocument.builder()
                .postId(post.getId())
                .userId(post.getUser().getId())
                .userNickname(post.getUser().getNickName())
                .content(post.getContent())
                .hashtags(hashtags)
                .createdDate(post.getCreatedDate())
                .build();
        postSearchRepository.save(document);
        log.info("ES 인덱싱 완료 - postId: {}, 해시태그: {}", post.getId(), hashtags);
    }

    public void deletePost(Long postId) {
        postSearchRepository.deleteById(String.valueOf(postId));
        log.info("ES 삭제 완료 - postId: {}", postId);
    }

    // 해시태그 자동완성 — keyword를 포함하는 해시태그 목록 반환
    public List<String> searchHashtags(String keyword) {
        NativeQuery query = NativeQuery.builder()
                .withQuery(q -> q
                        .wildcard(w -> w.field("hashtags").wildcard("*" + keyword + "*"))
                )
                .withPageable(PageRequest.of(0, 200))
                .build();

        SearchHits<PostDocument> hits = elasticsearchOperations.search(query, PostDocument.class);
        return hits.stream()
                .flatMap(hit -> hit.getContent().getHashtags().stream())
                .filter(tag -> tag.contains(keyword))
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    // 해시태그 검색 → postId 목록 반환
    public List<Long> search(String keyword, int page, int size) {
        NativeQuery query = NativeQuery.builder()
                .withQuery(q -> q
                        .term(t -> t.field("hashtags").value(keyword))
                )
                .withPageable(PageRequest.of(page, size))
                .withSort(Sort.by(Sort.Direction.DESC, "createdDate"))
                .build();

        SearchHits<PostDocument> hits = elasticsearchOperations.search(query, PostDocument.class);
        return hits.stream()
                .map(hit -> hit.getContent().getPostId())
                .collect(Collectors.toList());
    }
}
