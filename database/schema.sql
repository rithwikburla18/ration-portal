CREATE TABLE IF NOT EXISTS ration_cards (
    id BIGSERIAL PRIMARY KEY,
    ration_card_number VARCHAR(50) UNIQUE NOT NULL,
    card_type VARCHAR(20) NOT NULL,
    head_of_family VARCHAR(120),
    address TEXT,
    district VARCHAR(100),
    state VARCHAR(100),
    status VARCHAR(30) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS family_members (
    id BIGSERIAL PRIMARY KEY,
    ration_card_id BIGINT NOT NULL
        REFERENCES ration_cards(id)
        ON DELETE CASCADE,

    member_id VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(120) NOT NULL,
    age INT NOT NULL CHECK (age >= 0),
    gender VARCHAR(20),
    relationship VARCHAR(60),
    rice_quota_kg NUMERIC(10,2) NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS distribution_logs (
    id BIGSERIAL PRIMARY KEY,
    ration_card_id BIGINT
        REFERENCES ration_cards(id),

    member_id BIGINT
        REFERENCES family_members(id),

    quantity_kg NUMERIC(10,2) NOT NULL,
    distribution_date DATE NOT NULL,
    fps_id VARCHAR(50),
    transaction_status VARCHAR(30) NOT NULL
);