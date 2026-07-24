package com.amdox.nexushr.employee.client;

import java.time.LocalDateTime;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.amdox.nexushr.employee.dto.request.NotificationRequest;

@Component
public class NotificationClient {

    private final RestTemplate restTemplate = new RestTemplate();

    private static final String NOTIFICATION_URL =
            "http://localhost:8088/api/notifications";

    public void send(NotificationRequest request) {

        try {

            System.out.println("===== EMPLOYEE NOTIFICATION =====");
            System.out.println(request.getEmployeeId());
            System.out.println(request.getTitle());

            request.setRead(false);
            request.setCreatedAt(LocalDateTime.now());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<NotificationRequest> entity =
                    new HttpEntity<>(request, headers);

            String response = restTemplate.postForObject(
                    NOTIFICATION_URL,
                    entity,
                    String.class
            );

            System.out.println("Notification Sent");
            System.out.println(response);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}