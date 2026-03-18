package com.puppynoteserver.community.post.document;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@Document(indexName = "posts")
@Setting(settingPath = "es-settings.json")
public class PostDocument {

    @Id
    private String id;

    @Field(type = FieldType.Long)
    private Long postId;

    @Field(type = FieldType.Long)
    private Long userId;

    @Field(type = FieldType.Keyword)
    private String userNickname;

    @Field(type = FieldType.Text, analyzer = "nori")
    private String content;

    @Field(type = FieldType.Keyword)
    private List<String> hashtags;

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second)
    private LocalDateTime createdDate;

    @Builder
    public PostDocument(Long postId, Long userId, String userNickname,
                        String content, List<String> hashtags, LocalDateTime createdDate) {
        this.id = String.valueOf(postId);
        this.postId = postId;
        this.userId = userId;
        this.userNickname = userNickname;
        this.content = content;
        this.hashtags = hashtags;
        this.createdDate = createdDate;
    }
}
