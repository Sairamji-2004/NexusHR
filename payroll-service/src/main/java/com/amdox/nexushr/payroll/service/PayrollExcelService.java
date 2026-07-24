package com.amdox.nexushr.payroll.service;

import java.util.UUID;

public interface PayrollExcelService {

    byte[] exportPayroll(UUID tenantId);

}