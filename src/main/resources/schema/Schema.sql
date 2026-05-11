CREATE TABLE pipeline_control
(
    id               BIGSERIAL PRIMARY KEY,
    transaction_date DATE        NOT NULL UNIQUE,
    status           VARCHAR(20) NOT NULL DEFAULT 'IMPORTING',
    processed_count  INT         NOT NULL DEFAULT 0,
    total_count      INT,
    created_at       TIMESTAMP   NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMP   NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_pipeline_control_status ON pipeline_control (status);
CREATE INDEX idx_pipeline_control_date ON pipeline_control (transaction_date);

CREATE TABLE import_checkpoint
(
    date_completed DATE         NOT NULL,
    zip_entry_name VARCHAR(255) NOT NULL,
    PRIMARY KEY (date_completed, zip_entry_name)
);

CREATE TABLE import_failures
(
    entry_name        VARCHAR(255) NOT NULL,
    transaction_date  DATE         NOT NULL,
    attempts          INT          NOT NULL DEFAULT 1,
    last_error        TEXT,
    last_attempted_at TIMESTAMPTZ  NOT NULL DEFAULT now(),
    PRIMARY KEY (entry_name, transaction_date)
);

CREATE TABLE company_entry
(
    entry_id         BIGSERIAL PRIMARY KEY,
    entry            JSONB       NOT NULL,
    checkpoint_date  DATE        NOT NULL,
    corporate_number VARCHAR(13) NOT NULL,
    inserted_at      TIMESTAMP   NOT NULL DEFAULT now()
);

CREATE INDEX idx_company_entry_checkpoint_corporate
    ON company_entry (checkpoint_date, corporate_number);

CREATE TABLE export_job
(
    checkpoint_date  DATE        NOT NULL,
    corporate_number VARCHAR(13) NOT NULL,
    status           VARCHAR(20) NOT NULL DEFAULT 'pending',
    claimed_by       VARCHAR(255),
    claimed_at       TIMESTAMPTZ,
    locked_until     TIMESTAMPTZ,
    completed_at     TIMESTAMPTZ,
    attempt_count    INT         NOT NULL DEFAULT 0,
    last_error       TEXT,
    PRIMARY KEY (checkpoint_date, corporate_number)
);

CREATE INDEX idx_export_job_checkpoint_corporate ON export_job (checkpoint_date, corporate_number);

CREATE INDEX idx_export_job_checkpoint_corporate_claimed ON export_job (checkpoint_date, corporate_number, claimed_by);

CREATE TABLE IF NOT EXISTS flow_runs
(
    id       SERIAL PRIMARY KEY,
    run_at   TIMESTAMP DEFAULT NOW(),
    status   VARCHAR(20),
    zip_size BIGINT,
    version  INTEGER,
    folder   VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS processed_files
(
    id             BIGSERIAL    NOT NULL PRIMARY KEY,
    file_name      VARCHAR(255) NOT NULL,
    path           VARCHAR(255) NOT NULL,
    file_date      DATE         NOT NULL,
    is_processed   BOOLEAN               DEFAULT FALSE,
    date_processed TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_processed_files_name_date UNIQUE (file_name, file_date)
);


CREATE INDEX idx_company_entry_corporate_checkpoint
    ON company_entry (corporate_number, checkpoint_date);

CREATE INDEX brin_company_entry_checkpoint ON company_entry USING BRIN (checkpoint_date);

CREATE INDEX idx_export_job_claim_pending
    ON export_job (
                   status,
                   locked_until,
                   attempt_count,
                   corporate_number
        );

CREATE INDEX idx_export_job_claimed_by ON export_job (claimed_by, status);

CREATE INDEX idx_processed_files_unprocessed ON processed_files (id) WHERE is_processed = FALSE;
