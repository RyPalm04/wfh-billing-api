 -- Enable RLS
ALTER TABLE services
    ENABLE ROW LEVEL SECURITY;
ALTER TABLE merchandise
    ENABLE ROW LEVEL SECURITY;
ALTER TABLE special_charges
    ENABLE ROW LEVEL SECURITY;
ALTER TABLE cash_advances
    ENABLE ROW LEVEL SECURITY;
ALTER TABLE service_packages
    ENABLE ROW LEVEL SECURITY;
ALTER TABLE saved_statements
    ENABLE ROW LEVEL SECURITY;

  -- Tenant isolation policy (all 6 tables)
CREATE POLICY tenant_isolation ON services
    USING ((auth.jwt() -> 'app_metadata' ->> 'tenant_id')::uuid = tenant_id);

CREATE POLICY tenant_isolation ON merchandise
    USING ((auth.jwt() -> 'app_metadata' ->> 'tenant_id')::uuid = tenant_id);

CREATE POLICY tenant_isolation ON special_charges
    USING ((auth.jwt() -> 'app_metadata' ->> 'tenant_id')::uuid = tenant_id);

CREATE POLICY tenant_isolation ON cash_advances
    USING ((auth.jwt() -> 'app_metadata' ->> 'tenant_id')::uuid = tenant_id);

CREATE POLICY tenant_isolation ON service_packages
    USING ((auth.jwt() -> 'app_metadata' ->> 'tenant_id')::uuid = tenant_id);

CREATE POLICY tenant_isolation ON saved_statements
    USING ((auth.jwt() -> 'app_metadata' ->> 'tenant_id')::uuid = tenant_id);

-- Platform admin bypass
CREATE POLICY platform_admin_bypass ON services
    USING ((auth.jwt() -> 'app_metadata' ->> 'app_role') = 'platform_admin');

CREATE POLICY platform_admin_bypass ON merchandise
    USING ((auth.jwt() -> 'app_metadata' ->> 'app_role') = 'platform_admin');

CREATE POLICY platform_admin_bypass ON special_charges
    USING ((auth.jwt() -> 'app_metadata' ->> 'app_role') = 'platform_admin');

CREATE POLICY platform_admin_bypass ON cash_advances
    USING ((auth.jwt() -> 'app_metadata' ->> 'app_role') = 'platform_admin');

CREATE POLICY platform_admin_bypass ON service_packages
    USING ((auth.jwt() -> 'app_metadata' ->> 'app_role') = 'platform_admin');

CREATE POLICY platform_admin_bypass ON saved_statements
    USING ((auth.jwt() -> 'app_metadata' ->> 'app_role') = 'platform_admin');