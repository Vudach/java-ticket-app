import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    // ЗДЕСЬ УКАЖИ СВОЙ ПАРОЛЬ ОТ MYSQL!
    private static final String URL = "jdbc:mysql://localhost:3306/ticket_reservation";
    private static final String USER = "root";
    private static final String PASSWORD = "12345"; // ТВОЙ ПАРОЛЬ ЗДЕСЬ!

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Драйвер не найден!");
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}