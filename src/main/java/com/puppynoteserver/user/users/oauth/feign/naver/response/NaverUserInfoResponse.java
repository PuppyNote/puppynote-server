package com.puppynoteserver.user.users.oauth.feign.naver.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NaverUserInfoResponse {

    @JsonProperty("resultcode")
    private String resultcode;

    @JsonProperty("reponse")
    private Response response;

    public static class Response {

        @JsonProperty("email")
        private String email;

        @Builder
        private Response(String email) {
            this.email = email;
        }
    }

    public String getEmail() {
        return response.email;
    }
}
