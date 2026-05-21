BEGIN;

ALTER TABLE saved_statements ADD COLUMN data JSONB;

UPDATE saved_statements s SET data = json_build_object(
        'services', (
            SELECT COALESCE(json_agg(json_build_object(
                    'serviceId', svc.service_id,
                    'inPackage', svc.in_package,
                    'name', c.name,
                    'price', c.default_cost
                                     )), '[]')
            FROM saved_statement_services svc
                     JOIN services c ON c.id = svc.service_id
            WHERE svc.statement_id = s.id
        ),
        'merchandise', (
            SELECT COALESCE(json_agg(json_build_object(
                    'merchandiseId', m.merchandise_id,
                    'name', c.name,
                    'price', m.price,
                    'description', m.description
                                     )), '[]')
            FROM saved_statement_merchandise m
                     JOIN merchandise c ON c.id = m.merchandise_id
            WHERE m.statement_id = s.id
        ),
        'specialCharges', (
            SELECT COALESCE(json_agg(json_build_object(
                    'specialChargeId', sc.special_charge_id,
                    'name', c.name,
                    'price', sc.price,
                    'description', sc.description
                                     )), '[]')
            FROM saved_statement_special_charges sc
                     JOIN special_charges c ON c.id = sc.special_charge_id
            WHERE sc.statement_id = s.id
        ),
        'cashAdvances', (
            SELECT COALESCE(json_agg(json_build_object(
                    'cashAdvanceId', ca.cash_advance_id,
                    'name', c.name,
                    'amount', ca.amount,
                    'provider', ca.provider
                                     )), '[]')
            FROM saved_statement_cash_advances ca
                     JOIN cash_advances c ON c.id = ca.cash_advance_id
            WHERE ca.statement_id = s.id
        )
                                     );

DROP TABLE saved_statement_services;
DROP TABLE saved_statement_merchandise;
DROP TABLE saved_statement_special_charges;
DROP TABLE saved_statement_cash_advances;

COMMIT;