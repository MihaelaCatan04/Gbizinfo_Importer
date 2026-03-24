CREATE SEQUENCE company_id_seq;
CREATE SEQUENCE patent_id_seq;
CREATE SEQUENCE workplace_info_id_seq;
CREATE SEQUENCE finance_id_seq;
CREATE SEQUENCE item_info_id_seq;
CREATE SEQUENCE certification_id_seq;
CREATE SEQUENCE classification_id_seq;
CREATE SEQUENCE commendation_id_seq;
CREATE SEQUENCE major_shareholder_id_seq;
CREATE SEQUENCE management_index_id_seq;
CREATE SEQUENCE procurement_id_seq;
CREATE SEQUENCE subsidy_id_seq;
CREATE SEQUENCE base_info_id_seq;
CREATE SEQUENCE compatibility_id_seq;
CREATE SEQUENCE women_activity_id_seq;

CREATE TABLE base_info
(
    base_info_id                               BIGINT PRIMARY KEY DEFAULT nextval('base_info_id_seq'),
    average_continuous_service_years_type      VARCHAR(255),
    average_continuous_service_years_male      DOUBLE PRECISION,
    average_continuous_service_years_female    DOUBLE PRECISION,
    average_continuous_service_years           DOUBLE PRECISION,
    average_age                                DOUBLE PRECISION,
    month_average_predetermined_overtime_hours DOUBLE PRECISION
);

CREATE TABLE certification
(
    certification_id       BIGINT PRIMARY KEY DEFAULT nextval('certification_id_seq'),
    date_of_approval       DATE,
    title                  VARCHAR(255),
    target                 VARCHAR(255),
    government_departments VARCHAR(255),
    category               VARCHAR(255)
);

CREATE TABLE classification
(
    classification_id BIGINT PRIMARY KEY DEFAULT nextval('classification_id_seq'),
    code_value        VARCHAR(255),
    code_name         VARCHAR(255),
    japanese          VARCHAR(255)
);

CREATE TABLE commendation
(
    commendation_id        BIGINT PRIMARY KEY DEFAULT nextval('commendation_id_seq'),
    date_of_commendation   DATE,
    title                  VARCHAR(255),
    target                 TEXT,
    category               VARCHAR(255),
    government_departments VARCHAR(255),
    note                   TEXT
);

CREATE TABLE compatibility_of_childcare_and_work
(
    compatibility_of_childcare_and_work_id BIGINT PRIMARY KEY DEFAULT nextval('compatibility_id_seq'),
    number_of_paternity_leave              INTEGER,
    number_of_maternity_leave              INTEGER,
    paternity_leave_acquisition_num        INTEGER,
    maternity_leave_acquisition_num        INTEGER
);

CREATE TABLE finance
(
    finance_id             BIGINT PRIMARY KEY DEFAULT nextval('finance_id_seq'),
    accounting_standards   VARCHAR(255),
    fiscal_year_cover_page TEXT
);

CREATE TABLE item_info
(
    info_id     BIGINT PRIMARY KEY DEFAULT nextval('item_info_id_seq'),
    value       VARCHAR(255) NOT NULL,
    is_industry BOOLEAN      NOT NULL
);

CREATE TABLE major_shareholder
(
    major_shareholder_id    BIGINT PRIMARY KEY DEFAULT nextval('major_shareholder_id_seq'),
    name_major_stakeholders VARCHAR(255),
    shareholding_ratio      DOUBLE PRECISION NOT NULL
);

