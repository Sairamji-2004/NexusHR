CREATE TABLE notifications (

    id UUID PRIMARY KEY,

    employee_id UUID NOT NULL,

    employee_name VARCHAR(100),

    title VARCHAR(255) NOT NULL,

    message TEXT NOT NULL,

    type VARCHAR(50),

    status VARCHAR(30),

    created_at TIMESTAMP,

    sent_at TIMESTAMP

);