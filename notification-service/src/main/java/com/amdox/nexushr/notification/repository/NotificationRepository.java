package com.amdox.nexushr.notification.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.amdox.nexushr.notification.entity.Notification;

public interface NotificationRepository
        extends JpaRepository<Notification, UUID> {

    List<Notification> findByEmployeeId(UUID employeeId);

}