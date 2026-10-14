CREATE TABLE IF NOT EXISTS fps_dealer_applications (
    id BIGSERIAL PRIMARY KEY,
    fps_id VARCHAR(40) UNIQUE,
    dealer_name VARCHAR(120) NOT NULL,
    shop_name VARCHAR(150) NOT NULL,
    license_number VARCHAR(80) NOT NULL UNIQUE,
    email VARCHAR(150) NOT NULL UNIQUE,
    mobile VARCHAR(15) NOT NULL,
    district VARCHAR(100) NOT NULL,
    mandal_village VARCHAR(150) NOT NULL,
    shop_address TEXT NOT NULL,
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    rejection_reason TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    decided_at TIMESTAMP
);
CREATE INDEX IF NOT EXISTS ix_fps_dealer_status_district ON fps_dealer_applications(status, district);
