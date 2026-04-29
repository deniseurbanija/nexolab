import view.LoginWindow;

import javax.swing.*;
import com.formdev.flatlaf.FlatLightLaf;

/**
 * Punto de entrada de la aplicación NexoLab.
 *
 * Compilar (desde la carpeta nexolab/):
 *   javac -cp lib/mysql-connector-j-8.x.x.jar -sourcepath src -d out \
 *         src/Main.java src/model/*.java src/dao/*.java src/service/*.java src/view/*.java
 *
 * Ejecutar:
 *   java -cp "out;lib/mysql-connector-j-8.x.x.jar" Main
 */
public class Main {

    public static void main(String[] args) {
        FlatLightLaf.setup();
        // invokeLater garantiza que la UI se cree en el hilo correcto de Swing (EDT)
        SwingUtilities.invokeLater(LoginWindow::new);
    }
}
