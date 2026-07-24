package com.amdox.nexushr.attendance.service;


import com.amdox.nexushr.attendance.dto.request.AttendanceRequest;
import com.amdox.nexushr.attendance.dto.request.CheckInRequest;
import com.amdox.nexushr.attendance.dto.response.AttendanceResponse;

import java.time.LocalDate;
import java.util.List;


public interface AttendanceService {

    AttendanceResponse checkIn(CheckInRequest request);

    AttendanceResponse checkOut(String employeeId);

    AttendanceResponse markAttendance(AttendanceRequest request);

    AttendanceResponse getAttendanceById(String id);

    List<AttendanceResponse> getEmployeeAttendance(String employeeId);

    List<AttendanceResponse> getMonthlyAttendance(
            String employeeId,
            LocalDate startDate,
            LocalDate endDate
    );

    // NEW
    List<AttendanceResponse> getAllAttendance();
}