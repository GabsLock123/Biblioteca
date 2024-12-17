package biblioteca.util;

import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionFactory {
    private static ConnectionFactory instance; // Instância única
    private static DBManager dbManager;

    // Construtor privado para impedir criação de instâncias
    private ConnectionFactory() {}

    public static ConnectionFactory getInstance() {
        if (instance == null) {
            synchronized (ConnectionFactory.class) {
                if (instance == null) {
                    instance = new ConnectionFactory();
                }
            }
        }
        return instance;
    }

    public void setDBManager(DBManager manager) {
        dbManager = manager;
    }

    public Connection getConnection() throws SQLException {
        if (dbManager == null) {
            throw new IllegalStateException("DBManager não configurado.");
        }
        return dbManager.connect();
    }
}
