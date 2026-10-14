-- Additive, repeat-safe schema update for existing Neon installations.
ALTER TABLE ration_cards ADD COLUMN IF NOT EXISTS application_number VARCHAR(50);
ALTER TABLE ration_cards ADD COLUMN IF NOT EXISTS mobile VARCHAR(15);
ALTER TABLE ration_cards ADD COLUMN IF NOT EXISTS pincode VARCHAR(10);
ALTER TABLE ration_cards ADD COLUMN IF NOT EXISTS issued_at TIMESTAMP;
ALTER TABLE ration_cards ADD COLUMN IF NOT EXISTS document_emailed_at TIMESTAMP;
ALTER TABLE ration_cards ADD COLUMN IF NOT EXISTS owner_email VARCHAR(150);
ALTER TABLE ration_cards ADD COLUMN IF NOT EXISTS approval_status VARCHAR(30);
ALTER TABLE ration_card_applications ADD COLUMN IF NOT EXISTS completion_status VARCHAR(30) NOT NULL DEFAULT 'IN_PROGRESS';
ALTER TABLE ration_card_applications ADD COLUMN IF NOT EXISTS rejection_reason TEXT;
ALTER TABLE ration_card_applications ADD COLUMN IF NOT EXISTS issued_ration_card_number VARCHAR(20);
CREATE UNIQUE INDEX IF NOT EXISTS ux_ration_cards_application_number ON ration_cards(application_number) WHERE application_number IS NOT NULL;
CREATE TABLE IF NOT EXISTS audit_logs (id BIGSERIAL PRIMARY KEY, event_type VARCHAR(80) NOT NULL, actor_email VARCHAR(150), target_reference VARCHAR(100), outcome VARCHAR(30) NOT NULL, details TEXT, created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP);
CREATE TABLE IF NOT EXISTS notification_log (id BIGSERIAL PRIMARY KEY, notification_key VARCHAR(180) NOT NULL UNIQUE, recipient_email VARCHAR(150), notification_type VARCHAR(80) NOT NULL, status VARCHAR(20) NOT NULL DEFAULT 'PENDING', attempts INTEGER NOT NULL DEFAULT 0, last_error VARCHAR(300), next_attempt_at TIMESTAMP, created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, sent_at TIMESTAMP);
DO $$
DECLARE a RECORD; c RECORD; candidate TEXT; tries INTEGER;
BEGIN
 FOR a IN SELECT * FROM ration_card_applications WHERE UPPER(COALESCE(status,''))='APPROVED' LOOP
  SELECT * INTO c FROM ration_cards WHERE application_number=a.application_number OR ration_card_number='RC-'||a.application_number ORDER BY id LIMIT 1;
  IF FOUND THEN
   IF c.ration_card_number !~ '^[0-9]{12}$' THEN
    tries:=0;
    LOOP
     candidate:=LPAD(FLOOR(RANDOM()*1000000000000)::BIGINT::TEXT,12,'0');
     EXIT WHEN NOT EXISTS(SELECT 1 FROM ration_cards WHERE ration_card_number=candidate);
     tries:=tries+1; IF tries>50 THEN RAISE EXCEPTION 'Unable to allocate unique ration-card number during backfill'; END IF;
    END LOOP;
   ELSE candidate:=c.ration_card_number; END IF;
   UPDATE ration_cards SET ration_card_number=candidate, application_number=a.application_number, card_type=COALESCE(a.card_type,card_type), head_of_family=COALESCE(a.applicant_name,head_of_family), address=COALESCE(a.address,address), district=COALESCE(a.district,district), state=COALESCE(a.state,state), owner_email=LOWER(TRIM(COALESCE(a.email,owner_email))), mobile=COALESCE(a.mobile,mobile), pincode=COALESCE(a.pincode,pincode), status='ACTIVE', approval_status='ACTIVE', issued_at=COALESCE(issued_at,CURRENT_TIMESTAMP) WHERE id=c.id;
   UPDATE ration_card_applications SET completion_status='COMPLETED', issued_ration_card_number=candidate WHERE id=a.id;
   IF NOT EXISTS(SELECT 1 FROM family_members WHERE ration_card_id=c.id) THEN
    INSERT INTO family_members(ration_card_id,member_id,name,age,gender,relationship,rice_quota_kg)
    SELECT c.id,'MEM-'||UPPER(SUBSTRING(REPLACE(GEN_RANDOM_UUID()::TEXT,'-','') FROM 1 FOR 16)),COALESCE(m->>'name','Family Member'),GREATEST(0,LEAST(120,COALESCE(NULLIF(m->>'age','')::INTEGER,0))),NULLIF(m->>'gender',''),NULLIF(m->>'relationship',''),6.00
    FROM JSONB_ARRAY_ELEMENTS(COALESCE(NULLIF(a.family_members_data,'')::JSONB,'[]'::JSONB)) AS m;
   END IF;
  END IF;
 END LOOP;
END $$;
UPDATE ration_card_applications a SET completion_status='COMPLETED',issued_ration_card_number=rc.ration_card_number FROM ration_cards rc WHERE UPPER(COALESCE(a.status,''))='APPROVED' AND rc.application_number=a.application_number;
UPDATE ration_card_applications SET completion_status='NOT_APPLICABLE' WHERE UPPER(COALESCE(status,''))='REJECTED' AND completion_status='IN_PROGRESS';
