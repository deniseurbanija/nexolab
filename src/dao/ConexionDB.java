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

    // Forzamos UTF-8 end-to-end para evitar "??" en acentos/ñ.
    private static final String URL      =
            "jdbc:mysql://localhost:3306/nexolab" +
            "?useSSL=false" +
            "&serverTimezone=America/Argentina/Buenos_Aires" +
            "&useUnicode=true" +
            "&characterEncoding=UTF-8" +
            "&characterSetResults=UTF-8";
    private static final String USUARIO  = "root";
    // PowerShell: $env:DB_PASSWORD="tu_clave"  (antes de ejecutar la app)

    
    private static Connection instancia;

    private ConexionDB() {}

    public static Connection getConexion() throws SQLException {
        if (instancia == null || instancia.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                throw new SQLException("Driver MySQL no encontrado. Verificar mysql-connector-java en el classpath.", e);
            }

            String password = obtenerPasswordDb();
            if (password == null || password.isBlank()) {
                throw new SQLException(
                        "DB_PASSWORD no está configurada. Definila antes de ejecutar (PowerShell: $env:DB_PASSWORD=\"...\"), " +
                        "o pasá -Ddb.password=\"...\" al iniciar la JVM."
                );
            }

            instancia = DriverManager.getConnection(URL, USUARIO, password);
            System.out.println("[DB] Conexión establecida con NexoLab.");
        }
        return instancia;
    }

    private static String obtenerPasswordDb() {
        String env = System.getenv("DB_PASSWORD");
        if (env != null && !env.isBlank()) return env;
        // Alternativa para ejecución desde IDE/atajos: -Ddb.password=...
        String prop = System.getProperty("db.password");
        if (prop != null && !prop.isBlank()) return prop;
        return env; // puede ser null/blank
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
