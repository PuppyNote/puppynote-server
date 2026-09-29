package com.puppynoteserver.pet.familyMembers.adapter.out.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class UserServiceClientConfig {

    @Bean
    public RestClient userRestClient(RestClient.Builder builder, @Value("${user-service.url}") String baseUrl) {
        return builder.baseUrl(baseUrl).build();
    }
}
