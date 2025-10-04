package com.kidsshop;

// Импорт классов для работы с базой данных через JDBC
import java.sql.*;

// Главный класс приложения
public class App {
    public static void main(String[] args) { // точка входа в приложение
        Connection connection = null; // Объявляем переменную для подключения к БД (изначально null)

        // Блок try-catch-finally для обработки исключений
        try {
            Migration.runMigrations(); // Запуск миграций базы данных (создание таблиц и заполнение тестовыми данными)

            // Получение соединения
            connection = Database.getConnection(); // Получаем подключение к базе данных через класс Database
            connection.setAutoCommit(false); // Включаем управление транзакциями

            System.out.println("Приложение запущено успешно!"); // Вывод сообщения о успешном запуске
            System.out.println("=================================");

            // Вызов метода демонстрации CRUD операций
            demoCRUDOperations(connection);

            // Если все операции прошли без ошибок - подтверждаем транзакцию (коммитим)
            connection.commit();
            System.out.println("Все операции выполнены успешно!");

        } catch (Exception e) { // Обработка любых исключений
            System.err.println("Ошибка: " + e.getMessage());
            e.printStackTrace(); // Вывод стека вызовов для отладки

            // Откатываем транзакцию при ошибке
            if (connection != null) {
                try {
                    connection.rollback();
                    System.out.println("Транзакция откатана из-за ошибки");
                } catch (SQLException ex) {
                    System.err.println("Ошибка при откате транзакции: " + ex.getMessage());
                }
            }
        } finally {
            // Закрываем соединение с БД
            if (connection != null) {
                try {
                    Database.closeConnection();
                    System.out.println("🔌 Соединение с БД закрыто");
                } catch (SQLException e) {
                    System.err.println("Ошибка при закрытии соединения: " + e.getMessage());
                }
            }
        }
    }

    // Метод демонстрации CRUD операций (Create, Read, Update, Delete)
    private static void demoCRUDOperations(Connection connection) throws SQLException {
        // 1. Вставка нового товара и покупателя (CREATE)
        insertNewProductAndCustomer(connection);

        // 2. Создание заказа для покупателя (CREATE)
        createOrderForCustomer(connection);

        // 3. Чтение и вывод последних 5 заказов (READ)
        readLast5Orders(connection);

        // 4. Обновление цены товара и количества на складе (UPDATE)
        updateProductPriceAndQuantity(connection);

        // 5. Удаление тестовых записей (DELETE)
        deleteTestRecords(connection);
    }

