package com.amdox.nexushr.attendance.repository;


import com.amdox.nexushr.attendance.entity.Attendance;
import com.amdox.nexushr.attendance.enums.AttendanceStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, String> {


    Optional<Attendance> findByEmployeeIdAndAttendanceDate(
            String employeeId,
            LocalDate attendanceDate
    );


    List<Attendance> findByEmployeeId(
            String employeeId
    );


    List<Attendance> findByEmployeeIdAndAttendanceDateBetween(
            String employeeId,
            LocalDate startDate,
            LocalDate endDate
    );


    List<Attendance> findByStatus(
            AttendanceStatus status
    );

}