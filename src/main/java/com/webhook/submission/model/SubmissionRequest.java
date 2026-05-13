package com.webhook.submission.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubmissionRequest {

    /**
     * The final SQL query answer that will be submitted
     * to the webhook URL
     */
    @JsonProperty("finalQuery")
    private String finalQuery;
}