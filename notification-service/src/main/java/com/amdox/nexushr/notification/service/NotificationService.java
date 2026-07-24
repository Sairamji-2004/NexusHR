package com.amdox.nexushr.notification.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.amdox.nexushr.notification.entity.Notification;
import com.amdox.nexushr.notification.repository.NotificationRepository;

@Service
public class NotificationService {

    private final NotificationRepository repository;

    public NotificationService(NotificationRepository repository) {
        this.repository = repository;
    }

    // Create Notification
    public Notification save(Notification notification) {
        return repository.save(notification);
    }

    // Get All Notifications
    public List<Notification> getAll() {
        return repository.findAll();
    }

    // Get Notification By Id
    public Notification getById(UUID id) {
        return repository.findById(id).orElse(null);
    }

    // Get Notifications By Employee
    public List<Notification> getByEmployee(UUID employeeId) {
        return repository.findByEmployeeId(employeeId);
    }

    // Mark Notification As Read
    public Notification markAsRead(UUID id) {

        Notification notification = repository.findById(id).orElse(null);

        if (notification == null) {
            return null;
        }

        notification.setIsRead(true);

        return repository.save(notification);
    }

    // Delete Notification
    public void delete(UUID id) {
        repository.deleteById(id);
    }
}