CREATE TABLE management_index
(
    management_index_id                                           BIGINT PRIMARY KEY DEFAULT nextval('management_index_id_seq'),
    period                                                        INTEGER,
    net_sales_summary_of_business_results                         BIGINT,
    net_sales_summary_of_business_results_unit_ref                VARCHAR(10),
    operating_revenue1_summary_of_business_results                BIGINT,
    operating_revenue1_summary_of_business_results_unit_ref       VARCHAR(10),
    operating_revenue2_summary_of_business_results                BIGINT,
    operating_revenue2_summary_of_business_results_unit_ref       VARCHAR(10),
    gross_operating_revenue_summary_of_business_results           BIGINT,
    gross_operating_revenue_summary_of_business_results_unit_ref  VARCHAR(10),
    ordinary_income_summary_of_business_results                   BIGINT,
    ordinary_income_summary_of_business_results_unit_ref          VARCHAR(10),
    net_premiums_written_summary_of_business_results_ins          BIGINT,
    net_premiums_written_summary_of_business_results_ins_unit_ref VARCHAR(10),
    ordinary_income_loss_summary_of_business_results              BIGINT,
    ordinary_income_loss_summary_of_business_results_unit_ref     VARCHAR(10),
    net_income_loss_summary_of_business_results                   BIGINT,
    net_income_loss_summary_of_business_results_unit_ref          VARCHAR(10),
    capital_stock_summary_of_business_results                     BIGINT,
    capital_stock_summary_of_business_results_unit_ref            VARCHAR(10),
    net_assets_summary_of_business_results                        BIGINT,
    net_assets_summary_of_business_results_unit_ref               VARCHAR(10),
    total_assets_summary_of_business_results                      BIGINT,
    total_assets_summary_of_business_results_unit_ref             VARCHAR(10),
    number_of_employees                                           BIGINT,
    number_of_employees_unit_ref                                  VARCHAR(10)
);

CREATE TABLE patent
(
    patent_id           BIGINT PRIMARY KEY DEFAULT nextval('patent_id_seq'),
    patent_type         VARCHAR(255) NOT NULL,
    registration_number VARCHAR(255) NOT NULL,
    application_date    DATE,
    title               TEXT         NOT NULL,
    url                 TEXT
);

CREATE TABLE procurement
(
    procurement_id         BIGINT PRIMARY KEY DEFAULT nextval('procurement_id_seq'),
    date_of_order          TIMESTAMPTZ,
    title                  VARCHAR(255) NOT NULL,
    amount                 BIGINT,
    government_departments VARCHAR(255),
    note                   TEXT
);

CREATE TABLE subsidy
(
    subsidy_id             BIGINT PRIMARY KEY DEFAULT nextval('subsidy_id_seq'),
    date_of_approval       DATE,
    title                  VARCHAR(255) NOT NULL,
    amount                 BIGINT,
    target                 VARCHAR(255),
    government_departments VARCHAR(255)
);

CREATE TABLE women_activity_info
(
    women_activity_info_id         BIGINT PRIMARY KEY DEFAULT nextval('women_activity_id_seq'),
    female_workers_proportion_type VARCHAR(255),
    female_workers_proportion      DOUBLE PRECISION,
    female_share_of_manager        DOUBLE PRECISION,
    gender_total_of_manager        DOUBLE PRECISION,
    female_share_of_officers       DOUBLE PRECISION,
    gender_total_of_officers       DOUBLE PRECISION
);

CREATE TABLE workplace_info
(
    workplace_info_id                   BIGINT PRIMARY KEY DEFAULT nextval('workplace_info_id_seq'),
    base_info                           BIGINT,
    women_activity_info                 BIGINT,
    compatibility_of_childcare_and_work BIGINT,
    inserted_at                         TIMESTAMPTZ        DEFAULT now(),
    deleted                             BOOLEAN NOT NULL   DEFAULT FALSE,
    FOREIGN KEY (base_info) REFERENCES base_info (base_info_id),
    FOREIGN KEY (women_activity_info) REFERENCES women_activity_info (women_activity_info_id),
    FOREIGN KEY (compatibility_of_childcare_and_work) REFERENCES compatibility_of_childcare_and_work (compatibility_of_childcare_and_work_id)
);

