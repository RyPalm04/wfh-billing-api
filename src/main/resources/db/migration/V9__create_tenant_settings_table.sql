CREATE TABLE tenant_settings (
    id INT PRIMARY KEY DEFAULT 1,
    sales_tax_rate DECIMAL(5, 4)
);

INSERT INTO tenant_settings(id, sales_tax_rate) VALUES (1, 0.0000);