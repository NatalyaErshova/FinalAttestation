-- Заполнение таблицы статусов заказов
INSERT INTO order_status (status_name)
SELECT 'Новый' WHERE NOT EXISTS (SELECT 1 FROM order_status WHERE status_name = 'Новый')
UNION ALL
SELECT 'Подтвержден' WHERE NOT EXISTS (SELECT 1 FROM order_status WHERE status_name = 'Подтвержден')
UNION ALL
SELECT 'В обработке' WHERE NOT EXISTS (SELECT 1 FROM order_status WHERE status_name = 'В обработке')
UNION ALL
SELECT 'Отправлен' WHERE NOT EXISTS (SELECT 1 FROM order_status WHERE status_name = 'Отправлен')
UNION ALL
SELECT 'Доставлен' WHERE NOT EXISTS (SELECT 1 FROM order_status WHERE status_name = 'Доставлен')
UNION ALL
SELECT 'Отменен' WHERE NOT EXISTS (SELECT 1 FROM order_status WHERE status_name = 'Отменен');

-- Заполнение таблицы товаров
INSERT INTO product (description, price, quantity, category)
SELECT 'Конструктор LEGO Classic', 1999.99, 50, 'Конструкторы'
WHERE NOT EXISTS (SELECT 1 FROM product WHERE description = 'Конструктор LEGO Classic')
UNION ALL
SELECT 'Кукла Barbie', 1599.50, 30, 'Куклы'
WHERE NOT EXISTS (SELECT 1 FROM product WHERE description = 'Кукла Barbie')
UNION ALL
SELECT 'Машинка Hot Wheels', 499.99, 100, 'Машинки'
WHERE NOT EXISTS (SELECT 1 FROM product WHERE description = 'Машинка Hot Wheels')
UNION ALL
SELECT 'Плюшевый мишка', 899.00, 25, 'Мягкие игрушки'
WHERE NOT EXISTS (SELECT 1 FROM product WHERE description = 'Плюшевый мишка')
UNION ALL
SELECT 'Настольная игра "Монополия"', 2499.00, 15, 'Настольные игры'
WHERE NOT EXISTS (SELECT 1 FROM product WHERE description = 'Настольная игра "Монополия"')
UNION ALL
SELECT 'Пазл 1000 элементов', 699.99, 40, 'Пазлы'
WHERE NOT EXISTS (SELECT 1 FROM product WHERE description = 'Пазл 1000 элементов')
UNION ALL
SELECT 'Набор для рисования', 1299.00, 20, 'Творчество'
WHERE NOT EXISTS (SELECT 1 FROM product WHERE description = 'Набор для рисования')
UNION ALL
SELECT 'Робот-трансформер', 1799.50, 35, 'Роботы'
WHERE NOT EXISTS (SELECT 1 FROM product WHERE description = 'Робот-трансформер')
UNION ALL
SELECT 'Кубик Рубика', 299.99, 60, 'Головоломки'
WHERE NOT EXISTS (SELECT 1 FROM product WHERE description = 'Кубик Рубика')
UNION ALL
SELECT 'Детский планшет', 5999.00, 10, 'Электроника'
WHERE NOT EXISTS (SELECT 1 FROM product WHERE description = 'Детский планшет');

