ALTER TABLE ration_cards ADD COLUMN IF NOT EXISTS approval_status VARCHAR(30);
UPDATE ration_cards SET approval_status='APPROVED' WHERE approval_status IS NULL AND UPPER(status) IN ('ACTIVE','APPROVED');
UPDATE ration_cards SET approval_status='PENDING' WHERE approval_status IS NULL;