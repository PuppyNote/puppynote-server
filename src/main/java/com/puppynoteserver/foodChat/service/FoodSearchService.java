package com.puppynoteserver.foodChat.service;

import com.puppynoteserver.foodChat.document.FoodDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Profile("!test")
@RequiredArgsConstructor
public class FoodSearchService {

    private final ElasticsearchOperations elasticsearchOperations;

    public List<Long> search(String question, int page, int size) {
        try {
            NativeQuery query = NativeQuery.builder()
                    .withQuery(q -> q
                            .match(m -> m
                                    .field("question")
                                    .query(question)
                                    .minimumShouldMatch("80%")
                            )
                    )
                    .withMinScore(1.5f)
                    .withPageable(PageRequest.of(page, size))
                    .build();

            SearchHits<FoodDocument> hits = elasticsearchOperations.search(query, FoodDocument.class);
            return hits.stream()
                    .map(hit -> hit.getContent().getFoodChatHistoryId())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.warn("ES 조회 실패, AI 호출로 전환합니다: {}", e.getMessage());
            return List.of();
        }
    }
}
