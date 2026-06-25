ALTER TABLE IF EXISTS tenant_settings DROP CONSTRAINT tenant_settings_pkey;
ALTER TABLE IF EXISTS tenant_settings DROP COLUMN id;
ALTER TABLE IF EXISTS tenant_settings ADD PRIMARY KEY (tenant_id);