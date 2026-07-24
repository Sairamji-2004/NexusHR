package com.amdox.nexushr.attendance.entity;
import jakarta.validation.constraints.NotNull;
import com.amdox.nexushr.attendance.enums.AttendanceStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;


@Entity
@Table(
    name = "attendance",
    uniqueConstraints = {
        @UniqueConstraint(
            columnNames = {"employeeId", "attendanceDate"}
        )
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attendance {


    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @NotBlank
    @Column(nullable = false)
    private String employeeId;


    @Column(nullable = false)
    private LocalDate attendanceDate;


    private LocalDateTime checkInTime;


    private LocalDateTime checkOutTime;


    private Double workingHours;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private AttendanceStatus status;


    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;


    private LocalDateTime updatedAt;



    @PrePersist
    public void prePersist() {

        createdAt = LocalDateTime.now();

        if (attendanceDate == null) {
            attendanceDate = LocalDate.now();
        }

    }


    @PreUpdate
    public void preUpdate() {

        updatedAt = LocalDateTime.now();

    }

}