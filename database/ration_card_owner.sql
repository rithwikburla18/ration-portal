ALTER TABLE ration_cards ADD COLUMN IF NOT EXISTS owner_email VARCHAR(255);
CREATE INDEX IF NOT EXISTS idx_ration_cards_owner_email ON ration_cards(owner_email);