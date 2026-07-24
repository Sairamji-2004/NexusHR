package com.amdox.nexushr.attendance.service.impl;

import com.amdox.nexushr.attendance.client.NotificationClient;
import com.amdox.nexushr.attendance.dto.request.AttendanceRequest;
import com.amdox.nexushr.attendance.dto.request.CheckInRequest;
import com.amdox.nexushr.attendance.dto.request.NotificationRequest;
import com.amdox.nexushr.attendance.dto.response.AttendanceResponse;
import com.amdox.nexushr.attendance.entity.Attendance;
import com.amdox.nexushr.attendance.enums.AttendanceStatus;
import com.amdox.nexushr.attendance.exception.AttendanceNotFoundException;
import com.amdox.nexushr.attendance.repository.AttendanceRepository;
import com.amdox.nexushr.attendance.service.AttendanceService;
import com.amdox.nexushr.common.exception.DuplicateAttendanceException;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final NotificationClient notificationClient;
    @Override
     public AttendanceResponse checkIn(CheckInRequest request) {

        Attendance attendance = attendanceRepository
                .findByEmployeeIdAndAttendanceDate(
                        request.getEmployeeId(),
                        LocalDate.now()
                )
                .orElse(null);

        if (attendance != null) {

            if (attendance.getCheckInTime() != null) {
                throw new DuplicateAttendanceException(
                        "Employee already checked in today"
                );
            }

            attendance.setCheckInTime(LocalDateTime.now());
            attendance.setStatus(AttendanceStatus.PRESENT);

        } else {

            attendance = Attendance.builder()
                    .employeeId(request.getEmployeeId())
                    .attendanceDate(LocalDate.now())
                    .checkInTime(LocalDateTime.now())
                    .status(AttendanceStatus.PRESENT)
                    .build();
        }

        Attendance savedAttendance = attendanceRepository.save(attendance);

        NotificationRequest notification = new NotificationRequest();

        notification.setEmployeeId(
                java.util.UUID.fromString(savedAttendance.getEmployeeId())
        );
        notification.setTitle("Check-In Successful");

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("hh:mm a");

        notification.setMessage(
                "You have checked in successfully at "
                        + savedAttendance.getCheckInTime().format(formatter)
        );

        notification.setType("SUCCESS");

        System.out.println("===== SENDING CHECK-IN NOTIFICATION =====");
        System.out.println(notification.getEmployeeId());
        System.out.println(notification.getTitle());

        notificationClient.send(notification);

        System.out.println("===== CHECK-IN NOTIFICATION SENT =====");

        return mapToResponse(savedAttendance);}
        
        @Override
    public AttendanceResponse checkOut(String employeeId) {

        Attendance attendance = attendanceRepository
                .findByEmployeeIdAndAttendanceDate(
                        employeeId,
                        LocalDate.now()
                )
                .orElseThrow(() ->
                        new AttendanceNotFoundException(
                                "Attendance not found for employee: "
                                        + employeeId
                        )
                );

        if (attendance.getCheckInTime() == null) {
            throw new IllegalStateException(
                    "Employee has not checked in today."
            );
        }

        if (attendance.getCheckOutTime() != null) {
            throw new DuplicateAttendanceException(
                    "Employee already checked out today"
            );
        }

        LocalDateTime checkOutTime = LocalDateTime.now();

        attendance.setCheckOutTime(checkOutTime);

        Duration duration = Duration.between(
                attendance.getCheckInTime(),
                checkOutTime
        );

        attendance.setWorkingHours(duration.toMinutes() / 60.0);

        Attendance updatedAttendance = attendanceRepository.save(attendance);

        NotificationRequest notification = new NotificationRequest();

        notification.setEmployeeId(
                java.util.UUID.fromString(updatedAttendance.getEmployeeId())
        );
        notification.setTitle("Check-Out Successful");

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("hh:mm a");

        notification.setMessage(
                "You have checked out successfully at "
                        + updatedAttendance.getCheckOutTime().format(formatter)
                        + ". Working Hours: "
                        + String.format("%.1f", updatedAttendance.getWorkingHours())
                        + " hours."
        );

        notification.setType("INFO");

        System.out.println("===== SENDING CHECK-OUT NOTIFICATION =====");
        System.out.println(notification.getEmployeeId());
        System.out.println(notification.getTitle());

        notificationClient.send(notification);

        System.out.println("===== CHECK-OUT NOTIFICATION SENT =====");

        return mapToResponse(updatedAttendance);
        }
    @Override
    public AttendanceResponse markAttendance(AttendanceRequest request) {

        Attendance attendance = Attendance.builder()
                .employeeId(request.getEmployeeId())
                .attendanceDate(request.getAttendanceDate())
                .status(request.getStatus())
                .build();

        Attendance savedAttendance = attendanceRepository.save(attendance);

        return mapToResponse(savedAttendance);
    }

    @Override
    public AttendanceResponse getAttendanceById(String id) {

        Attendance attendance = attendanceRepository.findById(id)
                .orElseThrow(() ->
                        new AttendanceNotFoundException(
                                "Attendance not found with id: " + id
                        )
                );

        return mapToResponse(attendance);
    }

    @Override
    public List<AttendanceResponse> getEmployeeAttendance(String employeeId) {

        return attendanceRepository.findByEmployeeId(employeeId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<AttendanceResponse> getAllAttendance() {

        return attendanceRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<AttendanceResponse> getMonthlyAttendance(
            String employeeId,
            LocalDate startDate,
            LocalDate endDate
    ) {

        return attendanceRepository
                .findByEmployeeIdAndAttendanceDateBetween(
                        employeeId,
                        startDate,
                        endDate
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private AttendanceResponse mapToResponse(Attendance attendance) {

        return AttendanceResponse.builder()
                .id(attendance.getId())
                .employeeId(attendance.getEmployeeId())
                .attendanceDate(attendance.getAttendanceDate())
                .checkInTime(attendance.getCheckInTime())
                .checkOutTime(attendance.getCheckOutTime())
                .workingHours(attendance.getWorkingHours())
                .status(attendance.getStatus())
                .build();
    }
}