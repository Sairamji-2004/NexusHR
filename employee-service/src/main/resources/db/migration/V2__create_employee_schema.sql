-- ============================================================
-- NexusHR :: Employee Service — Flyway V2
-- Creates: departments, grades, employees, employment_history
-- ============================================================

-- ── DEPARTMENTS ──────────────────────────────────────────────
CREATE TABLE departments (
    id          UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id   UUID         NOT NULL,
    name        VARCHAR(200) NOT NULL,
    code        VARCHAR(20)  NOT NULL,
    parent_id   UUID         REFERENCES departments(id),
    head_id     UUID,        -- references employees(id) — set after employees created
    is_active   BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    UNIQUE(tenant_id, code)
);

CREATE INDEX idx_departments_tenant_id ON departments(tenant_id);
CREATE INDEX idx_departments_parent_id ON departments(parent_id);

-- ── GRADES ───────────────────────────────────────────────────
CREATE TABLE grades (
    id          SERIAL       PRIMARY KEY,
    tenant_id   UUID         NOT NULL,
    name        VARCHAR(50)  NOT NULL,   -- e.g. L1, L2, Senior, Lead
    level       INT          NOT NULL,   -- numeric level for hierarchy
    min_salary  NUMERIC(15,2),
    max_salary  NUMERIC(15,2),
    UNIQUE(tenant_id, name)
);

-- ── EMPLOYEES ────────────────────────────────────────────────
CREATE TABLE employees (
    id              UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID         NOT NULL,
    employee_code   VARCHAR(20)  NOT NULL,   -- e.g. EMP001
    first_name      VARCHAR(100) NOT NULL,
    last_name       VARCHAR(100) NOT NULL,
    email           VARCHAR(255) NOT NULL,
    phone           VARCHAR(20),
    date_of_birth   DATE,
    gender          VARCHAR(10),

    -- Employment details
    department_id   UUID         REFERENCES departments(id),
    manager_id      UUID         REFERENCES employees(id),  -- self-join for org chart
    grade_id        INT          REFERENCES grades(id),
    designation     VARCHAR(200),
    employment_type VARCHAR(20)  NOT NULL DEFAULT 'FULL_TIME',  -- FULL_TIME, PART_TIME, CONTRACT
    status          VARCHAR(20)  NOT NULL DEFAULT 'ACTIVE',     -- ACTIVE, ON_LEAVE, TERMINATED, RESIGNED

    -- Dates
    hire_date       DATE         NOT NULL,
    probation_end   DATE,
    confirmation_date DATE,
    last_working_day DATE,

    -- Salary
    current_ctc     NUMERIC(15,2),
    current_basic   NUMERIC(15,2),

    -- Address
    address_line1   VARCHAR(255),
    address_line2   VARCHAR(255),
    city            VARCHAR(100),
    state           VARCHAR(100),
    country         VARCHAR(100) DEFAULT 'India',
    pincode         VARCHAR(10),

    -- Documents
    pan_number      VARCHAR(20),
    aadhar_number   VARCHAR(20),
    uan_number      VARCHAR(20),   -- PF UAN

    -- Profile
    profile_photo_url TEXT,
    bio             TEXT,

    -- Soft delete
    is_active       BOOLEAN      NOT NULL DEFAULT TRUE,

    -- Audit
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(100),
    updated_by      VARCHAR(100),

    UNIQUE(tenant_id, employee_code),
    UNIQUE(tenant_id, email)
);

-- Indexes for performance
CREATE INDEX idx_employees_tenant_id     ON employees(tenant_id);
CREATE INDEX idx_employees_department_id ON employees(department_id);
CREATE INDEX idx_employees_manager_id    ON employees(manager_id);
CREATE INDEX idx_employees_status        ON employees(status);
CREATE INDEX idx_employees_email         ON employees(email);
-- Trigram index for name search
CREATE INDEX idx_employees_name_trgm ON employees
    USING gin((first_name || ' ' || last_name) gin_trgm_ops);

-- ── EMPLOYMENT_HISTORY ────────────────────────────────────────
-- Tracks every promotion, transfer, salary change
CREATE TABLE employment_history (
    id              UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id     UUID         NOT NULL REFERENCES employees(id),
    change_type     VARCHAR(30)  NOT NULL, -- HIRE, PROMOTION, TRANSFER, SALARY_CHANGE, TERMINATION
    effective_date  DATE         NOT NULL,
    from_department UUID         REFERENCES departments(id),
    to_department   UUID         REFERENCES departments(id),
    from_grade_id   INT          REFERENCES grades(id),
    to_grade_id     INT          REFERENCES grades(id),
    from_designation VARCHAR(200),
    to_designation   VARCHAR(200),
    from_ctc        NUMERIC(15,2),
    to_ctc          NUMERIC(15,2),
    from_manager_id UUID         REFERENCES employees(id),
    to_manager_id   UUID         REFERENCES employees(id),
    remarks         TEXT,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(100)
);

CREATE INDEX idx_emp_history_employee_id ON employment_history(employee_id);

-- ── SEED: Default department for demo tenant ─────────────────
INSERT INTO departments (id, tenant_id, name, code) VALUES
    ('aaaaaaaa-0000-0000-0000-000000000001',
     '00000000-0000-0000-0000-000000000001',
     'Human Resources', 'HR'),
    ('aaaaaaaa-0000-0000-0000-000000000002',
     '00000000-0000-0000-0000-000000000001',
     'Engineering', 'ENG'),
    ('aaaaaaaa-0000-0000-0000-000000000003',
     '00000000-0000-0000-0000-000000000001',
     'Finance', 'FIN');

INSERT INTO grades (tenant_id, name, level, min_salary, max_salary) VALUES
    ('00000000-0000-0000-0000-000000000001', 'L1 - Junior',    1, 300000,  600000),
    ('00000000-0000-0000-0000-000000000001', 'L2 - Mid',       2, 600000,  1200000),
    ('00000000-0000-0000-0000-000000000001', 'L3 - Senior',    3, 1200000, 2000000),
    ('00000000-0000-0000-0000-000000000001', 'L4 - Lead',      4, 2000000, 3500000),
    ('00000000-0000-0000-0000-000000000001', 'L5 - Manager',   5, 3500000, 6000000),
    ('00000000-0000-0000-0000-000000000001', 'L6 - Director',  6, 6000000, 12000000);
