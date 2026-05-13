package com.puppynoteserver.foodChat.document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Document(indexName = "food_chat")
@Setting(settingPath = "es-settings.json")
public class FoodDocument {

    @Id
    private String id;

    @Field(type = FieldType.Long)
    private Long foodChatHistoryId;

    @Field(type = FieldType.Text, analyzer = "nori")
    private String question;

    @Field(type = FieldType.Keyword)
    private String safetyLevel;

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second)
    private LocalDateTime createdDate;
}
