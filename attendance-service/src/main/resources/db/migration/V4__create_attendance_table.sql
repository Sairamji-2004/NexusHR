CREATE TABLE attendance (
    id VARCHAR(36) PRIMARY KEY,

    employee_id VARCHAR(255) NOT NULL,

    attendance_date DATE NOT NULL,

    check_in_time TIMESTAMP,

    check_out_time TIMESTAMP,

    working_hours DOUBLE PRECISION,

    status VARCHAR(50) NOT NULL,

    created_at TIMESTAMP NOT NULL,

    updated_at TIMESTAMP
);