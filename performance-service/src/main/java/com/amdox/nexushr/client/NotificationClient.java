package com.amdox.nexushr.client;

import com.amdox.nexushr.dto.NotificationRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class NotificationClient {

    private static final String URL =
            "http://localhost:8088/api/notifications";

    private final RestTemplate restTemplate;

    public NotificationClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void send(NotificationRequest request) {

        try {

            System.out.println("===== SENDING PERFORMANCE NOTIFICATION =====");
            System.out.println("Employee : " + request.getEmployeeId());
            System.out.println("Title    : " + request.getTitle());
            System.out.println("Message  : " + request.getMessage());

            restTemplate.postForObject(
                    URL,
                    request,
                    String.class
            );

            System.out.println("===== PERFORMANCE NOTIFICATION SENT =====");

        } catch (Exception e) {

            System.out.println("===== PERFORMANCE NOTIFICATION FAILED =====");
            e.printStackTrace();
        }
    }
}