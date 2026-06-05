package com.puppynoteserver.global.logagent;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorEventPayload {

    @JsonProperty("server_name")
    private String serverName;

    @JsonProperty("server_ip")
    private String serverIp;

    @JsonProperty("error_type")
    private String errorType;

    @JsonProperty("message")
    private String message;

    @JsonProperty("stack_trace")
    private String stackTrace;

    @JsonProperty("request_method")
    private String requestMethod;

    @JsonProperty("request_url")
    private String requestUrl;

    @JsonProperty("response_status")
    private int responseStatus;
}
