package com.amdox.nexushr.notification.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.amdox.nexushr.notification.entity.Notification;
import com.amdox.nexushr.notification.service.NotificationService;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin("*")
public class NotificationController {

    private final NotificationService service;

    public NotificationController(NotificationService service) {
        this.service = service;
    }

    // Create Notification
    @PostMapping
    public ResponseEntity<Notification> create(
            @RequestBody Notification notification) {

        System.out.println("========== Notification Received ==========");
        System.out.println("Employee : " + notification.getEmployeeId());
        System.out.println("Title    : " + notification.getTitle());
        System.out.println("Message  : " + notification.getMessage());

        return ResponseEntity.ok(service.save(notification));
    }

    // Get All Notifications
    @GetMapping
    public ResponseEntity<List<Notification>> getAll() {

        return ResponseEntity.ok(service.getAll());
    }

    // Get Notification By Id
    @GetMapping("/{id}")
    public ResponseEntity<Notification> getById(
            @PathVariable UUID id) {

        Notification notification = service.getById(id);

        if (notification == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(notification);
    }

    // Get Notifications By Employee
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<Notification>> getByEmployee(
            @PathVariable UUID employeeId) {

        return ResponseEntity.ok(
                service.getByEmployee(employeeId)
        );
    }

    // Mark As Read
    @PutMapping("/{id}/read")
    public ResponseEntity<Notification> markAsRead(
            @PathVariable UUID id) {

        Notification notification = service.markAsRead(id);

        if (notification == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(notification);
    }

    // Delete Notification
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(
            @PathVariable UUID id) {

        service.delete(id);

        return ResponseEntity.ok(
                "Notification deleted successfully"
        );
    }
}