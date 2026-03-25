CREATE TABLE import_checkpoint
(
    date_completed DATE NOT NULL,
    zip_entry_name VARCHAR(255) NOT NULL,
    PRIMARY KEY (date_completed, zip_entry_name)
);

CREATE TABLE company_entry
(
    entry_id BIGSERIAL PRIMARY KEY,
    entry jsonb NOT NULL,
    inserted_at TIMESTAMP NOT NULL DEFAULT now()
);