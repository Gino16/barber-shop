-- Apply this migration after assigning a real price to every existing item.
-- It intentionally fails instead of inventing a price for historical catalog rows.
BEGIN;

ALTER TABLE items
    ADD COLUMN IF NOT EXISTS price DECIMAL(19, 2);

DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM items WHERE price IS NULL) THEN
        RAISE EXCEPTION 'Cannot make items.price mandatory: assign prices to existing items first';
    END IF;
END $$;

ALTER TABLE items
    ALTER COLUMN price SET NOT NULL,
    ADD CONSTRAINT items_price_non_negative CHECK (price >= 0);

ALTER TABLE sales
    ALTER COLUMN total_amount TYPE DECIMAL(19, 2),
    ALTER COLUMN discount TYPE DECIMAL(19, 2);

ALTER TABLE sale_items
    ALTER COLUMN unit_price TYPE DECIMAL(19, 2),
    ALTER COLUMN subtotal_amount TYPE DECIMAL(19, 2);

ALTER TABLE sales
    ADD CONSTRAINT sales_discount_non_negative CHECK (discount >= 0),
    ADD CONSTRAINT sales_total_non_negative CHECK (total_amount >= 0);

ALTER TABLE sale_items
    ADD CONSTRAINT sale_items_unit_price_non_negative CHECK (unit_price >= 0),
    ADD CONSTRAINT sale_items_subtotal_non_negative CHECK (subtotal_amount >= 0),
    ADD CONSTRAINT sale_items_quantity_positive CHECK (quantity > 0);

COMMIT;
