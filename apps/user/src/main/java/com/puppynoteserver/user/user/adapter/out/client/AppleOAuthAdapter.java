package com.puppynoteserver.user.user.adapter.out.client;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.puppynoteserver.user.user.adapter.out.client.dto.AppleIdTokenPayload;
import com.puppynoteserver.user.user.application.port.out.OAuthApiClient;
import com.puppynoteserver.user.user.domain.enums.SnsType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Base64;

/**
 * [ADAPTER · OUT] Apple - 외부 호출 없이 idToken의 payload를 직접 디코딩해 이메일을 얻는다.
 */
@Component
@RequiredArgsConstructor
public class AppleOAuthAdapter implements OAuthApiClient {
    private final ObjectMapper objectMapper;

    @Override
    public SnsType oAuthSnsType() {
        return SnsType.APPLE;
    }

    @Override
    public String getEmail(String idToken) {
        return decodePayload(idToken, AppleIdTokenPayload.class).getEmail();
    }

    private <T> T decodePayload(String token, Class<T> targetClass) {
        String[] tokenParts = token.split("\\.");
        String payloadJWT = tokenParts[1];
        Base64.Decoder decoder = Base64.getUrlDecoder();
        String payload = new String(decoder.decode(payloadJWT));
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try {
            return objectMapper.readValue(payload, targetClass);
        } catch (Exception e) {
            throw new RuntimeException("Error decoding token payload", e);
        }
    }
}
