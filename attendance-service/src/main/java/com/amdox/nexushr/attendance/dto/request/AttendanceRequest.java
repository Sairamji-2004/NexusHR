package com.amdox.nexushr.attendance.dto.request;


import com.amdox.nexushr.attendance.enums.AttendanceStatus;

import jakarta.validation.constraints.NotBlank;

import lombok.*;


import java.time.LocalDate;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceRequest {


    @NotBlank(message = "Employee ID is required")
    private String employeeId;


    private LocalDate attendanceDate;

     @NonNull
    private AttendanceStatus status;

}