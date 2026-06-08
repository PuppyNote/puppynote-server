package com.puppynoteserver.global.logagent;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class LogAgentWebhookService {

    private final RestClient restClient = RestClient.create();

    @Value("${log-agent.webhook-url}")
    private String webhookUrl;

    @Value("${log-agent.server-name}")
    private String serverName;

    @Value("${log-agent.server-ip}")
    private String serverIp;

    public void sendError(Exception e, HttpServletRequest request) {
        String method = request.getMethod();
        String uri = request.getRequestURI();

        CompletableFuture.runAsync(() -> {
            try {
                ErrorEventPayload payload = ErrorEventPayload.builder()
                        .serverName(serverName)
                        .serverIp(serverIp)
                        .errorType(e.getClass().getSimpleName())
                        .message(e.getMessage() != null ? e.getMessage() : "")
                        .stackTrace(getStackTrace(e))
                        .requestMethod(method)
                        .requestUrl(uri)
                        .responseStatus(500)
                        .build();

                restClient.post()
                        .uri(webhookUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(payload)
                        .retrieve()
                        .toBodilessEntity();

                log.info("[LogAgent] error sent: {}", e.getClass().getSimpleName());
            } catch (Exception ex) {
                log.warn("[LogAgent] failed to send error: {}", ex.getMessage());
            }
        });
    }

    private String getStackTrace(Exception e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }
}
