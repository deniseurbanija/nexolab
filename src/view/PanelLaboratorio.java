package view;

import model.*;
import service.OrdenService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class PanelLaboratorio extends JFrame {

    private final Usuario      usuario;
    private final OrdenService ordenService = new OrdenService();

    private DefaultTableModel ordenesModel;
    private JTable            ordenesTable;

    public PanelLaboratorio(Usuario usuario) {
        this.usuario = usuario;
        setTitle("NexoLab - Laboratorio");
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

        JLabel lblTitulo = new JLabel("NEXOLAB  ·  Panel Laboratorio");
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

        // Centro
        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(new Color(15, 15, 25));
        center.setBorder(new EmptyBorder(16, 16, 16, 16));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actions.setBackground(new Color(15, 15, 25));
        actions.setBorder(new EmptyBorder(0, 0, 10, 0));

        JButton btnPendientes = buildPrimaryButton("Pendientes");
        btnPendientes.addActionListener(e -> cargarOrdenes(false));

        JButton btnTodas = buildSecondaryButton("Todas");
        btnTodas.addActionListener(e -> cargarOrdenes(true));

        JButton btnActualizarEstado = buildPrimaryButton("Actualizar Estado");
        btnActualizarEstado.addActionListener(e -> actualizarEstadoDialog());

        JButton btnCargarResultado = buildPrimaryButton("Cargar Resultado");
        btnCargarResultado.addActionListener(e -> cargarResultadoDialog());

        actions.add(btnPendientes);
        actions.add(btnTodas);
        actions.add(btnActualizarEstado);
        actions.add(btnCargarResultado);

        ordenesModel = new DefaultTableModel(
                new Object[]{"ID", "Paciente", "Estado", "Veterinario", "Fecha"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        ordenesTable = new JTable(ordenesModel);
        ordenesTable.setRowHeight(22);
        ordenesTable.setFillsViewportHeight(true);
        ordenesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        center.add(actions, BorderLayout.NORTH);
        center.add(new JScrollPane(ordenesTable), BorderLayout.CENTER);

        root.add(topBar, BorderLayout.NORTH);
        root.add(center, BorderLayout.CENTER);
        add(root);

        SwingUtilities.invokeLater(() -> cargarOrdenes(false));
    }

    private void cargarOrdenes(boolean todas) {
        ordenesModel.setRowCount(0);
        try {
            List<OrdenAnalisis> lista = todas
                    ? ordenService.listarTodasLaboratorio(usuario.getIdLaboratorio())
                    : ordenService.listarPendientesLaboratorio(usuario.getIdLaboratorio());
            for (OrdenAnalisis o : lista) {
                String paciente   = o.getNombrePaciente()    != null ? o.getNombrePaciente()    : "ID:" + o.getIdPaciente();
                String veterinario = o.getNombreVeterinario() != null ? o.getNombreVeterinario() : "ID:" + o.getIdVeterinario();
                String fecha      = o.getFechaCreacion()     != null ? o.getFechaCreacion().toLocalDate().toString() : "S/D";
                ordenesModel.addRow(new Object[]{o.getId(), paciente, o.getEstado(), veterinario, fecha});
            }
        } catch (SQLException ex) { showError("Error al cargar órdenes", ex); }
    }

    private void actualizarEstadoDialog() {
        int idOrden = getSelectedOrdenId();
        if (idOrden < 0) return;

        try {
            OrdenAnalisis orden = ordenService.buscarPorId(idOrden);
            if (orden == null) {
                JOptionPane.showMessageDialog(this, "Orden no encontrada.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (orden.getIdLaboratorio() != usuario.getIdLaboratorio()) {
                JOptionPane.showMessageDialog(this, "Esta orden no pertenece a su laboratorio.", "Acceso denegado", JOptionPane.ERROR_MESSAGE);
                return;
            }

            JComboBox<EstadoOrden> cbEstado = new JComboBox<>(EstadoOrden.values());
            cbEstado.setSelectedItem(orden.getEstado());

            int ok = JOptionPane.showConfirmDialog(this,
                    new Object[]{"Estado actual: " + orden.getEstado(), "Nuevo estado:", cbEstado},
                    "Actualizar Estado – Orden #" + idOrden,
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (ok != JOptionPane.OK_OPTION) return;

            EstadoOrden nuevoEstado = (EstadoOrden) cbEstado.getSelectedItem();
            ordenService.cambiarEstado(idOrden, nuevoEstado);
            JOptionPane.showMessageDialog(this,
                    "Orden #" + idOrden + " actualizada a: " + nuevoEstado, "OK", JOptionPane.INFORMATION_MESSAGE);
            cargarOrdenes(false);
        } catch (SQLException | IllegalArgumentException | IllegalStateException ex) {
            showError("No se pudo actualizar el estado", ex);
        }
    }

    private void cargarResultadoDialog() {
        int idOrden = getSelectedOrdenId();
        if (idOrden < 0) return;

        try {
            OrdenAnalisis orden = ordenService.buscarPorId(idOrden);
            if (orden == null) {
                JOptionPane.showMessageDialog(this, "Orden no encontrada.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (orden.getIdLaboratorio() != usuario.getIdLaboratorio()) {
                JOptionPane.showMessageDialog(this, "Esta orden no pertenece a su laboratorio.", "Acceso denegado", JOptionPane.ERROR_MESSAGE);
                return;
            }

            JTextField tfRuta = new JTextField();
            JTextField tfObs  = new JTextField();

            int ok = JOptionPane.showConfirmDialog(this,
                    new Object[]{"Ruta del archivo PDF (opcional):", tfRuta, "Observaciones:", tfObs},
                    "Cargar Resultado – Orden #" + idOrden,
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (ok != JOptionPane.OK_OPTION) return;

            String ruta = tfRuta.getText().trim();
            String obs  = tfObs.getText().trim();
            Resultado r = ordenService.cargarResultado(idOrden,
                    obs.isEmpty()  ? null : obs,
                    ruta.isEmpty() ? null : ruta,
                    usuario.getId());
            JOptionPane.showMessageDialog(this, "Resultado cargado con ID: " + r.getId(),
                    "OK", JOptionPane.INFORMATION_MESSAGE);
            cargarOrdenes(false);
        } catch (SQLException | IllegalArgumentException | IllegalStateException ex) {
            showError("No se pudo cargar el resultado", ex);
        }
    }

    private int getSelectedOrdenId() {
        int row = ordenesTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Seleccioná una orden de la tabla.",
                    "Atención", JOptionPane.WARNING_MESSAGE);
            return -1;
        }
        return (int) ordenesModel.getValueAt(row, 0);
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
