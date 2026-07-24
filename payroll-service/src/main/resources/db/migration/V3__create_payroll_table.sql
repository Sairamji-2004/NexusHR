CREATE TABLE IF NOT EXISTS payroll (
    id BIGSERIAL PRIMARY KEY,
    tenant_id UUID NOT NULL,
    employee_id UUID NOT NULL,
    employee_name VARCHAR(150) NOT NULL,
    month VARCHAR(20) NOT NULL,
    year INT NOT NULL,
    basic_salary DECIMAL(12,2) NOT NULL,
    hra DECIMAL(12,2) NOT NULL DEFAULT 0,
    da DECIMAL(12,2) NOT NULL DEFAULT 0,
    special_allowance DECIMAL(12,2) NOT NULL DEFAULT 0,
    pf_deduction DECIMAL(12,2) NOT NULL DEFAULT 0,
    professional_tax DECIMAL(12,2) NOT NULL DEFAULT 0,
    other_deductions DECIMAL(12,2) NOT NULL DEFAULT 0,
    gross_salary DECIMAL(12,2) NOT NULL,
    net_salary DECIMAL(12,2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'GENERATED',
    generated_on TIMESTAMP NOT NULL,
    CONSTRAINT uq_tenant_employee_month_year UNIQUE (tenant_id, employee_id, month, year)
);

CREATE INDEX idx_payroll_tenant_id ON payroll(tenant_id);

