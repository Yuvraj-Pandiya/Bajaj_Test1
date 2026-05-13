package com.webhook.submission.runner;

import com.webhook.submission.service.WebhookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * CommandLineRunner runs automatically after the Spring context
 * is fully loaded — no manual URL trigger needed.
 *
 * This is the "wake up" action described in Phase 1, Step 1.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AppStartupRunner implements CommandLineRunner {

    private final WebhookService webhookService;

    @Override
    public void run(String... args) throws Exception {
        log.info("Application started — triggering webhook flow automatically...");
        webhookService.executeWebhookFlow();
    }
}