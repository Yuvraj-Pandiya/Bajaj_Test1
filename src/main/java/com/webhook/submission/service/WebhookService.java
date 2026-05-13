package com.webhook.submission.service;

import com.webhook.submission.model.GenerateWebhookRequest;
import com.webhook.submission.model.GenerateWebhookResponse;
import com.webhook.submission.model.SubmissionRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class WebhookService {

    private final RestTemplate restTemplate;

    // ---------------------------------------------------------------
    // STEP 1 ENDPOINT: Where we send our identity details first
    // ---------------------------------------------------------------
    private static final String GENERATE_WEBHOOK_URL =
            "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

    // ---------------------------------------------------------------
    // YOUR PERSONAL DETAILS - Replace with your actual information
    // ---------------------------------------------------------------
    private static final String YOUR_NAME          = "Yuvraj";
    private static final String YOUR_REG_NUMBER    = "123324";  // Last digit = 7 (ODD)
    private static final String YOUR_EMAIL         = "practiseyuvraj@gamil.com";

    // ---------------------------------------------------------------
    // THE SQL ANSWER
    //
    // Last digit of REG12347 is 7 → ODD → Use Question 1
    //
    // QUESTION (ODD):
    // Find the 2nd highest salary without using LIMIT/TOP/ROWNUM
    // or any built-in ranking functions.
    //
    // QUESTION (EVEN):
    // [Paste even question SQL here if your reg ends in even digit]
    // ---------------------------------------------------------------
    private static final String FINAL_SQL_QUERY =
            "SELECT MAX(salary) AS salary " +
                    "FROM employees " +
                    "WHERE salary < (SELECT MAX(salary) FROM employees)";

    // ---------------------------------------------------------------
    // PUBLIC METHOD: Called by AppStartupRunner on app boot
    // ---------------------------------------------------------------
    public void executeWebhookFlow() {

        log.info("========================================");
        log.info("  WEBHOOK FLOW STARTED");
        log.info("========================================");

        // --- PHASE 1: Generate Webhook (Get token + webhook URL) ---
        GenerateWebhookResponse webhookResponse = generateWebhook();

        if (webhookResponse == null) {
            log.error("FAILED: Could not retrieve webhook details. Stopping.");
            return;
        }

        log.info("Webhook URL received : {}", webhookResponse.getWebhook());
        log.info("Access Token received: {}", webhookResponse.getAccessToken());

        // --- PHASE 2: Submit the SQL Answer ---
        submitAnswer(webhookResponse.getWebhook(), webhookResponse.getAccessToken());
    }

    // ---------------------------------------------------------------
    // PRIVATE METHOD: Step 1 - POST to generateWebhook endpoint
    // ---------------------------------------------------------------
    private GenerateWebhookResponse generateWebhook() {

        log.info("--- Step 1: Sending identity details to generate webhook ---");

        // Build the request body
        GenerateWebhookRequest requestBody = new GenerateWebhookRequest(
                YOUR_NAME,
                YOUR_REG_NUMBER,
                YOUR_EMAIL
        );

        // Set headers: we are sending JSON
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Wrap body + headers into one HttpEntity object
        HttpEntity<GenerateWebhookRequest> httpEntity =
                new HttpEntity<>(requestBody, headers);

        try {
            // Send POST request using RestTemplate
            ResponseEntity<GenerateWebhookResponse> response = restTemplate.exchange(
                    GENERATE_WEBHOOK_URL,
                    HttpMethod.POST,
                    httpEntity,
                    GenerateWebhookResponse.class
            );

            log.info("Step 1 HTTP Status: {}", response.getStatusCode());

            // Return the response body (contains webhook + accessToken)
            return response.getBody();

        } catch (HttpClientErrorException e) {
            // 4xx errors (bad request, unauthorized, etc.)
            log.error("Client error during webhook generation: {} - {}",
                    e.getStatusCode(), e.getResponseBodyAsString());
            return null;

        } catch (HttpServerErrorException e) {
            // 5xx errors (server-side issue)
            log.error("Server error during webhook generation: {} - {}",
                    e.getStatusCode(), e.getResponseBodyAsString());
            return null;

        } catch (Exception e) {
            log.error("Unexpected error during webhook generation: {}", e.getMessage());
            return null;
        }
    }

    // ---------------------------------------------------------------
    // PRIVATE METHOD: Step 2 - POST SQL answer to webhook URL
    // ---------------------------------------------------------------
    private void submitAnswer(String webhookUrl, String accessToken) {

        log.info("--- Step 2: Submitting SQL answer to webhook ---");
        log.info("SQL Query being submitted: {}", FINAL_SQL_QUERY);

        // Build the request body containing our SQL answer
        SubmissionRequest submissionBody = new SubmissionRequest(FINAL_SQL_QUERY);

        // Set headers:
        // 1. Content-Type: application/json  (we are sending JSON)
        // 2. Authorization: <accessToken>    (our secret ID badge)
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", accessToken); // No "Bearer" prefix unless required

        // Wrap body + headers
        HttpEntity<SubmissionRequest> httpEntity =
                new HttpEntity<>(submissionBody, headers);

        try {
            // Send POST request to the webhook URL we received earlier
            ResponseEntity<String> response = restTemplate.exchange(
                    webhookUrl,
                    HttpMethod.POST,
                    httpEntity,
                    String.class
            );

            log.info("Step 2 HTTP Status : {}", response.getStatusCode());
            log.info("Step 2 Response Body: {}", response.getBody());
            log.info("========================================");
            log.info("  SUBMISSION SUCCESSFUL!");
            log.info("========================================");

        } catch (HttpClientErrorException e) {
            log.error("Client error during submission: {} - {}",
                    e.getStatusCode(), e.getResponseBodyAsString());

        } catch (HttpServerErrorException e) {
            log.error("Server error during submission: {} - {}",
                    e.getStatusCode(), e.getResponseBodyAsString());

        } catch (Exception e) {
            log.error("Unexpected error during submission: {}", e.getMessage());
        }
    }
}