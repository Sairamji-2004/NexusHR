package com.amdox.nexushr.leave.repository;

import com.amdox.nexushr.leave.entity.Leave;
import com.amdox.nexushr.leave.entity.LeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LeaveRepository extends JpaRepository<Leave, UUID> {

    List<Leave> findByEmployeeId(UUID employeeId);

    List<Leave> findByStatus(LeaveStatus status);

    List<Leave> findByEmployeeIdAndStatus(UUID employeeId, LeaveStatus status);

}