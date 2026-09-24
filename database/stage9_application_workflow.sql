ALTER TABLE ration_card_applications
ADD COLUMN IF NOT EXISTS application_number VARCHAR(50);

ALTER TABLE ration_card_applications
ADD COLUMN IF NOT EXISTS mobile VARCHAR(15);

ALTER TABLE ration_card_applications
ADD COLUMN IF NOT EXISTS email VARCHAR(150);

ALTER TABLE ration_card_applications
ADD COLUMN IF NOT EXISTS card_type VARCHAR(20);

ALTER TABLE ration_card_applications
ADD COLUMN IF NOT EXISTS state VARCHAR(100);

ALTER TABLE ration_card_applications
ADD COLUMN IF NOT EXISTS pincode VARCHAR(10);

ALTER TABLE ration_card_applications
ADD COLUMN IF NOT EXISTS family_members_data TEXT;

UPDATE ration_card_applications
SET application_number = 'RCAPP-' || LPAD(id::text, 6, '0')
WHERE application_number IS NULL;

ALTER TABLE ration_card_applications
ALTER COLUMN application_number SET NOT NULL;

CREATE UNIQUE INDEX IF NOT EXISTS idx_ration_card_applications_application_number
ON ration_card_applications(application_number);