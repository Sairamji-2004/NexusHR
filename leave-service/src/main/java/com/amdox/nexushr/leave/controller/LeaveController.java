	package com.amdox.nexushr.leave.controller;
	
	import com.amdox.nexushr.leave.entity.Leave;
	import com.amdox.nexushr.leave.service.LeaveService;
	import org.springframework.web.bind.annotation.*;
	
	import java.util.List;
	import java.util.UUID;
	
	@RestController
	@RequestMapping("/api/leaves")
	@CrossOrigin("*")
	public class LeaveController {
	
	    private final LeaveService leaveService;
	
	    public LeaveController(LeaveService leaveService) {
	        this.leaveService = leaveService;
	    }
	
	    @PostMapping
	    public Leave applyLeave(@RequestBody Leave leave) {
	
	        System.out.println("=================================");
	        System.out.println("Employee ID : " + leave.getEmployeeId());
	        System.out.println("Employee Name : " + leave.getEmployeeName());
	        System.out.println("Leave Type : " + leave.getLeaveType());
	        System.out.println("Start Date : " + leave.getStartDate());
	        System.out.println("End Date : " + leave.getEndDate());
	        System.out.println("Total Days : " + leave.getTotalDays());
	        System.out.println("Reason : " + leave.getReason());
	        System.out.println("=================================");
	
	        return leaveService.applyLeave(leave);
	    }
	
	    @GetMapping
	    public List<Leave> getAllLeaves() {
	        return leaveService.getAllLeaves();
	    }
	
	    @GetMapping("/employee/{employeeId}")
	    public List<Leave> getEmployeeLeaves(@PathVariable UUID employeeId) {
	        return leaveService.getEmployeeLeaves(employeeId);
	    }
	
	    @PutMapping("/{leaveId}/approve")
	    public Leave approveLeave(@PathVariable UUID leaveId) {
	        return leaveService.approveLeave(leaveId);
	    }
	
	    @PutMapping("/{leaveId}/reject")
	    public Leave rejectLeave(@PathVariable UUID leaveId) {
	        return leaveService.rejectLeave(leaveId);
	    }
	
	    @DeleteMapping("/{leaveId}")
	    public void deleteLeave(@PathVariable UUID leaveId) {
	        leaveService.deleteLeave(leaveId);
	    }
	}