-- Заполнение таблицы покупателей
INSERT INTO customer (first_name, last_name, phone, email)
SELECT 'Иван', 'Петров', '+79161234567', 'ivan.petrov@mail.ru'
WHERE NOT EXISTS (SELECT 1 FROM customer WHERE email = 'ivan.petrov@mail.ru')
UNION ALL
SELECT 'Мария', 'Сидорова', '+79167654321', 'maria.sidorova@mail.ru'
WHERE NOT EXISTS (SELECT 1 FROM customer WHERE email = 'maria.sidorova@mail.ru')
UNION ALL
SELECT 'Алексей', 'Козлов', '+79031112233', 'alex.kozlov@mail.ru'
WHERE NOT EXISTS (SELECT 1 FROM customer WHERE email = 'alex.kozlov@mail.ru')
UNION ALL
SELECT 'Елена', 'Николаева', '+79265554433', 'elena.nikolaeva@mail.ru'
WHERE NOT EXISTS (SELECT 1 FROM customer WHERE email = 'elena.nikolaeva@mail.ru')
UNION ALL
SELECT 'Дмитрий', 'Васильев', '+79189998877', 'dmitry.vasiliev@mail.ru'
WHERE NOT EXISTS (SELECT 1 FROM customer WHERE email = 'dmitry.vasiliev@mail.ru')
UNION ALL
SELECT 'Ольга', 'Смирнова', '+79056667788', 'olga.smirnova@mail.ru'
WHERE NOT EXISTS (SELECT 1 FROM customer WHERE email = 'olga.smirnova@mail.ru')
UNION ALL
SELECT 'Сергей', 'Попов', '+79134445566', 'sergey.popov@mail.ru'
WHERE NOT EXISTS (SELECT 1 FROM customer WHERE email = 'sergey.popov@mail.ru')
UNION ALL
SELECT 'Анна', 'Федорова', '+79213332211', 'anna.fedorova@mail.ru'
WHERE NOT EXISTS (SELECT 1 FROM customer WHERE email = 'anna.fedorova@mail.ru')
UNION ALL
SELECT 'Павел', 'Морозов', '+79048889900', 'pavel.morozov@mail.ru'
WHERE NOT EXISTS (SELECT 1 FROM customer WHERE email = 'pavel.morozov@mail.ru')
UNION ALL
SELECT 'Юлия', 'Волкова', '+79151110022', 'julia.volkova@mail.ru'
WHERE NOT EXISTS (SELECT 1 FROM customer WHERE email = 'julia.volkova@mail.ru');

-- Заполнение таблицы заказов
INSERT INTO order_table (product_id, customer_id, order_date, quantity, status_id)
SELECT 1, 1, CURRENT_TIMESTAMP - INTERVAL '5 days', 2,
       (SELECT id FROM order_status WHERE status_name = 'Отправлен' LIMIT 1)
WHERE EXISTS (SELECT 1 FROM product WHERE id = 1)
  AND EXISTS (SELECT 1 FROM customer WHERE id = 1)
  AND NOT EXISTS (SELECT 1 FROM order_table WHERE product_id = 1 AND customer_id = 1 AND quantity = 2)
UNION ALL
SELECT 3, 2, CURRENT_TIMESTAMP - INTERVAL '3 days', 5,
       (SELECT id FROM order_status WHERE status_name = 'В обработке' LIMIT 1)
WHERE EXISTS (SELECT 1 FROM product WHERE id = 3)
  AND EXISTS (SELECT 1 FROM customer WHERE id = 2)
  AND NOT EXISTS (SELECT 1 FROM order_table WHERE product_id = 3 AND customer_id = 2 AND quantity = 5)
UNION ALL
SELECT 5, 3, CURRENT_TIMESTAMP - INTERVAL '1 day', 1,
       (SELECT id FROM order_status WHERE status_name = 'Подтвержден' LIMIT 1)
WHERE EXISTS (SELECT 1 FROM product WHERE id = 5)
  AND EXISTS (SELECT 1 FROM customer WHERE id = 3)
  AND NOT EXISTS (SELECT 1 FROM order_table WHERE product_id = 5 AND customer_id = 3 AND quantity = 1)
UNION ALL
SELECT 2, 4, CURRENT_TIMESTAMP - INTERVAL '6 days', 3,
       (SELECT id FROM order_status WHERE status_name = 'Доставлен' LIMIT 1)
WHERE EXISTS (SELECT 1 FROM product WHERE id = 2)
  AND EXISTS (SELECT 1 FROM customer WHERE id = 4)
  AND NOT EXISTS (SELECT 1 FROM order_table WHERE product_id = 2 AND customer_id = 4 AND quantity = 3)
