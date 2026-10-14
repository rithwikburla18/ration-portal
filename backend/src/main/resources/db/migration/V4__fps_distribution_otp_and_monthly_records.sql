ALTER TABLE distribution_logs ADD COLUMN IF NOT EXISTS distribution_time TIME;
ALTER TABLE distribution_logs ADD COLUMN IF NOT EXISTS distribution_month VARCHAR(7);
ALTER TABLE distribution_logs ADD COLUMN IF NOT EXISTS transaction_id VARCHAR(50);
ALTER TABLE distribution_logs ADD COLUMN IF NOT EXISTS receipt_id VARCHAR(50);
ALTER TABLE distribution_logs ADD COLUMN IF NOT EXISTS dealer_email VARCHAR(150);

CREATE TABLE IF NOT EXISTS distribution_otps (
    id BIGSERIAL PRIMARY KEY,
    ration_card_number VARCHAR(20) NOT NULL,
    dealer_email VARCHAR(150) NOT NULL,
    otp_hash VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NOT NULL,
    attempts INTEGER NOT NULL DEFAULT 0,
    used BOOLEAN NOT NULL DEFAULT FALSE,
    verified_at TIMESTAMP,
    verification_token_hash VARCHAR(64),
    verification_expires_at TIMESTAMP,
    submitted_at TIMESTAMP
);
CREATE INDEX IF NOT EXISTS ix_distribution_otp_lookup
    ON distribution_otps(ration_card_number,dealer_email,created_at);

-- Backfill legacy records into one monthly row per card while preserving the total quantity.
UPDATE distribution_logs
SET distribution_month=TO_CHAR(distribution_date,'YYYY-MM')
WHERE distribution_month IS NULL AND ration_card_id IS NOT NULL;

WITH duplicates AS (
    SELECT ration_card_id, distribution_month, MIN(id) AS keep_id, SUM(quantity_kg) AS total_kg
    FROM distribution_logs
    WHERE ration_card_id IS NOT NULL AND distribution_month IS NOT NULL
    GROUP BY ration_card_id,distribution_month HAVING COUNT(*)>1
)
UPDATE distribution_logs d
SET quantity_kg=x.total_kg, transaction_status='COMPLETED'
FROM duplicates x WHERE d.id=x.keep_id;

WITH duplicates AS (
    SELECT ration_card_id, distribution_month, MIN(id) AS keep_id
    FROM distribution_logs
    WHERE ration_card_id IS NOT NULL AND distribution_month IS NOT NULL
    GROUP BY ration_card_id,distribution_month HAVING COUNT(*)>1
)
DELETE FROM distribution_logs d USING duplicates x
WHERE d.ration_card_id=x.ration_card_id AND d.distribution_month=x.distribution_month AND d.id<>x.keep_id;

UPDATE distribution_logs
SET transaction_id='LEGACY-RC-'||id::TEXT, receipt_id='LEGACY-RC-'||id::TEXT
WHERE transaction_id IS NULL OR receipt_id IS NULL;

CREATE UNIQUE INDEX IF NOT EXISTS ux_distribution_card_month
    ON distribution_logs(ration_card_id,distribution_month)
    WHERE ration_card_id IS NOT NULL AND distribution_month IS NOT NULL;
CREATE UNIQUE INDEX IF NOT EXISTS ux_distribution_transaction_id
    ON distribution_logs(transaction_id) WHERE transaction_id IS NOT NULL;