CREATE TABLE company
(
    company_id            BIGINT PRIMARY KEY DEFAULT nextval('company_id_seq'),
    corporate_number      VARCHAR(13) UNIQUE NOT NULL,
    name                  VARCHAR(255),
    kana                  VARCHAR(255),
    name_en               VARCHAR(255),
    postal_code           VARCHAR(255),
    location              VARCHAR(255),
    process               VARCHAR(255),
    aggregated_year       VARCHAR(1),
    status                VARCHAR(255),
    close_date            DATE,
    close_cause           VARCHAR(255),
    kind                  VARCHAR(10),
    representative_name   VARCHAR(255),
    capital_stock         BIGINT,
    employee_number       INTEGER,
    company_size_male     INTEGER,
    company_size_female   INTEGER,
    business_summary      TEXT,
    company_url           TEXT,
    founding_year         INTEGER,
    date_of_establishment DATE,
    qualification_grade   VARCHAR(255),
    workplace_info        BIGINT,
    update_date           TIMESTAMPTZ,
    FOREIGN KEY (workplace_info) REFERENCES workplace_info (workplace_info_id)
);

CREATE TABLE company_certification
(
    company_id       BIGINT NOT NULL,
    certification_id BIGINT NOT NULL,
    FOREIGN KEY (company_id) REFERENCES company (company_id),
    FOREIGN KEY (certification_id) REFERENCES certification (certification_id),
    UNIQUE (company_id, certification_id)
);

CREATE TABLE company_commendation
(
    company_id      BIGINT NOT NULL,
    commendation_id BIGINT NOT NULL,
    FOREIGN KEY (company_id) REFERENCES company (company_id),
    FOREIGN KEY (commendation_id) REFERENCES commendation (commendation_id),
    UNIQUE (company_id, commendation_id)
);

CREATE TABLE company_finance
(
    company_id BIGINT NOT NULL,
    finance_id BIGINT NOT NULL,
    FOREIGN KEY (company_id) REFERENCES company (company_id),
    FOREIGN KEY (finance_id) REFERENCES finance (finance_id),
    UNIQUE (company_id, finance_id)
);

CREATE TABLE company_item
(
    company_id BIGINT NOT NULL REFERENCES company (company_id),
    info_id    BIGINT NOT NULL REFERENCES item_info (info_id),
    UNIQUE (company_id, info_id)
);

CREATE TABLE company_patent
(
    company_id BIGINT NOT NULL,
    patent_id  BIGINT NOT NULL,
    FOREIGN KEY (company_id) REFERENCES company (company_id),
    FOREIGN KEY (patent_id) REFERENCES patent (patent_id),
    UNIQUE (company_id, patent_id)
);

CREATE TABLE company_procurement
(
    company_id     BIGINT NOT NULL,
    procurement_id BIGINT NOT NULL,
    FOREIGN KEY (company_id) REFERENCES company (company_id),
    FOREIGN KEY (procurement_id) REFERENCES procurement (procurement_id),
    UNIQUE (company_id, procurement_id)
);

CREATE TABLE company_subsidy
(
    company_id BIGINT NOT NULL,
    subsidy_id BIGINT NOT NULL,
    FOREIGN KEY (company_id) REFERENCES company (company_id),
    FOREIGN KEY (subsidy_id) REFERENCES subsidy (subsidy_id),
    UNIQUE (company_id, subsidy_id)
);

CREATE TABLE finance_management
(
    finance_id          BIGINT NOT NULL,
    management_index_id BIGINT NOT NULL,
    FOREIGN KEY (finance_id) REFERENCES finance (finance_id),
    FOREIGN KEY (management_index_id) REFERENCES management_index (management_index_id),
    UNIQUE (finance_id, management_index_id)
);

CREATE TABLE finance_shareholder
(
    finance_id           BIGINT NOT NULL,
    major_shareholder_id BIGINT NOT NULL,
    FOREIGN KEY (finance_id) REFERENCES finance (finance_id),
    FOREIGN KEY (major_shareholder_id) REFERENCES major_shareholder (major_shareholder_id),
    UNIQUE (finance_id, major_shareholder_id)
);

CREATE TABLE patent_classification
(
    patent_id         BIGINT NOT NULL,
    classification_id BIGINT NOT NULL,
    FOREIGN KEY (patent_id) REFERENCES patent (patent_id),
    FOREIGN KEY (classification_id) REFERENCES classification (classification_id),
    UNIQUE (patent_id, classification_id)
);
