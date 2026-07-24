CREATE TABLE leaves (
    id UUID PRIMARY KEY,

    employee_id UUID NOT NULL,
    employee_name VARCHAR(150) NOT NULL,

    leave_type VARCHAR(50) NOT NULL,

    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    total_days INTEGER NOT NULL,

    reason TEXT,

    status VARCHAR(30) NOT NULL,

    approved_by UUID,
    approved_date TIMESTAMP,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);