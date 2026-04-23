package view;

import model.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Ventana principal del Administrador.
 * Por ahora es un placeholder — se reemplazará con el panel real.
 */
public class PanelAdmin extends JFrame {

    public PanelAdmin(Usuario usuario) {
        setTitle("NexoLab - Administrador");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initUI(usuario);
        setVisible(true);
    }

    private void initUI(Usuario usuario) {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(15, 15, 25));

        // Barra superior
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(20, 20, 35));
        topBar.setBorder(new EmptyBorder(14, 20, 14, 20));

        JLabel lblTitulo = new JLabel("NEXOLAB  ·  Panel Administrador");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 15));
        lblTitulo.setForeground(new Color(96, 165, 250));

        JLabel lblUsuario = new JLabel(usuario.getNombreCompleto());
        lblUsuario.setFont(new Font("Arial", Font.PLAIN, 13));
        lblUsuario.setForeground(new Color(150, 150, 180));

        JButton btnLogout = new JButton("Cerrar sesión");
        btnLogout.setBackground(new Color(50, 50, 70));
        btnLogout.setForeground(new Color(200, 200, 220));
        btnLogout.setFont(new Font("Arial", Font.PLAIN, 12));
        btnLogout.setBorderPainted(false);
        btnLogout.setFocusPainted(false);
        btnLogout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLogout.addActionListener(e -> {
            dispose();
            new LoginWindow();
        });

        JPanel rightBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightBar.setBackground(new Color(20, 20, 35));
        rightBar.add(lblUsuario);
        rightBar.add(btnLogout);

        topBar.add(lblTitulo, BorderLayout.WEST);
        topBar.add(rightBar, BorderLayout.EAST);

        // Centro placeholder
        JLabel lblPending = new JLabel("Panel en construcción...");
        lblPending.setForeground(new Color(120, 120, 150));
        lblPending.setFont(new Font("Arial", Font.PLAIN, 16));
        lblPending.setHorizontalAlignment(SwingConstants.CENTER);

        root.add(topBar, BorderLayout.NORTH);
        root.add(lblPending, BorderLayout.CENTER);
        add(root);
    }
}
