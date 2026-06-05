CREATE TABLE license_keys (
    id SERIAL PRIMARY KEY,
    tenant_id UUID REFERENCES tenants (id) ON DELETE CASCADE NOT NULL,
    key TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    revoked_at TIMESTAMP
);

CREATE UNIQUE INDEX idx_license_keys_key ON license_keys (key);
CREATE INDEX IF NOT EXISTS idx_license_keys_tenant ON license_keys (tenant_id);