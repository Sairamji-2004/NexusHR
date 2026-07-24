package com.amdox.nexushr.attendance.dto.response;


import com.amdox.nexushr.attendance.enums.AttendanceStatus;

import lombok.*;


import java.time.LocalDate;
import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceResponse {


    private String id;


    private String employeeId;


    private LocalDate attendanceDate;


    private LocalDateTime checkInTime;


    private LocalDateTime checkOutTime;


    private Double workingHours;


    private AttendanceStatus status;

}