CREATE TABLE stored_files
(
    id UUID PRIMARY KEY,

    original_filename VARCHAR(500) NOT NULL,

    storage_key VARCHAR(255) NOT NULL UNIQUE,

    content_type VARCHAR(255) NOT NULL,

    size BIGINT NOT NULL,

    processing_status VARCHAR(30) NOT NULL,

    created_at TIMESTAMPTZ NOT NULL,

    updated_at TIMESTAMPTZ NOT NULL,

    CONSTRAINT chk_stored_files_size
        CHECK (size >= 0)
    );

CREATE INDEX idx_stored_files_processing_status
    ON stored_files (processing_status);