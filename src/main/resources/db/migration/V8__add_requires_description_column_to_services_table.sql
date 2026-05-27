ALTER TABLE services
    ADD COLUMN IF NOT EXISTS requires_description boolean DEFAULT (false),
    ALTER COLUMN default_cost DROP NOT NULL;

UPDATE services
SET requires_description = true,
    default_cost = null
WHERE services.id in (12, 13);
