package com.amdox.nexushr.leave.service.impl;

import com.amdox.nexushr.leave.client.NotificationClient;
import com.amdox.nexushr.leave.dto.NotificationRequest;
import com.amdox.nexushr.leave.entity.Leave;
import com.amdox.nexushr.leave.entity.LeaveStatus;
import com.amdox.nexushr.leave.repository.LeaveRepository;
import com.amdox.nexushr.leave.service.LeaveService;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class LeaveServiceImpl implements LeaveService {

    private final LeaveRepository leaveRepository;
    private final NotificationClient notificationClient;

    public LeaveServiceImpl(
            LeaveRepository leaveRepository,
            NotificationClient notificationClient) {

        this.leaveRepository = leaveRepository;
        this.notificationClient = notificationClient;
    }

    @Override
    public Leave applyLeave(Leave leave) {

        if (leave.getEmployeeName() == null || leave.getEmployeeName().isBlank()) {
            leave.setEmployeeName("Unknown Employee");
        }

        leave.setStatus(LeaveStatus.PENDING);

        Leave savedLeave = leaveRepository.save(leave);

        sendNotification(
                savedLeave,
                "Leave Applied",
                "Your leave request has been submitted successfully.",
                "INFO"
        );

        return savedLeave;
    }

    @Override
    public List<Leave> getAllLeaves() {
        return leaveRepository.findAll();
    }

    @Override
    public List<Leave> getEmployeeLeaves(UUID employeeId) {
        return leaveRepository.findByEmployeeId(employeeId);
    }

    @Override
    public Leave approveLeave(UUID leaveId) {

        Leave leave = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new RuntimeException("Leave not found"));

        leave.setStatus(LeaveStatus.APPROVED);
        leave.setApprovedDate(LocalDateTime.now());

        Leave savedLeave = leaveRepository.save(leave);

        sendNotification(
                savedLeave,
                "Leave Approved",
                "Your leave request has been approved.",
                "SUCCESS"
        );

        return savedLeave;
    }

    @Override
    public Leave rejectLeave(UUID leaveId) {

        Leave leave = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new RuntimeException("Leave not found"));

        leave.setStatus(LeaveStatus.REJECTED);
        leave.setApprovedDate(LocalDateTime.now());

        Leave savedLeave = leaveRepository.save(leave);

        sendNotification(
                savedLeave,
                "Leave Rejected",
                "Your leave request has been rejected.",
                "WARNING"
        );

        return savedLeave;
    }

    @Override
    public void deleteLeave(UUID leaveId) {
        leaveRepository.deleteById(leaveId);
    }

    private void sendNotification(
            Leave leave,
            String title,
            String message,
            String type) {

        try {

            String token = null;

            Authentication authentication =
                    SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null &&
                    authentication.getCredentials() instanceof String) {

                token = (String) authentication.getCredentials();
            }

            NotificationRequest notification = new NotificationRequest();

            notification.setEmployeeId(leave.getEmployeeId());
            notification.setTitle(title);
            notification.setMessage(message);
            notification.setType(type);
            notification.setRead(false);
            notification.setCreatedAt(LocalDateTime.now());

            notificationClient.sendNotification(notification, token);

        } catch (Exception e) {

            System.out.println("Notification Error: " + e.getMessage());

        }
    }
}