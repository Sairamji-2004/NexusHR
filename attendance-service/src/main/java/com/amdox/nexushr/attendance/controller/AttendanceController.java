package com.amdox.nexushr.attendance.controller;

import com.amdox.nexushr.attendance.dto.request.AttendanceRequest;
import com.amdox.nexushr.attendance.dto.request.CheckInRequest;
import com.amdox.nexushr.attendance.dto.response.AttendanceResponse;
import com.amdox.nexushr.attendance.service.AttendanceService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    /**
     * Employee Check In
     */
    @PostMapping("/check-in")
    public ResponseEntity<AttendanceResponse> checkIn(
            @RequestBody CheckInRequest request) {

        System.out.println("===== CHECK IN REQUEST =====");
        System.out.println("EmployeeId = " + request.getEmployeeId());

        return ResponseEntity.ok(
                attendanceService.checkIn(request)
        );
    }
    /**
     * Employee Check Out
     */
    @PostMapping("/check-out/{employeeId}")
    public ResponseEntity<AttendanceResponse> checkOut(
            @PathVariable String employeeId) {

        return ResponseEntity.ok(
                attendanceService.checkOut(employeeId)
        );
    }

    /**
     * Mark Attendance
     */
    @PostMapping("/mark")
    public ResponseEntity<AttendanceResponse> markAttendance(
            @RequestBody AttendanceRequest request) {

        return ResponseEntity.ok(
                attendanceService.markAttendance(request)
        );
    }
    
    
    
    @GetMapping
    public ResponseEntity<List<AttendanceResponse>> getAllAttendance() {

        return ResponseEntity.ok(
                attendanceService.getAllAttendance()
        );
    }

    /**
     * Get Attendance By Id
     */
    @GetMapping("/{id}")
    public ResponseEntity<AttendanceResponse> getAttendanceById(
            @PathVariable String id) {

        return ResponseEntity.ok(
                attendanceService.getAttendanceById(id)
        );
    }

    /**
     * Get Employee Attendance History
     */
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<AttendanceResponse>> getEmployeeAttendance(
            @PathVariable String employeeId) {

        return ResponseEntity.ok(
                attendanceService.getEmployeeAttendance(employeeId)
        );
    }

    /**
     * Get Monthly Attendance
     */
    @GetMapping("/employee/{employeeId}/monthly")
    public ResponseEntity<List<AttendanceResponse>> getMonthlyAttendance(

            @PathVariable String employeeId,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate) {

        return ResponseEntity.ok(
                attendanceService.getMonthlyAttendance(
                        employeeId,
                        startDate,
                        endDate
                )
        );
    }

}