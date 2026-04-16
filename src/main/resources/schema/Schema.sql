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
    checkpoint_date  DATE        NOT NULL,
    corporate_number VARCHAR(13) NOT NULL,
    inserted_at      TIMESTAMP   NOT NULL DEFAULT now()
);

CREATE TABLE export_job
(
    checkpoint_date  DATE        NOT NULL,
    corporate_number VARCHAR(13) NOT NULL,
    status           VARCHAR(20) NOT NULL DEFAULT 'pending',
    claimed_by       VARCHAR(255),
    claimed_at       TIMESTAMPTZ,
    completed_at     TIMESTAMPTZ,
    attempt_count    INT         NOT NULL DEFAULT 0,
    PRIMARY KEY (checkpoint_date, corporate_number)
);

CREATE TABLE pipeline_control
(
    pipeline_name    VARCHAR(20) PRIMARY KEY,
    phase            VARCHAR(20),
    export_requested BOOLEAN,
    transaction_date DATE,
    owner_node       VARCHAR(100),
    lease_until      TIMESTAMP,
    updated_at       TIMESTAMPTZ
);

insert into pipeline_control (pipeline_name,
                              phase,
                              export_requested,
                              owner_node,
                              lease_until,
                              updated_at)
values ('MAIN',
        'IDLE',
        false,
        null,
        null,
        now());

CREATE TABLE import_failures
(
    entry_name        VARCHAR(255) NOT NULL,
    transaction_date  DATE         NOT NULL,
    attempts          INT          NOT NULL DEFAULT 1,
    last_error        TEXT,
    last_attempted_at TIMESTAMPTZ  NOT NULL DEFAULT now(),
    PRIMARY KEY (entry_name, transaction_date)
);

CREATE INDEX idx_company_entry_checkpoint_corporate
    ON company_entry (checkpoint_date, corporate_number);