package view;

import dao.ClinicaDAO;
import dao.LaboratorioDAO;
import dao.UsuarioDAO;
import model.*;
import service.AuthService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;

public class PanelAdmin extends JFrame {

    private final ClinicaDAO     clinicaDAO     = new ClinicaDAO();
    private final LaboratorioDAO laboratorioDAO = new LaboratorioDAO();
    private final UsuarioDAO     usuarioDAO     = new UsuarioDAO();

    private DefaultTableModel clinicasModel;
    private DefaultTableModel laboratoriosModel;
    private DefaultTableModel usuariosModel;

    public PanelAdmin(Usuario usuario) {
        setTitle("NexoLab - Administrador");
        setSize(900, 620);
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
        btnLogout.addActionListener(e -> { dispose(); new LoginWindow(); });

        JPanel rightBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightBar.setBackground(new Color(20, 20, 35));
        rightBar.add(lblUsuario);
        rightBar.add(btnLogout);

        topBar.add(lblTitulo, BorderLayout.WEST);
        topBar.add(rightBar, BorderLayout.EAST);

        // Pestañas
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Clínicas",      buildClinicasTab());
        tabs.addTab("Laboratorios",  buildLaboratoriosTab());
        tabs.addTab("Usuarios",      buildUsuariosTab());

        root.add(topBar, BorderLayout.NORTH);
        root.add(tabs,   BorderLayout.CENTER);
        add(root);

        SwingUtilities.invokeLater(() -> {
            cargarClinicas();
            cargarLaboratorios();
            cargarUsuarios();
        });
    }

    // ─── Pestaña Clínicas ────────────────────────────────────────────────────

