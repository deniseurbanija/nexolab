package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton que provee una única conexión JDBC a MySQL.
 * En el MVP usamos una conexión compartida; en producción
 * se reemplazaría por un connection pool (HikariCP, etc.).
 */
public class ConexionDB {

    private static final String URL      = "jdbc:mysql://localhost:3306/nexolab?useSSL=false&serverTimezone=America/Argentina/Buenos_Aires";
    private static final String USUARIO  = "root";
    private static final String PASSWORD = "";  // Ajustar según configuración local

    private static Connection instancia;

    private ConexionDB() {}

    public static Connection getConexion() throws SQLException {
        if (instancia == null || instancia.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                throw new SQLException("Driver MySQL no encontrado. Verificar mysql-connector-java en el classpath.", e);
            }
            instancia = DriverManager.getConnection(URL, USUARIO, PASSWORD);
            System.out.println("[DB] Conexión establecida con NexoLab.");
        }
        return instancia;
    }

    public static void cerrarConexion() {
        if (instancia != null) {
            try {
                instancia.close();
                System.out.println("[DB] Conexión cerrada.");
            } catch (SQLException e) {
                System.err.println("[DB] Error al cerrar conexión: " + e.getMessage());
            }
        }
    }
}
