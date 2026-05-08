package com.puppynoteserver.foodChat.repository;

import com.puppynoteserver.foodChat.document.FoodDocument;
import org.springframework.context.annotation.Profile;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

@Profile("!test")
public interface FoodDocumentRepository extends ElasticsearchRepository<FoodDocument, String> {
}
