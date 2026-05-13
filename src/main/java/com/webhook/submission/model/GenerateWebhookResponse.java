package com.webhook.submission.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GenerateWebhookResponse {

    /**
     * The webhook URL where we must POST our final SQL answer
     */
    @JsonProperty("webhook")
    private String webhook;

    /**
     * The access token to be used as Authorization header
     * in the second request
     */
    @JsonProperty("accessToken")
    private String accessToken;
}