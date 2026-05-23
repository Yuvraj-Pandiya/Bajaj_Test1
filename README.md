# Bajaj Test 1 - Testing Guide

This project is a Spring Boot application that automatically performs the required Bajaj webhook submission flow when the application starts.

There is no local REST controller to call manually. Testing this task means validating that the application starts successfully, calls the remote `generateWebhook` API, receives a webhook URL and access token, and then submits the SQL answer to the returned webhook endpoint.

## 1. What this application does

On startup, the app runs the following sequence automatically:

1. Starts the Spring Boot application.
2. Executes `AppStartupRunner`.
3. Calls the Bajaj `generateWebhook` endpoint with:
   - `name`
   - `regNo`
   - `email`
4. Reads the response fields:
   - `webhook`
   - `accessToken`
5. Sends the SQL answer to the returned webhook URL.
6. Prints success or failure logs.

## 2. Project details used in the current implementation

Current implementation values are defined in `src/main/java/com/webhook/submission/service/WebhookService.java`.

- Name: `Yuvraj`
- Registration number: `123324`
- Email: `practiseyuvraj@gamil.com`
- Startup trigger class: `src/main/java/com/webhook/submission/runner/AppStartupRunner.java`
- Main flow class: `src/main/java/com/webhook/submission/service/WebhookService.java`
- App entry point: `src/main/java/com/webhook/submission/WebhookSubmissionApplication.java`
- Port: `8080`

## 3. Prerequisites

Make sure the following are installed:

- Java `21`
- Maven `3.9+` or any Maven version compatible with Spring Boot `3.2.0`
- Internet access to reach the remote Bajaj API

## 4. Important note before testing

The current code has a mismatch between the SQL-selection comment and the actual registration number:

- The code comment says the last digit is odd and mentions `7`.
- The actual `regNo` in code is `123324`.
- The last digit of `123324` is `4`, which is even.

This means the README can explain how to test the current flow, but functional correctness against the assignment also depends on whether the SQL answer matches the question for the actual registration number.

There is also a likely typo in the configured email:

- Current email: `practiseyuvraj@gamil.com`
- This may need to be `gmail.com` if the remote API validates email format or identity strictly.

## 5. How to build the project

Open a terminal in the project root and run:

```bash
mvn clean package
```

What to verify:

- Build finishes successfully.
- A JAR is created in `target/`.
- Expected artifact: `target/Bajaj_Test1-1.0-SNAPSHOT.jar`

## 6. How to run the application

You can run the task in either of the following ways.

### Option A: Run with Maven

```bash
mvn spring-boot:run
```

### Option B: Run the packaged JAR

```bash
java -jar target/Bajaj_Test1-1.0-SNAPSHOT.jar
```

## 7. What should happen when the app starts

No manual API trigger is required.

As soon as the application starts, `AppStartupRunner` calls:

- `webhookService.executeWebhookFlow()`

That method performs:

1. Webhook generation request.
2. SQL submission request.

## 8. Exact request flow to validate

### Step 1: Generate webhook

Endpoint used:

- `https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA`

Request method:

- `POST`

Headers:

- `Content-Type: application/json`

Request body format:

```json
{
  "name": "Yuvraj",
  "regNo": "123324",
  "email": "practiseyuvraj@gamil.com"
}
```

Expected response fields:

```json
{
  "webhook": "<remote-webhook-url>",
  "accessToken": "<token>"
}
```

### Step 2: Submit final SQL query

Endpoint used:

- The `webhook` value returned from Step 1

Request method:

- `POST`

Headers:

- `Content-Type: application/json`
- `Authorization: <accessToken>`

Request body format:

```json
{
  "finalQuery": "SELECT MAX(salary) AS salary FROM employees WHERE salary < (SELECT MAX(salary) FROM employees)"
}
```

## 9. Log lines to verify during testing

While running the application, check for these log stages:

- `Application started` message from `AppStartupRunner`
- `WEBHOOK FLOW STARTED`
- `Step 1: Sending identity details to generate webhook`
- `Step 1 HTTP Status`
- `Webhook URL received`
- `Access Token received`
- `Step 2: Submitting SQL answer to webhook`
- `SQL Query being submitted`
- `Step 2 HTTP Status`
- `Step 2 Response Body`
- `SUBMISSION SUCCESSFUL!`

## 10. Successful test criteria

Treat the task as successfully tested only if all of the following are true:

- The application starts without build/runtime errors.
- Step 1 returns a successful HTTP status.
- A non-empty `webhook` value is received.
- A non-empty `accessToken` value is received.
- Step 2 returns a successful HTTP status.
- The final success log is printed.

## 11. Failure cases to check

Testing should also cover the following possible failure points:

- Java version is not `21`
- Maven is missing or incompatible
- No internet connection
- Remote Bajaj API is unavailable
- Incorrect `regNo`, `name`, or `email`
- Email typo causes remote rejection
- Wrong SQL query for the assigned question
- `Authorization` header is rejected by the remote webhook
- Remote webhook expects `Bearer <token>` instead of plain token

## 12. How the app handles failures currently

The code already logs failures for:

- `4xx` client errors during webhook generation
- `5xx` server errors during webhook generation
- `4xx` client errors during final submission
- `5xx` server errors during final submission
- Unexpected exceptions in both steps

If Step 1 fails, Step 2 is not executed.

## 13. Automated test status

Current status:

- `mvn test` runs successfully
- No actual unit tests or integration tests are present in `src/test/java`

This means the task is currently validated through:

- successful build
- successful application startup
- successful live API interaction
- correct logs

## 14. Recommended manual testing checklist

Use this checklist while testing:

1. Confirm Java `21` is installed.
2. Confirm Maven is installed.
3. Run `mvn clean package`.
4. Confirm JAR creation under `target/`.
5. Run the application.
6. Confirm startup logs appear.
7. Confirm Step 1 request is attempted.
8. Confirm Step 1 returns success.
9. Confirm `webhook` and `accessToken` are printed.
10. Confirm Step 2 request is attempted.
11. Confirm the SQL query is printed in logs.
12. Confirm Step 2 returns success.
13. Confirm the response body is printed.
14. Confirm `SUBMISSION SUCCESSFUL!` appears.
15. If it fails, capture the exact HTTP status and response body from logs.

## 15. Source files relevant for testing

- `src/main/java/com/webhook/submission/WebhookSubmissionApplication.java`
- `src/main/java/com/webhook/submission/runner/AppStartupRunner.java`
- `src/main/java/com/webhook/submission/service/WebhookService.java`
- `src/main/java/com/webhook/submission/model/GenerateWebhookRequest.java`
- `src/main/java/com/webhook/submission/model/GenerateWebhookResponse.java`
- `src/main/java/com/webhook/submission/model/SubmissionRequest.java`
- `src/main/resources/application.properties`
- `pom.xml`

## 16. Commands used for basic validation

The following command works in the current project:

```bash
mvn test
```

The current repository does not include automated test classes, so this command only confirms the test phase passes and the project configuration is valid.

## 17. Final testing conclusion

To test this current task properly, the main validation is not a local endpoint test. The real test is running the Spring Boot application and confirming that the complete external webhook flow executes successfully from startup to final SQL submission.
