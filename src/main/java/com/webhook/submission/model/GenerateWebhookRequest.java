package com.webhook.submission.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenerateWebhookRequest {

    @JsonProperty("name")
    private String name;

    @JsonProperty("regNo")
    private String regNo;

    @JsonProperty("email")
    private String email;
}
