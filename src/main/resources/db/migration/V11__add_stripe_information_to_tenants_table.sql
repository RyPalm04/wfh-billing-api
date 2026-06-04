ALTER TABLE tenants
    ADD COLUMN IF NOT EXISTS supabase_user_id TEXT UNIQUE,
    ADD COLUMN IF NOT EXISTS stripe_customer_id             TEXT UNIQUE,
    ADD COLUMN IF NOT EXISTS stripe_subscription_id         TEXT UNIQUE;
