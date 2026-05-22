UPDATE saved_statements
SET data = jsonb_set(
        data - 'packageName' - 'packagePrice',
        '{servicePackage}',
        CASE
            WHEN data->>'packageName' IS NOT NULL THEN
                jsonb_build_object(
                        'id', COALESCE(package_id, 0),
                        'sortOrder', 0,
                        'name', data->>'packageName',
                        'defaultCost', (data->>'packagePrice')::numeric,
                        'legacyPackage', false
                )
            ELSE 'null'::jsonb
            END
           )
WHERE data ? 'packageName';