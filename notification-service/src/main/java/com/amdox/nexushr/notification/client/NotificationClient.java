package com.amdox.nexushr.notification.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.amdox.nexushr.dto.NotificationRequest;

@Component
public class NotificationClient {

    private static final String NOTIFICATION_URL =
            "http://localhost:8088/api/notifications";

    private final RestTemplate restTemplate;

    public NotificationClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void send(NotificationRequest request) {

        try {
            restTemplate.postForObject(
                    NOTIFICATION_URL,
                    request,
                    String.class
            );

            System.out.println("Notification sent successfully.");

        } catch (Exception e) {

            System.out.println("Failed to send notification: "
                    + e.getMessage());

        }
    }
}