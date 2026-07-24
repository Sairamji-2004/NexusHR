package com.amdox.nexushr.payroll.service;

import com.amdox.nexushr.common.exception.DuplicateResourceException;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import com.amdox.nexushr.common.exception.ResourceNotFoundException;
import com.amdox.nexushr.payroll.dto.PayrollRequestDTO;
import com.amdox.nexushr.payroll.dto.PayrollResponseDTO;
import com.amdox.nexushr.payroll.entity.Payroll;
import com.amdox.nexushr.payroll.repository.PayrollRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.amdox.nexushr.payroll.client.NotificationClient;
import com.amdox.nexushr.payroll.dto.NotificationRequest;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class PayrollService {

    private static final Logger log = LoggerFactory.getLogger(PayrollService.class);

    private static final BigDecimal HRA_RATE = new BigDecimal("0.40");
    private static final BigDecimal DA_RATE = new BigDecimal("0.10");
    private static final BigDecimal SPECIAL_ALLOWANCE_RATE = new BigDecimal("0.05");
    private static final BigDecimal PF_RATE = new BigDecimal("0.12");
    private static final BigDecimal PROFESSIONAL_TAX = new BigDecimal("200.00");

    private final PayrollRepository payrollRepository;
    private final NotificationClient notificationClient;

    public PayrollService(
            PayrollRepository payrollRepository,
            NotificationClient notificationClient) {

        this.payrollRepository = payrollRepository;
        this.notificationClient = notificationClient;
    }

    public PayrollResponseDTO generatePayroll(UUID tenantId, PayrollRequestDTO request) {

        log.info("========== GENERATE PAYROLL ==========");
        log.info("Tenant ID   : {}", tenantId);
        log.info("Employee ID : {}", request.getEmployeeId());
        log.info("Employee    : {}", request.getEmployeeName());

        payrollRepository.findByTenantIdAndEmployeeIdAndMonthAndYear(
                tenantId,
                request.getEmployeeId(),
                request.getMonth(),
                request.getYear()
        ).ifPresent(existing -> {
            throw new DuplicateResourceException(
                    "Payroll already generated for employee "
                            + request.getEmployeeId()
            );
        });

        BigDecimal basic = request.getBasicSalary();

        BigDecimal hra = request.getHra() != null
                ? request.getHra()
                : round(basic.multiply(HRA_RATE));

        BigDecimal da = request.getDa() != null
                ? request.getDa()
                : round(basic.multiply(DA_RATE));

        BigDecimal specialAllowance = request.getSpecialAllowance() != null
                ? request.getSpecialAllowance()
                : round(basic.multiply(SPECIAL_ALLOWANCE_RATE));

        BigDecimal pf = round(basic.multiply(PF_RATE));

        BigDecimal other = request.getOtherDeductions() != null
                ? request.getOtherDeductions()
                : BigDecimal.ZERO;

        BigDecimal gross = basic.add(hra).add(da).add(specialAllowance);

        BigDecimal net = gross
                .subtract(pf)
                .subtract(PROFESSIONAL_TAX)
                .subtract(other);

        Payroll payroll = new Payroll();

        payroll.setTenantId(tenantId);
        payroll.setEmployeeId(request.getEmployeeId());
        payroll.setEmployeeName(request.getEmployeeName());
        payroll.setMonth(request.getMonth());
        payroll.setYear(request.getYear());
        payroll.setBasicSalary(basic);
        payroll.setHra(hra);
        payroll.setDa(da);
        payroll.setSpecialAllowance(specialAllowance);
        payroll.setPfDeduction(pf);
        payroll.setProfessionalTax(PROFESSIONAL_TAX);
        payroll.setOtherDeductions(other);
        payroll.setGrossSalary(round(gross));
        payroll.setNetSalary(round(net));
        payroll.setStatus("GENERATED");
        payroll.setGeneratedOn(LocalDateTime.now());

        log.info("Saving payroll...");

        Payroll saved = payrollRepository.save(payroll);

        log.info("Payroll saved successfully.");
        log.info("Payroll ID : {}", saved.getId());

        NotificationRequest notification =
                new NotificationRequest(
                        saved.getEmployeeId(),
                        "Payroll Generated",
                        "Your payroll for "
                                + saved.getMonth()
                                + " "
                                + saved.getYear()
                                + " has been generated successfully.",
                        "SUCCESS"
                );

        notificationClient.send(notification);
        System.out.println("========= PAYROLL =========");
        System.out.println(notification.getEmployeeId());
        System.out.println(notification.getTitle());
        System.out.println(notification.getMessage());

        return toResponseDTO(saved);
    }

    public List<PayrollResponseDTO> getAllPayrolls(UUID tenantId) {
        return payrollRepository.findByTenantId(tenantId)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public PayrollResponseDTO getPayrollById(UUID tenantId, Long id) {
        Payroll payroll = payrollRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Payroll not found"));

        return toResponseDTO(payroll);
    }

    public List<PayrollResponseDTO> getPayrollsByEmployee(UUID tenantId, UUID employeeId) {
        return payrollRepository.findByTenantIdAndEmployeeId(tenantId, employeeId)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    public List<PayrollResponseDTO> getPayrollsByMonthYear(UUID tenantId, String month, Integer year) {
        return payrollRepository.findByTenantIdAndMonthAndYear(tenantId, month, year)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }
    public byte[] exportPayrollExcel(UUID tenantId) {

        List<Payroll> payrolls = payrollRepository.findByTenantId(tenantId);

        try (Workbook workbook = new XSSFWorkbook()) {

            Sheet sheet = workbook.createSheet("Payroll");

            Row header = sheet.createRow(0);

            header.createCell(0).setCellValue("Employee");
            header.createCell(1).setCellValue("Month");
            header.createCell(2).setCellValue("Year");
            header.createCell(3).setCellValue("Basic Salary");
            header.createCell(4).setCellValue("HRA");
            header.createCell(5).setCellValue("DA");
            header.createCell(6).setCellValue("Allowance");
            header.createCell(7).setCellValue("Gross Salary");
            header.createCell(8).setCellValue("Net Salary");
            header.createCell(9).setCellValue("Status");

            int rowNum = 1;

            for (Payroll p : payrolls) {

                Row row = sheet.createRow(rowNum++);

                row.createCell(0).setCellValue(p.getEmployeeName());
                row.createCell(1).setCellValue(p.getMonth());
                row.createCell(2).setCellValue(p.getYear());

                row.createCell(3).setCellValue(p.getBasicSalary().doubleValue());
                row.createCell(4).setCellValue(p.getHra().doubleValue());
                row.createCell(5).setCellValue(p.getDa().doubleValue());
                row.createCell(6).setCellValue(p.getSpecialAllowance().doubleValue());

                row.createCell(7).setCellValue(p.getGrossSalary().doubleValue());
                row.createCell(8).setCellValue(p.getNetSalary().doubleValue());

                row.createCell(9).setCellValue(p.getStatus());
            }

            for (int i = 0; i < 10; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();

            workbook.write(out);

            return out.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Failed to export payroll", e);
        }
    }
    public void deletePayroll(UUID tenantId, Long id) {
        Payroll payroll = payrollRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Payroll not found"));

        payrollRepository.delete(payroll);
    }

    private BigDecimal round(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }

    private PayrollResponseDTO toResponseDTO(Payroll p) {
        PayrollResponseDTO dto = new PayrollResponseDTO();

        dto.setId(p.getId());
        dto.setEmployeeId(p.getEmployeeId());
        dto.setEmployeeName(p.getEmployeeName());
        dto.setMonth(p.getMonth());
        dto.setYear(p.getYear());
        dto.setBasicSalary(p.getBasicSalary());
        dto.setHra(p.getHra());
        dto.setDa(p.getDa());
        dto.setSpecialAllowance(p.getSpecialAllowance());
        dto.setPfDeduction(p.getPfDeduction());
        dto.setProfessionalTax(p.getProfessionalTax());
        dto.setOtherDeductions(p.getOtherDeductions());
        dto.setGrossSalary(p.getGrossSalary());
        dto.setNetSalary(p.getNetSalary());
        dto.setStatus(p.getStatus());
        dto.setGeneratedOn(p.getGeneratedOn());

        return dto;
    }
    public byte[] generatePayslip(UUID tenantId, Long payrollId) {

        Payroll payroll = payrollRepository
                .findByIdAndTenantId(payrollId, tenantId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Payroll not found"));

        try {

            ByteArrayOutputStream output = new ByteArrayOutputStream();

            Document document = new Document();

            PdfWriter.getInstance(document, output);

            document.open();

            Font title = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20);

            Paragraph heading = new Paragraph("NexusHR Payslip", title);
            heading.setAlignment(Element.ALIGN_CENTER);

            document.add(heading);
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);

            table.addCell("Employee Name");
            table.addCell(payroll.getEmployeeName());

            table.addCell("Employee ID");
            table.addCell(payroll.getEmployeeId().toString());

            table.addCell("Month");
            table.addCell(payroll.getMonth());

            table.addCell("Year");
            table.addCell(String.valueOf(payroll.getYear()));

            table.addCell("Basic Salary");
            table.addCell("₹ " + payroll.getBasicSalary());

            table.addCell("HRA");
            table.addCell("₹ " + payroll.getHra());

            table.addCell("DA");
            table.addCell("₹ " + payroll.getDa());

            table.addCell("Special Allowance");
            table.addCell("₹ " + payroll.getSpecialAllowance());

            table.addCell("PF Deduction");
            table.addCell("₹ " + payroll.getPfDeduction());

            table.addCell("Professional Tax");
            table.addCell("₹ " + payroll.getProfessionalTax());

            table.addCell("Other Deductions");
            table.addCell("₹ " + payroll.getOtherDeductions());

            table.addCell("Gross Salary");
            table.addCell("₹ " + payroll.getGrossSalary());

            table.addCell("Net Salary");
            table.addCell("₹ " + payroll.getNetSalary());

            table.addCell("Status");
            table.addCell(payroll.getStatus());

            document.add(table);

            document.close();

            return output.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Unable to generate PDF", e);
        }
    }
   
}