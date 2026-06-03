-- Add tenants table
CREATE TABLE IF NOT EXISTS tenants
(
    id         UUID PRIMARY KEY,
    name       VARCHAR,
    status     VARCHAR,
    created_at TIMESTAMP
);

-- Add tenant_id uuid columns to each table
ALTER TABLE saved_statements
    ADD COLUMN IF NOT EXISTS tenant_id UUID REFERENCES tenants (id) ON DELETE CASCADE;
ALTER TABLE services
    ADD COLUMN IF NOT EXISTS tenant_id UUID REFERENCES tenants (id) ON DELETE CASCADE;
ALTER TABLE merchandise
    ADD COLUMN IF NOT EXISTS tenant_id UUID REFERENCES tenants (id) ON DELETE CASCADE;
ALTER TABLE special_charges
    ADD COLUMN IF NOT EXISTS tenant_id UUID REFERENCES tenants (id) ON DELETE CASCADE;
ALTER TABLE cash_advances
    ADD COLUMN IF NOT EXISTS tenant_id UUID REFERENCES tenants (id) ON DELETE CASCADE;
ALTER TABLE service_packages
    ADD COLUMN IF NOT EXISTS tenant_id UUID REFERENCES tenants (id) ON DELETE CASCADE;
ALTER TABLE tenant_settings
    ADD COLUMN IF NOT EXISTS tenant_id UUID REFERENCES tenants (id) ON DELETE CASCADE;

-- Seed tenants table with base user
INSERT INTO tenants (id, name, status, created_at)
VALUES ('00000000-0000-0000-0000-000000000000', 'Eternatel Platform Master', 'platform_manager', NOW())
ON CONFLICT (id) DO NOTHING;

-- Fill out the columns
UPDATE saved_statements
SET tenant_id = '00000000-0000-0000-0000-000000000000'
WHERE tenant_id IS NULL;
UPDATE services
SET tenant_id = '00000000-0000-0000-0000-000000000000'
WHERE tenant_id IS NULL;
UPDATE merchandise
SET tenant_id = '00000000-0000-0000-0000-000000000000'
WHERE tenant_id IS NULL;
UPDATE special_charges
SET tenant_id = '00000000-0000-0000-0000-000000000000'
WHERE tenant_id IS NULL;
UPDATE cash_advances
SET tenant_id = '00000000-0000-0000-0000-000000000000'
WHERE tenant_id IS NULL;
UPDATE service_packages
SET tenant_id = '00000000-0000-0000-0000-000000000000'
WHERE tenant_id IS NULL;
UPDATE tenant_settings
SET tenant_id = '00000000-0000-0000-0000-000000000000'
WHERE tenant_id IS NULL;

-- Enforce NOT NULL constraint
ALTER TABLE saved_statements
    ALTER COLUMN tenant_id SET NOT NULL;
ALTER TABLE services
    ALTER COLUMN tenant_id SET NOT NULL;
ALTER TABLE merchandise
    ALTER COLUMN tenant_id SET NOT NULL;
ALTER TABLE special_charges
    ALTER COLUMN tenant_id SET NOT NULL;
ALTER TABLE cash_advances
    ALTER COLUMN tenant_id SET NOT NULL;
ALTER TABLE service_packages
    ALTER COLUMN tenant_id SET NOT NULL;
ALTER TABLE tenant_settings
    ALTER COLUMN tenant_id SET NOT NULL;

CREATE INDEX idx_saved_statements_tenant ON saved_statements (tenant_id);
CREATE INDEX idx_services_tenant ON services (tenant_id);
CREATE INDEX idx_merchandise_tenant ON merchandise (tenant_id);
CREATE INDEX idx_special_charges_tenant ON special_charges (tenant_id);
CREATE INDEX idx_cash_advances_tenant ON cash_advances (tenant_id);
CREATE INDEX idx_service_packages_tenant ON service_packages (tenant_id);
CREATE INDEX idx_tenant_settings_tenant ON tenant_settings (tenant_id);