    private JPanel buildClinicasTab() {
        clinicasModel = new DefaultTableModel(
                new Object[]{"ID", "Nombre", "Dirección", "Teléfono", "Email"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = buildTable(clinicasModel);

        JButton btnRegistrar = buildPrimaryButton("Registrar Clínica");
        btnRegistrar.addActionListener(e -> { if (registrarClinicaDialog()) cargarClinicas(); });

        JButton btnRefrescar = buildSecondaryButton("Refrescar");
        btnRefrescar.addActionListener(e -> cargarClinicas());

        return buildTabPanel(table, btnRegistrar, btnRefrescar);
    }

    private void cargarClinicas() {
        clinicasModel.setRowCount(0);
        try {
            for (Clinica c : clinicaDAO.listarActivas())
                clinicasModel.addRow(new Object[]{
                        c.getId(), c.getNombre(), c.getDireccion(), c.getTelefono(), c.getEmail()});
        } catch (SQLException ex) { showError("Error al cargar clínicas", ex); }
    }

    private boolean registrarClinicaDialog() {
        JTextField tfNombre = new JTextField();
        JTextField tfDir    = new JTextField();
        JTextField tfTel    = new JTextField();
        JTextField tfEmail  = new JTextField();

        int ok = JOptionPane.showConfirmDialog(this,
                new Object[]{"Nombre:", tfNombre, "Dirección:", tfDir, "Teléfono:", tfTel, "Email:", tfEmail},
                "Registrar Clínica", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (ok != JOptionPane.OK_OPTION) return false;

        try {
            Clinica c = new Clinica();
            c.setNombre(tfNombre.getText().trim());
            c.setDireccion(tfDir.getText().trim());
            c.setTelefono(tfTel.getText().trim());
            c.setEmail(tfEmail.getText().trim());
            clinicaDAO.insertar(c);
            JOptionPane.showMessageDialog(this, "Clínica registrada con ID: " + c.getId(),
                    "OK", JOptionPane.INFORMATION_MESSAGE);
            return true;
        } catch (SQLException ex) { showError("No se pudo registrar la clínica", ex); return false; }
    }

    // ─── Pestaña Laboratorios ─────────────────────────────────────────────────

    private JPanel buildLaboratoriosTab() {
        laboratoriosModel = new DefaultTableModel(
                new Object[]{"ID", "Nombre", "Dirección", "Teléfono", "Email"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = buildTable(laboratoriosModel);

        JButton btnRegistrar = buildPrimaryButton("Registrar Laboratorio");
        btnRegistrar.addActionListener(e -> { if (registrarLaboratorioDialog()) cargarLaboratorios(); });

        JButton btnRefrescar = buildSecondaryButton("Refrescar");
        btnRefrescar.addActionListener(e -> cargarLaboratorios());

        return buildTabPanel(table, btnRegistrar, btnRefrescar);
    }

    private void cargarLaboratorios() {
        laboratoriosModel.setRowCount(0);
        try {
            for (Laboratorio l : laboratorioDAO.listarActivos())
                laboratoriosModel.addRow(new Object[]{
                        l.getId(), l.getNombre(), l.getDireccion(), l.getTelefono(), l.getEmail()});
        } catch (SQLException ex) { showError("Error al cargar laboratorios", ex); }
    }

    private boolean registrarLaboratorioDialog() {
        JTextField tfNombre = new JTextField();
        JTextField tfDir    = new JTextField();
        JTextField tfTel    = new JTextField();
        JTextField tfEmail  = new JTextField();

        int ok = JOptionPane.showConfirmDialog(this,
                new Object[]{"Nombre:", tfNombre, "Dirección:", tfDir, "Teléfono:", tfTel, "Email:", tfEmail},
                "Registrar Laboratorio", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (ok != JOptionPane.OK_OPTION) return false;

        try {
            Laboratorio l = new Laboratorio();
            l.setNombre(tfNombre.getText().trim());
            l.setDireccion(tfDir.getText().trim());
            l.setTelefono(tfTel.getText().trim());
            l.setEmail(tfEmail.getText().trim());
            laboratorioDAO.insertar(l);
            JOptionPane.showMessageDialog(this, "Laboratorio registrado con ID: " + l.getId(),
                    "OK", JOptionPane.INFORMATION_MESSAGE);
            return true;
        } catch (SQLException ex) { showError("No se pudo registrar el laboratorio", ex); return false; }
    }

    // ─── Pestaña Usuarios ─────────────────────────────────────────────────────

    private JPanel buildUsuariosTab() {
        usuariosModel = new DefaultTableModel(
                new Object[]{"ID", "Username", "Nombre completo", "Rol", "Activo"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = buildTable(usuariosModel);

        JButton btnRegistrar = buildPrimaryButton("Registrar Usuario");
        btnRegistrar.addActionListener(e -> { if (registrarUsuarioDialog()) cargarUsuarios(); });

        JButton btnRefrescar = buildSecondaryButton("Refrescar");
        btnRefrescar.addActionListener(e -> cargarUsuarios());

        return buildTabPanel(table, btnRegistrar, btnRefrescar);
    }

    private void cargarUsuarios() {
        usuariosModel.setRowCount(0);
        try {
            for (Usuario u : usuarioDAO.listarTodos())
                usuariosModel.addRow(new Object[]{
                        u.getId(), u.getUsername(), u.getNombreCompleto(), u.getRol(), u.isActivo()});
        } catch (SQLException ex) { showError("Error al cargar usuarios", ex); }
    }

    private boolean registrarUsuarioDialog() {
        JTextField     tfUsername = new JTextField();
        JPasswordField tfPass     = new JPasswordField();
        JTextField     tfNombre   = new JTextField();
        JComboBox<Rol> cbRol      = new JComboBox<>(Rol.values());
        JTextField     tfIdClinica = new JTextField();
        JTextField     tfIdLab    = new JTextField();

        int ok = JOptionPane.showConfirmDialog(this,
                new Object[]{
                        "Username:", tfUsername,
                        "Contraseña:", tfPass,
                        "Nombre completo:", tfNombre,
                        "Rol:", cbRol,
                        "ID Clínica (solo VETERINARIO):", tfIdClinica,
                        "ID Laboratorio (solo LABORATORIO):", tfIdLab
                },
                "Registrar Usuario", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (ok != JOptionPane.OK_OPTION) return false;

        try {
            Rol     rol    = (Rol) cbRol.getSelectedItem();
            String  cliStr = tfIdClinica.getText().trim();
            String  labStr = tfIdLab.getText().trim();
            Integer idCli  = null, idLab = null;

            if (rol == Rol.VETERINARIO && !cliStr.isEmpty())
                idCli = Integer.parseInt(cliStr);
            else if (rol == Rol.LABORATORIO && !labStr.isEmpty())
                idLab = Integer.parseInt(labStr);

            Usuario u = new Usuario();
            u.setUsername(tfUsername.getText().trim());
            u.setPasswordHash(AuthService.hashSHA256(new String(tfPass.getPassword())));
            u.setNombreCompleto(tfNombre.getText().trim());
            u.setRol(rol);
            u.setIdClinica(idCli);
            u.setIdLaboratorio(idLab);
            usuarioDAO.insertar(u);
            JOptionPane.showMessageDialog(this, "Usuario creado con ID: " + u.getId(),
                    "OK", JOptionPane.INFORMATION_MESSAGE);
            return true;
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "El ID de clínica/laboratorio debe ser un número entero.",
                    "Error de formato", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (SQLException ex) { showError("No se pudo registrar el usuario", ex); return false; }
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private JPanel buildTabPanel(JTable table, JButton... buttons) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(15, 15, 25));
        panel.setBorder(new EmptyBorder(16, 16, 16, 16));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actions.setBackground(new Color(15, 15, 25));
        for (JButton b : buttons) actions.add(b);
        actions.setBorder(new EmptyBorder(0, 0, 10, 0));

        panel.add(actions, BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JTable buildTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setRowHeight(22);
        table.setFillsViewportHeight(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        return table;
    }

    private JButton buildPrimaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(new Color(37, 99, 235));
        btn.setForeground(new Color(245, 245, 255));
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton buildSecondaryButton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(new Color(50, 50, 70));
        btn.setForeground(new Color(200, 200, 220));
        btn.setFont(new Font("Arial", Font.PLAIN, 12));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void showError(String title, Exception ex) {
        JOptionPane.showMessageDialog(this,
                ex.getMessage() != null ? ex.getMessage() : ex.toString(),
                title, JOptionPane.ERROR_MESSAGE);
    }
}
