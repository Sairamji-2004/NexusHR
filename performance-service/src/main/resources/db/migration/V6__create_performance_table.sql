CREATE TABLE performance (

    id UUID PRIMARY KEY,

    employee_id UUID NOT NULL,

    employee_name VARCHAR(255) NOT NULL,

    department VARCHAR(255) NOT NULL,

    rating INTEGER NOT NULL,

    feedback TEXT,

    reviewer VARCHAR(255),

    review_date DATE,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);