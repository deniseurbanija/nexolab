import dao.ConexionDB;
import model.Usuario;
import service.AuthService;
import view.MenuAdmin;
import view.MenuLaboratorio;
import view.MenuVeterinario;

import java.sql.SQLException;
import java.util.Scanner;

/**
 * Punto de entrada de la aplicación NexoLab.
 *
 * Compilar (desde la carpeta nexolab/):
 *   javac -cp lib/mysql-connector-j-8.x.x.jar -sourcepath src -d out \
 *         src/Main.java src/model/*.java src/dao/*.java src/service/*.java src/view/*.java
 *
 * Ejecutar:
 *   java -cp out:lib/mysql-connector-j-8.x.x.jar Main
 */
public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        AuthService authService = new AuthService();

        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║         NEXOLAB v1.0 (MVP)            ║");
        System.out.println("║  Sistema de Análisis Clínicos Vet.    ║");
        System.out.println("╚══════════════════════════════════════╝");

        boolean ejecutando = true;
        while (ejecutando) {
            System.out.println("\n── Iniciar Sesión ──────────────────────");
            System.out.print("Usuario: ");
            String username = sc.nextLine().trim();

            if (username.equalsIgnoreCase("salir")) {
                ejecutando = false;
                break;
            }

            System.out.print("Contraseña: ");
            String password = sc.nextLine().trim();

            try {
                Usuario usuario = authService.login(username, password);

                if (usuario == null) {
                    System.out.println("[!] Credenciales incorrectas. Intente de nuevo.");
                    continue;
                }

                System.out.println("\n[OK] Bienvenido/a, " + usuario.getNombreCompleto() +
                        " [" + usuario.getRol() + "]");

                switch (usuario.getRol()) {
                    case VETERINARIO -> new MenuVeterinario(usuario, sc).mostrar();
                    case LABORATORIO -> new MenuLaboratorio(usuario, sc).mostrar();
                    case ADMIN       -> new MenuAdmin(sc).mostrar();
                }

            } catch (SQLException e) {
                System.err.println("[ERROR DE CONEXIÓN] " + e.getMessage());
                System.err.println("Verifique que MySQL esté ejecutándose y que la BD 'nexolab' exista.");
                ejecutando = false;
            }
        }

        ConexionDB.cerrarConexion();
        System.out.println("\nNexoLab cerrado. ¡Hasta pronto!");
        sc.close();
    }
}
