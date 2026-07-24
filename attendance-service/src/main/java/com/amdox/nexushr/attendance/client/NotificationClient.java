package com.amdox.nexushr.attendance.client;
import java.time.LocalDateTime;


import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.amdox.nexushr.attendance.dto.request.NotificationRequest;





@Component
public class NotificationClient {

    private final RestTemplate restTemplate = new RestTemplate();

    private static final String NOTIFICATION_URL =
            "http://localhost:8088/api/notifications";

    // Without JWT
    public void send(NotificationRequest request) {

        try {

            request.setRead(false);
            request.setCreatedAt(LocalDateTime.now());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<NotificationRequest> entity =
                    new HttpEntity<>(request, headers);

            restTemplate.postForEntity(
                    NOTIFICATION_URL,
                    entity,
                    String.class
            );
            

        } catch (Exception e) {

        	e.printStackTrace();
        }
    }

    // With JWT
    public void sendNotification(NotificationRequest request, String token) {

        try {

            System.out.println("========== Sending Notification ==========");
            System.out.println("Employee ID : " + request.getEmployeeId());
            System.out.println("Title       : " + request.getTitle());
            System.out.println("Message     : " + request.getMessage());
            System.out.println("Type        : " + request.getType());

            request.setRead(false);
            request.setCreatedAt(LocalDateTime.now());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            if (token != null && !token.isBlank()) {
                headers.setBearerAuth(token);
            }

            HttpEntity<NotificationRequest> entity =
                    new HttpEntity<>(request, headers);

            String response = restTemplate.postForObject(
                    NOTIFICATION_URL,
                    entity,
                    String.class
            );

            System.out.println("Notification sent successfully.");
            System.out.println("Response = " + response);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}