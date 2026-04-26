package view;

import model.Rol;
import model.Usuario;
import service.AuthService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;

public class LoginWindow extends JFrame {

    private final JTextField txtUsername = new JTextField();
    private final JPasswordField txtPassword = new JPasswordField();
    private final JLabel lblError = new JLabel(" ");
    private final JButton btnLogin = new JButton("Ingresar");

    private final AuthService authService = new AuthService();

    public LoginWindow() {
        setTitle("NexoLab - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(520, 520);
        setLocationRelativeTo(null);
        setResizable(false);

        initUI();
        setVisible(true);
    }

    private void initUI() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(new Color(15, 15, 25));
        root.setBorder(new EmptyBorder(18, 18, 18, 18));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel title = new JLabel("NEXOLAB");
        title.setFont(new Font("Inter", Font.BOLD, 22));
        title.setForeground(new Color(96, 165, 250));
        JLabel subtitle = new JLabel("Acceso al sistema");
        subtitle.setFont(new Font("Inter", Font.PLAIN, 13));
        subtitle.setForeground(new Color(150, 150, 180));
        header.add(title, BorderLayout.NORTH);
        header.add(subtitle, BorderLayout.SOUTH);

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(8, 0, 8, 0);
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1;

        JLabel lblUser = new JLabel("Usuario");
        lblUser.setForeground(new Color(200, 200, 220));
        lblUser.setFont(new Font("Inter", Font.PLAIN, 12));

        JLabel lblPass = new JLabel("Contraseña");
        lblPass.setForeground(new Color(200, 200, 220));
        lblPass.setFont(new Font("Inter", Font.PLAIN, 12));

        styleField(txtUsername);
        styleField(txtPassword);

        lblError.setForeground(new Color(248, 113, 113));
        lblError.setFont(new Font("Inter", Font.PLAIN, 12));

        styleButton(btnLogin);
        btnLogin.addActionListener(e -> doLogin());

        // Agrega listener para el enter
        KeyAdapter enterToLogin = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) doLogin();
            }
        };
        txtUsername.addKeyListener(enterToLogin);
        txtPassword.addKeyListener(enterToLogin);

        gc.gridx = 0; gc.gridy = 0;
        form.add(lblUser, gc);
        gc.gridy = 1;
        form.add(txtUsername, gc);
        gc.gridy = 2;
        form.add(lblPass, gc);
        gc.gridy = 3;
        form.add(txtPassword, gc);
        gc.gridy = 4;
        form.add(lblError, gc);
        gc.gridy = 5;
        form.add(btnLogin, gc);

        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.setBorder(new EmptyBorder(18, 0, 0, 0));
        center.add(form, BorderLayout.NORTH);

        root.add(header, BorderLayout.NORTH);
        root.add(center, BorderLayout.CENTER);
        setContentPane(root);

        SwingUtilities.invokeLater(() -> txtUsername.requestFocusInWindow());
    }

    private void doLogin() {
        setError(" ");

        String username = txtUsername.getText() == null ? "" : txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());
        if (username.isEmpty() || password.isEmpty()) {
            setError("Ingresá usuario y contraseña.");
            return;
        }

        setEnabledState(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        SwingWorker<Usuario, Void> worker = new SwingWorker<>() {
            @Override
            protected Usuario doInBackground() throws SQLException {
                return authService.login(username, password);
            }

            @Override
            protected void done() {
                setCursor(Cursor.getDefaultCursor());
                setEnabledState(true);
                txtPassword.setText("");

                try {
                    Usuario usuario = get();
                    if (usuario == null) {
                        setError("Credenciales inválidas.");
                        return;
                    }
                    abrirPanelPorRol(usuario);
                } catch (Exception ex) {
                    setError("Error al iniciar sesión. Revisá la conexión a DB.");
                }
            }
        };
        worker.execute();
    }

    private void abrirPanelPorRol(Usuario usuario) {
        dispose();
        Rol rol = usuario.getRol();
        if (rol == Rol.ADMIN) {
            new PanelAdmin(usuario);
        } else if (rol == Rol.VETERINARIO) {
            new PanelVeterinario(usuario);
        } else if (rol == Rol.LABORATORIO) {
            new PanelLaboratorio(usuario);
        } else {
            JOptionPane.showMessageDialog(
                    null,
                    "Rol no soportado: " + rol,
                    "NexoLab",
                    JOptionPane.ERROR_MESSAGE
            );
            new LoginWindow();
        }
    }

    private void setError(String msg) {
        lblError.setText(msg);
    }

    private void setEnabledState(boolean enabled) {
        txtUsername.setEnabled(enabled);
        txtPassword.setEnabled(enabled);
        btnLogin.setEnabled(enabled);
    }

    private void styleField(JTextField field) {
        field.setBackground(new Color(22, 22, 35));
        field.setForeground(new Color(230, 230, 240));
        field.setCaretColor(new Color(230, 230, 240));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(55, 55, 75), 1),
                new EmptyBorder(10, 12, 10, 12)
        ));
        field.setFont(new Font("Inter", Font.PLAIN, 13));
    }

    private void styleButton(JButton b) {
        b.setBackground(new Color(59, 130, 246));
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Inter", Font.BOLD, 13));
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setPreferredSize(new Dimension(0, 38));
    }
}
