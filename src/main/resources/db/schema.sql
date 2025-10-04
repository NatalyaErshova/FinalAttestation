-- Создание таблицы статусов заказов
CREATE TABLE IF NOT EXISTS order_status (
    id SERIAL PRIMARY KEY,
    status_name VARCHAR(50) NOT NULL UNIQUE
);

COMMENT ON TABLE order_status IS 'Справочник статусов заказов';
COMMENT ON COLUMN order_status.id IS 'Уникальный идентификатор статуса';
COMMENT ON COLUMN order_status.status_name IS 'Наименование статуса';

-- Создание таблицы товаров
CREATE TABLE IF NOT EXISTS product (
    id SERIAL PRIMARY KEY,
    description TEXT NOT NULL,
    price DECIMAL(10,2) NOT NULL CHECK (price >= 0),
    quantity INTEGER NOT NULL CHECK (quantity >= 0),
    category VARCHAR(100) NOT NULL
);

COMMENT ON TABLE product IS 'Таблица товаров';
COMMENT ON COLUMN product.id IS 'Уникальный идентификатор товара';
COMMENT ON COLUMN product.description IS 'Описание товара';
COMMENT ON COLUMN product.price IS 'Стоимость товара (должна быть >= 0)';
COMMENT ON COLUMN product.quantity IS 'Количество на складе (должно быть >= 0)';
COMMENT ON COLUMN product.category IS 'Категория товара';

-- Создание таблицы покупателей
CREATE TABLE IF NOT EXISTS customer (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    email VARCHAR(100)
);

COMMENT ON TABLE customer IS 'Таблица покупателей';
COMMENT ON COLUMN customer.id IS 'Уникальный идентификатор покупателя';
COMMENT ON COLUMN customer.first_name IS 'Имя покупателя';
COMMENT ON COLUMN customer.last_name IS 'Фамилия покупателя';
COMMENT ON COLUMN customer.phone IS 'Телефон покупателя';
COMMENT ON COLUMN customer.email IS 'Email покупателя';

-- Создание таблицы заказов (используем order_table т.к. order - зарезервированное слово)
CREATE TABLE IF NOT EXISTS order_table (
    id SERIAL PRIMARY KEY,
    product_id INTEGER NOT NULL,
    customer_id INTEGER NOT NULL,
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    status_id INTEGER NOT NULL,

    FOREIGN KEY (product_id) REFERENCES product(id),
    FOREIGN KEY (customer_id) REFERENCES customer(id),
    FOREIGN KEY (status_id) REFERENCES order_status(id)
);

COMMENT ON TABLE order_table IS 'Таблица заказов';
COMMENT ON COLUMN order_table.id IS 'Уникальный идентификатор заказа';
COMMENT ON COLUMN order_table.product_id IS 'Ссылка на товар (внешний ключ)';
COMMENT ON COLUMN order_table.customer_id IS 'Ссылка на покупателя (внешний ключ)';
COMMENT ON COLUMN order_table.order_date IS 'Дата и время заказа';
COMMENT ON COLUMN order_table.quantity IS 'Количество товара в заказе';
COMMENT ON COLUMN order_table.status_id IS 'Статус заказа';

-- Создание индексов
DO $$
BEGIN
    -- Индекс для product_id
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'idx_order_product_id') THEN
        CREATE INDEX idx_order_product_id ON order_table(product_id);
    END IF;

    -- Индекс для customer_id
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'idx_order_customer_id') THEN
        CREATE INDEX idx_order_customer_id ON order_table(customer_id);
    END IF;

    -- Индекс для order_date
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'idx_order_date') THEN
        CREATE INDEX idx_order_date ON order_table(order_date);
    END IF;

    -- Индекс для status_id
    IF NOT EXISTS (SELECT 1 FROM pg_indexes WHERE indexname = 'idx_order_status_id') THEN
        CREATE INDEX idx_order_status_id ON order_table(status_id);
    END IF;
END $$;