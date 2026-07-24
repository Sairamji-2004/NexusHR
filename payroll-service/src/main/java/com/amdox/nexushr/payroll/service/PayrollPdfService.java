package com.amdox.nexushr.payroll.service;

import com.amdox.nexushr.common.exception.ResourceNotFoundException;

import com.amdox.nexushr.payroll.entity.Payroll;
import com.amdox.nexushr.payroll.repository.PayrollRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

@Service
public class PayrollPdfService {

    private final PayrollRepository payrollRepository;

    public PayrollPdfService(PayrollRepository payrollRepository) {
        this.payrollRepository = payrollRepository;
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

            table.addCell("Employee");
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

            table.addCell("PF");
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