CREATE TABLE activation_codes (
    id SERIAL PRIMARY KEY,
    tenant_id UUID REFERENCES tenants(id) ON DELETE CASCADE NOT NULL,
    code TEXT NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    used_at TIMESTAMP
);

CREATE TABLE license_keys (
    id SERIAL PRIMARY KEY,
    tenant_id UUID REFERENCES tenants (id) ON DELETE CASCADE NOT NULL,
    key UUID NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    revoked_at TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_activation_codes_tenant ON activation_codes (tenant_id);
CREATE INDEX IF NOT EXISTS idx_license_keys_tenant ON license_keys (tenant_id);