    // Метод для вставки нового товара и покупателя
    private static void insertNewProductAndCustomer(Connection connection) throws SQLException {
        System.out.println("\n1. ВСТАВКА НОВОГО ТОВАРА И ПОКУПАТЕЛЯ");

        // Вставка нового товара
        String insertProductSQL = "INSERT INTO product (description, price, quantity, category) VALUES (?, ?, ?, ?)";
        long newProductId = -1;

        // try-with-resources для автоматического закрытия PreparedStatement
        try (PreparedStatement pstmt = connection.prepareStatement(insertProductSQL, Statement.RETURN_GENERATED_KEYS)) {
            // Устанавливаем параметры в SQL запрос
            pstmt.setString(1, "Детский велосипед");
            pstmt.setDouble(2, 4599.99);
            pstmt.setInt(3, 15);
            pstmt.setString(4, "Спорт");
            pstmt.executeUpdate(); // Выполняем INSERT запрос

            // Получаем сгенерированный ID новой записи
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    newProductId = generatedKeys.getLong(1);
                    System.out.println("Новый товар добавлен с ID: " + newProductId);
                }
            }
        }

        // Вставка нового покупателя (аналогично товару)
        String insertCustomerSQL = "INSERT INTO customer (first_name, last_name, phone, email) VALUES (?, ?, ?, ?)";
        long newCustomerId = -1;

        try (PreparedStatement pstmt = connection.prepareStatement(insertCustomerSQL, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, "Тест");
            pstmt.setString(2, "Пользователь");
            pstmt.setString(3, "+79160000000");
            pstmt.setString(4, "test.user@mail.ru");
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    newCustomerId = generatedKeys.getLong(1);
                    System.out.println("Новый покупатель добавлен с ID: " + newCustomerId);
                }
            }
        }

        System.out.println("\nДОБАВЛЕННЫЕ ЗАПИСИ:"); // Вывод добавленных записей

        // Вывод добавленного товара в виде таблицы
        if (newProductId != -1) {
            String selectProductSQL = "SELECT * FROM product WHERE id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(selectProductSQL)) {
                pstmt.setLong(1, newProductId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        System.out.println("┌────────────────────────────────────────────────────────┐");
                        System.out.println("│                  ДОБАВЛЕННЫЙ ТОВАР                    │");
                        System.out.println("├────────────────────────────────────────────────────────┤");
                        System.out.printf("│ ID: %-45d │\n", rs.getInt("id"));
                        System.out.printf("│ Описание: %-36s │\n",
                                rs.getString("description").length() > 36 ?
                                        rs.getString("description").substring(0, 33) + "..." :
                                        rs.getString("description"));
                        System.out.printf("│ Цена: %-42.2f │\n", rs.getDouble("price"));
                        System.out.printf("│ Количество: %-35d │\n", rs.getInt("quantity"));
                        System.out.printf("│ Категория: %-36s │\n", rs.getString("category"));
                        System.out.println("└────────────────────────────────────────────────────────┘");
                    }
                }
            }
        }

        // Вывод добавленного покупателя в виде таблицы
        if (newCustomerId != -1) {
            String selectCustomerSQL = "SELECT * FROM customer WHERE id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(selectCustomerSQL)) {
                pstmt.setLong(1, newCustomerId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        System.out.println("┌────────────────────────────────────────────────────────┐");
                        System.out.println("│                 ДОБАВЛЕННЫЙ ПОКУПАТЕЛЬ                │");
                        System.out.println("├────────────────────────────────────────────────────────┤");
                        System.out.printf("│ ID: %-45d │\n", rs.getInt("id"));
                        System.out.printf("│ Имя: %-43s │\n", rs.getString("first_name"));
                        System.out.printf("│ Фамилия: %-38s │\n", rs.getString("last_name"));
                        System.out.printf("│ Телефон: %-38s │\n", rs.getString("phone"));
                        System.out.printf("│ Email: %-41s │\n", rs.getString("email"));
                        System.out.println("└────────────────────────────────────────────────────────┘");
                    }
                }
            }
        }
    }

    // Метод для создания заказа
    private static void createOrderForCustomer(Connection connection) throws SQLException {
        System.out.println("\n2. СОЗДАНИЕ ЗАКАЗА ДЛЯ ПОКУПАТЕЛЯ");

        // Сначала находим реально существующие ID товара, покупателя и статуса
        int existingProductId = findExistingProductId(connection);
        int existingCustomerId = findExistingCustomerId(connection);
        int existingStatusId = findExistingStatusId(connection);

        // Проверяем что все необходимые ID найдены
        if (existingProductId == -1 || existingCustomerId == -1 || existingStatusId == -1) {
            System.out.println("Не удалось найти подходящие товар, покупателя или статус для создания заказа");
            return;
        }

        String insertOrderSQL = "INSERT INTO order_table (product_id, customer_id, quantity, status_id) VALUES (?, ?, ?, ?)";
        long newOrderId = -1;

        try (PreparedStatement pstmt = connection.prepareStatement(insertOrderSQL, Statement.RETURN_GENERATED_KEYS)) {
            // Используем найденные реальные ID
            pstmt.setInt(1, existingProductId); // Используем реальный product_id
            pstmt.setInt(2, existingCustomerId); // Используем реальный customer_id
            pstmt.setInt(3, 2); // quantity
            pstmt.setInt(4, existingStatusId); // Используем реальный status_id

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        newOrderId = generatedKeys.getLong(1);
                    }
                }
                System.out.println("Новый заказ успешно создан с ID: " + newOrderId);
            }
        }

        // Вывод созданного заказа
        if (newOrderId != -1) {
            // SQL запрос с JOIN для получения полной информации о заказе
            String selectOrderSQL = "SELECT o.*, p.description as product_name, " +
                    "c.first_name || ' ' || c.last_name as customer_name, " +
                    "os.status_name " +
                    "FROM order_table o " +
                    "JOIN product p ON o.product_id = p.id " +
                    "JOIN customer c ON o.customer_id = c.id " +
                    "JOIN order_status os ON o.status_id = os.id " +
                    "WHERE o.id = ?";

            try (PreparedStatement pstmt = connection.prepareStatement(selectOrderSQL)) {
                pstmt.setLong(1, newOrderId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        // Выводим информацию о заказе в виде таблицы
                        System.out.println("┌────────────────────────────────────────────────────────┐");
                        System.out.println("│                   СОЗДАННЫЙ ЗАКАЗ                     │");
                        System.out.println("├────────────────────────────────────────────────────────┤");
                        System.out.printf("│ ID заказа: %-37d │\n", rs.getInt("id"));
                        System.out.printf("│ Товар: %-42s │\n",
                                rs.getString("product_name").length() > 42 ?
                                        rs.getString("product_name").substring(0, 39) + "..." :
                                        rs.getString("product_name"));
                        System.out.printf("│ Покупатель: %-37s │\n", rs.getString("customer_name"));
                        System.out.printf("│ Количество: %-36d │\n", rs.getInt("quantity"));
                        System.out.printf("│ Статус: %-41s │\n", rs.getString("status_name"));
                        System.out.printf("│ Дата заказа: %-34s │\n", rs.getTimestamp("order_date"));
                        System.out.println("└────────────────────────────────────────────────────────┘");
                    }
                }
            }
        }
    }

    // Вспомогательный метод для поиска существующего товара
    private static int findExistingProductId(Connection connection) throws SQLException {
        String sql = "SELECT id FROM product ORDER BY id LIMIT 1";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("id");
            }
        }
        return -1; // Возвращаем -1 если товар не найден
    }

    // Вспомогательный метод для поиска существующего покупателя
    private static int findExistingCustomerId(Connection connection) throws SQLException {
        String sql = "SELECT id FROM customer ORDER BY id LIMIT 1";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("id");
            }
        }
        return -1; // Возвращаем -1 если покупатель не найден
    }

    // Вспомогательный метод для поиска существующего статуса
    private static int findExistingStatusId(Connection connection) throws SQLException {
        String sql = "SELECT id FROM order_status ORDER BY id LIMIT 1";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("id");
            }
        }
        return -1; // Возвращаем -1 если статус не найден
    }

    // Метод для чтения и вывода последних 5 заказов
    private static void readLast5Orders(Connection connection) throws SQLException {
        System.out.println("\n3. ПОСЛЕДНИЕ 5 ЗАКАЗОВ");
        // SQL запрос с JOIN трех таблиц
        String selectOrdersSQL = "SELECT " +
                "o.id as order_id, " +
                "o.order_date, " +
                "c.first_name || ' ' || c.last_name as customer_name, " +
                "p.description as product_name, " +
                "o.quantity, " +
                "p.price, " +
                "os.status_name " +
                "FROM order_table o " +
                "JOIN customer c ON o.customer_id = c.id " +
                "JOIN product p ON o.product_id = p.id " +
                "JOIN order_status os ON o.status_id = os.id " +
                "ORDER BY o.order_date DESC " + // Сортировка по дате (новые сначала)
                "LIMIT 5"; // Ограничение 5 записей

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(selectOrdersSQL)) {
            // Вывод заголовка таблицы
            System.out.println("┌─────┬────────────────────┬──────────────────┬──────────────────────┬──────────┬──────────┬─────────────┐");
            System.out.println("│ ID  │ Дата заказа        │ Покупатель       │ Товар                │ Кол-во   │ Цена     │ Статус      │");
            System.out.println("├─────┼────────────────────┼──────────────────┼──────────────────────┼──────────┼──────────┼─────────────┤");
            // Обработка каждой строки результата
            while (rs.next()) {
                int orderId = rs.getInt("order_id");
                Timestamp orderDate = rs.getTimestamp("order_date");
                String customerName = rs.getString("customer_name");
                String productName = rs.getString("product_name");
                int quantity = rs.getInt("quantity");
                double price = rs.getDouble("price");
                String status = rs.getString("status_name");

                // Обрезаем длинные названия
                String shortCustomerName = customerName.length() > 16 ?
                        customerName.substring(0, 13) + "..." : customerName;
                String shortProductName = productName.length() > 20 ?
                        productName.substring(0, 17) + "..." : productName;

                System.out.printf("│ %-3d │ %-18s │ %-16s │ %-20s │ %-8d │ %-8.2f │ %-11s │\n",
                        orderId,
                        orderDate.toString().substring(0, 16),
                        shortCustomerName,
                        shortProductName,
                        quantity, price, status);
            }
            // Закрываем таблицу
            System.out.println("└─────┴────────────────────┴──────────────────┴──────────────────────┴──────────┴──────────┴─────────────┘");
        }
    }

    // Метод для обновления цены и количества товара
    private static void updateProductPriceAndQuantity(Connection connection) throws SQLException {
        System.out.println("\n4. ОБНОВЛЕНИЕ ЦЕНЫ И КОЛИЧЕСТВА ТОВАРА");

        // ВЫВОД СТАРЫХ ЗНАЧЕНИЙ
        System.out.println("\nСОСТОЯНИЕ ДО ОБНОВЛЕНИЯ:");
        showProductState(connection, 1, "Товар ID=1 (до обновления)");
        showProductState(connection, 2, "Товар ID=2 (до обновления)");

        // Обновление цены товара ID=1 (увеличиваем на 5%)
        String updatePriceSQL = "UPDATE product SET price = price * 1.05 WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(updatePriceSQL)) {
            pstmt.setInt(1, 1);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Цена товара ID=1 увеличена на 5%");
            }
        }

        // Обновление количества товара ID=2 (увеличиваем на 10 единиц)
        String updateQuantitySQL = "UPDATE product SET quantity = quantity + 10 WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(updateQuantitySQL)) {
            pstmt.setInt(1, 2);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Количество товара ID=2 увеличено на 10 единиц");
            }
        }

        // ВЫВОД НОВЫХ ЗНАЧЕНИЙ (после обновления)
        System.out.println("\nСОСТОЯНИЕ ПОСЛЕ ОБНОВЛЕНИЯ:");
        showProductState(connection, 1, "Товар ID=1 (после обновления)");
        showProductState(connection, 2, "Товар ID=2 (после обновления)");
    }

    // Вспомогательный метод для вывода состояния товара
    private static void showProductState(Connection connection, int productId, String title) throws SQLException {
        String selectProductSQL = "SELECT * FROM product WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(selectProductSQL)) {
            pstmt.setInt(1, productId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    //Выводим информацию о товаре в виде таблицы
                    System.out.println("┌────────────────────────────────────────────────────────┐");
                    System.out.printf("│ %-54s │\n", title);
                    System.out.println("├────────────────────────────────────────────────────────┤");
                    System.out.printf("│ ID: %-45d │\n", rs.getInt("id"));
                    System.out.printf("│ Описание: %-36s │\n",
                            rs.getString("description").length() > 36 ?
                                    rs.getString("description").substring(0, 33) + "..." :
                                    rs.getString("description"));
                    System.out.printf("│ Цена: %-42.2f │\n", rs.getDouble("price"));
                    System.out.printf("│ Количество: %-35d │\n", rs.getInt("quantity"));
                    System.out.printf("│ Категория: %-36s │\n", rs.getString("category"));
                    System.out.println("└────────────────────────────────────────────────────────┘");
                }
            }
        }
    }

    // Метод для удаления тестовых записей
    private static void deleteTestRecords(Connection connection) throws SQLException {
        System.out.println("\n5. УДАЛЕНИЕ ТЕСТОВЫХ ЗАПИСЕЙ");

        // ВЫВОД ЗАПИСЕЙ ПЕРЕД УДАЛЕНИЕМ
        System.out.println("\nЗАПИСИ ДЛЯ УДАЛЕНИЯ:");

        // Показываем тестового покупателя перед удалением
        String selectTestCustomerSQL = "SELECT * FROM customer WHERE first_name = 'Тест' AND last_name = 'Пользователь'";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(selectTestCustomerSQL)) {

            boolean hasTestCustomer = false;
            while (rs.next()) {
                hasTestCustomer = true;
                System.out.println("┌────────────────────────────────────────────────────────┐");
                System.out.println("│               ПОКУПАТЕЛЬ ДЛЯ УДАЛЕНИЯ                 │");
                System.out.println("├────────────────────────────────────────────────────────┤");
                System.out.printf("│ ID: %-45d │\n", rs.getInt("id"));
                System.out.printf("│ Имя: %-43s │\n", rs.getString("first_name"));
                System.out.printf("│ Фамилия: %-38s │\n", rs.getString("last_name"));
                System.out.printf("│ Телефон: %-38s │\n", rs.getString("phone"));
                System.out.printf("│ Email: %-41s │\n", rs.getString("email"));
                System.out.println("└────────────────────────────────────────────────────────┘");
            }
            if (!hasTestCustomer) {
                System.out.println("Тестовый покупатель не найден для удаления");
            }
        }

        // Показываем тестовый товар перед удалением
        String selectTestProductSQL = "SELECT * FROM product WHERE description = 'Детский велосипед'";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(selectTestProductSQL)) {

            boolean hasTestProduct = false;
            while (rs.next()) {
                hasTestProduct = true;
                System.out.println("┌────────────────────────────────────────────────────────┐");
                System.out.println("│                 ТОВАР ДЛЯ УДАЛЕНИЯ                    │");
                System.out.println("├────────────────────────────────────────────────────────┤");
                System.out.printf("│ ID: %-45d │\n", rs.getInt("id"));
                System.out.printf("│ Описание: %-36s │\n", rs.getString("description"));
                System.out.printf("│ Цена: %-42.2f │\n", rs.getDouble("price"));
                System.out.printf("│ Количество: %-35d │\n", rs.getInt("quantity"));
                System.out.printf("│ Категория: %-36s │\n", rs.getString("category"));
                System.out.println("└────────────────────────────────────────────────────────┘");
            }
            if (!hasTestProduct) {
                System.out.println("Тестовый товар не найден для удаления");
            }
        }

        // УДАЛЕНИЕ
        System.out.println("\n🗑️ ПРОЦЕСС УДАЛЕНИЯ:"); // Удаляем

        // Удаляем тестового пользователя
        String deleteCustomerSQL = "DELETE FROM customer WHERE first_name = 'Тест' AND last_name = 'Пользователь'";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteCustomerSQL)) {
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Тестовый покупатель удален");
            } else {
                System.out.println("Тестовый покупатель не найден для удаления");
            }
        }

        // Удаляем тестовый товар
        String deleteProductSQL = "DELETE FROM product WHERE description = 'Детский велосипед'";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteProductSQL)) {
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Тестовый товар удален");
            } else {
                System.out.println("Тестовый товар не найден для удаления");
            }
        }

        // ПОДТВЕРЖДЕНИЕ УДАЛЕНИЯ
        System.out.println("\nПОДТВЕРЖДЕНИЕ УДАЛЕНИЯ:");

        // Проверяем что покупатель удален
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(selectTestCustomerSQL)) {
            if (!rs.next()) {
                System.out.println("✓ Тестовый покупатель успешно удален из БД");
            }
        }

        // Проверяем что товар удален
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(selectTestProductSQL)) {
            if (!rs.next()) {
                System.out.println("✓ Тестовый товар успешно удален из БД");
            }
        }
    }
}