UNION ALL
SELECT 4, 5, CURRENT_TIMESTAMP - INTERVAL '2 days', 1,
       (SELECT id FROM order_status WHERE status_name = 'Отправлен' LIMIT 1)
WHERE EXISTS (SELECT 1 FROM product WHERE id = 4)
  AND EXISTS (SELECT 1 FROM customer WHERE id = 5)
  AND NOT EXISTS (SELECT 1 FROM order_table WHERE product_id = 4 AND customer_id = 5 AND quantity = 1)
UNION ALL
SELECT 6, 6, CURRENT_TIMESTAMP - INTERVAL '4 days', 2,
       (SELECT id FROM order_status WHERE status_name = 'В обработке' LIMIT 1)
WHERE EXISTS (SELECT 1 FROM product WHERE id = 6)
  AND EXISTS (SELECT 1 FROM customer WHERE id = 6)
  AND NOT EXISTS (SELECT 1 FROM order_table WHERE product_id = 6 AND customer_id = 6 AND quantity = 2)
UNION ALL
SELECT 7, 7, CURRENT_TIMESTAMP - INTERVAL '7 days', 1,
       (SELECT id FROM order_status WHERE status_name = 'Доставлен' LIMIT 1)
WHERE EXISTS (SELECT 1 FROM product WHERE id = 7)
  AND EXISTS (SELECT 1 FROM customer WHERE id = 7)
  AND NOT EXISTS (SELECT 1 FROM order_table WHERE product_id = 7 AND customer_id = 7 AND quantity = 1)
UNION ALL
SELECT 8, 8, CURRENT_TIMESTAMP - INTERVAL '1 day', 4,
       (SELECT id FROM order_status WHERE status_name = 'Подтвержден' LIMIT 1)
WHERE EXISTS (SELECT 1 FROM product WHERE id = 8)
  AND EXISTS (SELECT 1 FROM customer WHERE id = 8)
  AND NOT EXISTS (SELECT 1 FROM order_table WHERE product_id = 8 AND customer_id = 8 AND quantity = 4)
UNION ALL
SELECT 9, 9, CURRENT_TIMESTAMP - INTERVAL '3 days', 10,
       (SELECT id FROM order_status WHERE status_name = 'Отправлен' LIMIT 1)
WHERE EXISTS (SELECT 1 FROM product WHERE id = 9)
  AND EXISTS (SELECT 1 FROM customer WHERE id = 9)
  AND NOT EXISTS (SELECT 1 FROM order_table WHERE product_id = 9 AND customer_id = 9 AND quantity = 10)
UNION ALL
SELECT 10, 10, CURRENT_TIMESTAMP - INTERVAL '2 days', 1,
       (SELECT id FROM order_status WHERE status_name = 'Новый' LIMIT 1)
WHERE EXISTS (SELECT 1 FROM product WHERE id = 10)
  AND EXISTS (SELECT 1 FROM customer WHERE id = 10)
  AND NOT EXISTS (SELECT 1 FROM order_table WHERE product_id = 10 AND customer_id = 10 AND quantity = 1)
UNION ALL
SELECT 1, 2, CURRENT_TIMESTAMP, 1,
       (SELECT id FROM order_status WHERE status_name = 'Новый' LIMIT 1)
WHERE EXISTS (SELECT 1 FROM product WHERE id = 1)
  AND EXISTS (SELECT 1 FROM customer WHERE id = 2)
  AND NOT EXISTS (SELECT 1 FROM order_table WHERE product_id = 1 AND customer_id = 2 AND quantity = 1)
UNION ALL
SELECT 3, 3, CURRENT_TIMESTAMP, 3,
       (SELECT id FROM order_status WHERE status_name = 'Новый' LIMIT 1)
WHERE EXISTS (SELECT 1 FROM product WHERE id = 3)
  AND EXISTS (SELECT 1 FROM customer WHERE id = 3)
  AND NOT EXISTS (SELECT 1 FROM order_table WHERE product_id = 3 AND customer_id = 3 AND quantity = 3);