package biblioteca.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PostgreSQLManager implements DBManager {
    private static PostgreSQLManager instance; // Instância única
    private String url = "jdbc:postgresql://localhost:5432/biblioteca";
    private String username = "postgres";
    private String password = "123";

    // Construtor privado
    private PostgreSQLManager() {}

    public static PostgreSQLManager getInstance() {
        if (instance == null) {
            synchronized (PostgreSQLManager.class) {
                if (instance == null) {
                    instance = new PostgreSQLManager();
                }
            }
        }
        return instance;
    }

    @Override
    public Connection connect() {
        try {
            return DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Falha ao conectar ao banco de dados.");
        }
    }
}
