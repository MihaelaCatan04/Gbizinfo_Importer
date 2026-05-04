CREATE TABLE pipeline_control
(
    pipeline_name    VARCHAR PRIMARY KEY,
    phase            VARCHAR   NOT NULL DEFAULT 'IDLE',
    owner_node       VARCHAR,
    lease_until      TIMESTAMP,
    transaction_date DATE,
    updated_at       TIMESTAMP NOT NULL DEFAULT now()
);

INSERT INTO pipeline_control (pipeline_name, phase, updated_at)
VALUES ('MAIN', 'IDLE', now())
ON CONFLICT DO NOTHING;

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

CREATE INDEX idx_export_job_checkpoint_corporate
    ON export_job (checkpoint_date, corporate_number);

CREATE INDEX idx_export_job_checkpoint_corporate_claimed
    ON export_job (checkpoint_date, corporate_number, claimed_by);