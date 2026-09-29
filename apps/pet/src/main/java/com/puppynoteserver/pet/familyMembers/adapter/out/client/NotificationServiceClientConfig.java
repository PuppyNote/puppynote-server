package com.puppynoteserver.pet.familyMembers.adapter.out.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class NotificationServiceClientConfig {

    @Bean
    public RestClient notificationRestClient(RestClient.Builder builder, @Value("${notification-service.url}") String baseUrl) {
        return builder.baseUrl(baseUrl).build();
    }
}
