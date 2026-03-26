CREATE TABLE import_checkpoint
(
    date_completed DATE         NOT NULL,
    zip_entry_name VARCHAR(255) NOT NULL,
    PRIMARY KEY (date_completed, zip_entry_name)
);

CREATE TABLE company_entry
(
    entry_id         BIGSERIAL PRIMARY KEY,
    entry            jsonb       NOT NULL,
    corporate_number VARCHAR(13) NOT NULL,
    inserted_at      TIMESTAMP   NOT NULL DEFAULT now()
);

CREATE INDEX idx_raw_company_corporate_number ON company_entry (corporate_number);

CREATE TABLE export_job
(
    run_id           VARCHAR(36) NOT NULL,
    corporate_number VARCHAR(13) NOT NULL,
    status           VARCHAR(20) NOT NULL DEFAULT 'pending',
    claimed_by       VARCHAR(255),
    claimed_at       TIMESTAMPTZ,
    completed_at     TIMESTAMPTZ,
    PRIMARY KEY (run_id, corporate_number)
);