package com.amdox.nexushr.leave.service;

import com.amdox.nexushr.leave.entity.Leave;

import java.util.List;
import java.util.UUID;

public interface LeaveService {

    Leave applyLeave(Leave leave);

    List<Leave> getAllLeaves();

    List<Leave> getEmployeeLeaves(UUID employeeId);

    Leave approveLeave(UUID leaveId);

    Leave rejectLeave(UUID leaveId);

    void deleteLeave(UUID leaveId);
}