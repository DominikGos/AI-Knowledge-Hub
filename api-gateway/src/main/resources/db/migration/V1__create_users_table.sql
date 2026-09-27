CREATE TABLE users
(
    id UUID PRIMARY KEY,

    name VARCHAR(255),

    email VARCHAR(255),

    password VARCHAR(255),

    created_at TIMESTAMPTZ NOT NULL,

    updated_at TIMESTAMPTZ NOT NULL
);