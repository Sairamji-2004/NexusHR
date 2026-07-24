package com.amdox.nexushr.attendance.dto.request;


import jakarta.validation.constraints.NotBlank;

import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckInRequest {


    @NotBlank(message = "Employee ID is required")
    private String employeeId;

}