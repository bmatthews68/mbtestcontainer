package com.btmatthews.testcontainers.mountebank;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ImposterDefinition {

    private final String name;

    private final String protocol;

    private final int port;

    private DefaultResponse defaultResponse;

    public ImposterDefinition(@JsonProperty("name") final String name,
                              @JsonProperty("protocol") final String protocol,
                              @JsonProperty("port") final int port,
                              @JsonProperty("defaultResponse") final DefaultResponse defaultResponse) {
        this.name = name;
        this.protocol = protocol;
        this.port = port;
        this.defaultResponse = defaultResponse;
    }

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("protocol")
    public String getProtocol() {
        return protocol;
    }

    @JsonProperty("port")
    public int getPort() {
        return port;
    }

    @JsonProperty("defaultResponse")
    public DefaultResponse getDefaultResponse() {
        return defaultResponse;
    }

    @JsonProperty("defaultResponse")
    public void setDefaultResponse(final DefaultResponse defaultResponse) {
        this.defaultResponse = defaultResponse;
    }

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DefaultResponse {
        private final int statusCode;
        private final String body;
        private final Map<String, List<String>> headers;

        @JsonCreator
        public DefaultResponse(@JsonProperty("statusCode") final int statusCode,
                               @JsonProperty("body") final String body,
                               @JsonProperty("headers") final Map<String, List<String>> headers) {
            this.statusCode = statusCode;
            this.body = body;
            this.headers = headers;
        }

        @JsonProperty("statusCode")
        public int getStatusCode() {
            return statusCode;
        }

        @JsonProperty("body")
        public String getBody() {
            return body;
        }

        @JsonProperty("headers")
        public Map<String, List<String>> getHeaders() {
            return headers;
        }
    }
}
