-- 5 ЗАПРОСОВ НА ЧТЕНИЕ

-- 1. Список всех заказов за последние 7 дней с именем покупателя и описанием товара
SELECT
    o.id as order_id,
    o.order_date,
    c.first_name || ' ' || c.last_name as customer_name,
    p.description as product_name,
    o.quantity,
    os.status_name
FROM order_table o
JOIN customer c ON o.customer_id = c.id
JOIN product p ON o.product_id = p.id
JOIN order_status os ON o.status_id = os.id
WHERE o.order_date >= CURRENT_DATE - INTERVAL '7 days'
ORDER BY o.order_date DESC;

-- 2. Топ-3 самых популярных товаров (по количеству заказов)
SELECT
    p.description as product_name,
    p.category,
    SUM(o.quantity) as total_ordered
FROM order_table o
JOIN product p ON o.product_id = p.id
GROUP BY p.id, p.description, p.category
ORDER BY total_ordered DESC
LIMIT 3;

-- 3. Покупатели с количеством заказов и общей суммой покупок
SELECT
    c.first_name || ' ' || c.last_name as customer_name,
    COUNT(o.id) as order_count,
    SUM(o.quantity * p.price) as total_spent
FROM customer c
LEFT JOIN order_table o ON c.id = o.customer_id
LEFT JOIN product p ON o.product_id = p.id
GROUP BY c.id, c.first_name, c.last_name
ORDER BY total_spent DESC NULLS LAST;

-- 4. Заказы по статусам с агрегацией
SELECT
    os.status_name,
    COUNT(o.id) as order_count,
    SUM(o.quantity * p.price) as total_amount
FROM order_status os
LEFT JOIN order_table o ON os.id = o.status_id
LEFT JOIN product p ON o.product_id = p.id
GROUP BY os.id, os.status_name
ORDER BY order_count DESC;

-- 5. Товары, которые скоро закончатся на складе (меньше 20 штук)
SELECT
    description,
    price,
    quantity,
    category
FROM product
WHERE quantity < 20
ORDER BY quantity ASC;

-- 3 ЗАПРОСА НА ИЗМЕНЕНИЕ

-- 1. Обновление количества на складе при покупке (уменьшаем количество на 1 для товара с id=1)
UPDATE product
SET quantity = quantity - 1
WHERE id = 1 AND quantity >= 1;

-- 2. Обновление статуса заказа на "Доставлен" для заказов старше 5 дней
UPDATE order_table
SET status_id = (SELECT id FROM order_status WHERE status_name = 'Доставлен')
WHERE order_date < CURRENT_DATE - INTERVAL '5 days'
AND status_id != (SELECT id FROM order_status WHERE status_name = 'Доставлен');

-- 3. Увеличение цены на 10% для товаров категории "Конструкторы"
UPDATE product
SET price = price * 1.10
WHERE category = 'Конструкторы';

-- 2 ЗАПРОСА НА УДАЛЕНИЕ

-- 1. Удаление клиентов без заказов
DELETE FROM customer
WHERE id NOT IN (SELECT DISTINCT customer_id FROM order_table);

-- 2. Удаление отмененных заказов старше 30 дней
DELETE FROM order_table
WHERE status_id = (SELECT id FROM order_status WHERE status_name = 'Отменен')
AND order_date < CURRENT_DATE - INTERVAL '30 days';