package com.kidsshop;

//импорт необходимых классов
import org.flywaydb.core.Flyway; // Импорт класса Flyway из библиотеки Flyway
import java.io.IOException; // Импорт класса для обработки исключений ввода-вывода
import java.io.InputStream; // Импорт класса для чтения данных из файлов и ресурсов
import java.util.Properties; // Импорт класса для работы с properties-файлами (файлами настроек)

public class Migration { // Класс для управления миграциями базы данных с помощью Flyway
    public static void runMigrations() throws IOException { // Статический метод для запуска миграций БД
        Properties props = new Properties(); // Создаем объект Properties для чтения настроек из файла
        // try-with-resources для автоматического закрытия InputStream
        try (InputStream input = Migration.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) { // Проверяем что файл с настройками найден в classpath
                throw new IOException("Unable to find application.properties"); // Если файл не найден, выбрасываем исключение
            }
            props.load(input); // Загружаем свойства из файла в объект Properties
        }
        // Извлекаем настройки для Flyway из properties-файла
        String url = props.getProperty("flyway.url"); // URL базы данных
        String user = props.getProperty("flyway.user"); // Имя пользователя БД
        String password = props.getProperty("flyway.password"); // Пароль пользователя БД

        // Создаем и настраиваем объект Flyway используя Fluent API
        Flyway flyway = Flyway.configure()
                .dataSource(url, user, password) // Указываем источник данных (БД)
                .locations("classpath:db/migration") // Указываем папку с миграциями (в classpath)
                .baselineOnMigrate(true) // Включаем базовую линию при миграции (создает flyway_schema_history если нет)
                .validateMigrationNaming(true) // Включаем проверку имен файлов миграций
                .load(); // Завершаем настройку и создаем объект Flyway

        System.out.println("Восстановление истории миграций..."); // Выводим сообщение в консоль
        flyway.repair(); // Восстанавливаем историю миграций (исправляет проблемы с checksum и т.д.)

        System.out.println("Запуск миграций..."); // Выводим сообщение в консоль
        flyway.migrate(); // Запускаем выполнение миграций
        System.out.println("Миграции успешно выполнены");
    }
}