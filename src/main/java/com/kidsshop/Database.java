package com.kidsshop;

//импорт необходимых классов
import java.io.IOException; // Для обработки ошибок ввода-вывода
import java.io.InputStream; // Для чтения данных из файлов
import java.sql.*; // Все классы для работы с JDBC
import java.util.Properties; // Для работы с properties-файлами

// Класс для управления подключением к базе данных
public class Database {
    private static Connection connection; // Статическая переменная для хранения единственного подключения

    // Метод для получения подключения к БД
    public static Connection getConnection() throws SQLException, IOException {
        if (connection == null || connection.isClosed()) { // Проверяем: если подключение не создано или закрыто - создаем новое
            Properties props = new Properties(); // Создаем объект Properties для чтения настроек из файла
            // try-with-resources автоматически закрывает InputStream после использования
            try (InputStream input = Database.class.getClassLoader().getResourceAsStream("application.properties")) {
                if (input == null) { // Проверяем что файл найден
                    throw new IOException("Sorry, unable to find application.properties");
                }
                props.load(input); // Загружаем свойства из файла в объект Properties
            } // InputStream автоматически закрываем здесь

            // Извлекаем настройки подключения из properties
            String url = props.getProperty("db.url"); // URL базы данных
            String user = props.getProperty("db.username"); // Имя пользователя БД
            String password = props.getProperty("db.password"); // Пароль пользователя БД

            connection = DriverManager.getConnection(url, user, password); // Создаем подключение к базе данных используя DriverManager
        }
        return connection; // Возвращаем существующее или новое подключение
    }
    // Метод для закрытия подключения к БД
    public static void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) { // Проверяем что подключение существует и не закрыто
            connection.close(); // Закрываем подключение
        }
    }
}