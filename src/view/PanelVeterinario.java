package view;

import model.Usuario;
import model.Dueno;
import model.Laboratorio;
import model.OrdenAnalisis;
import model.Paciente;
import model.TipoAnalisis;
import service.OrdenService;
import service.PacienteService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Ventana principal del Veterinario.
 * Por ahora es un placeholder — se reemplazará con el panel real.
 */
public class PanelVeterinario extends JFrame {

    private final Usuario usuario;
    private final PacienteService pacienteService = new PacienteService();
    private final OrdenService ordenService = new OrdenService();

    private DefaultTableModel ordenesModel;
    private JTable ordenesTable;

    public PanelVeterinario(Usuario usuario) {
        this.usuario = usuario;
        setTitle("NexoLab - Veterinario");
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

        JLabel lblTitulo = new JLabel("NEXOLAB  ·  Panel Veterinario");
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

        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(new Color(15, 15, 25));
        center.setBorder(new EmptyBorder(16, 16, 16, 16));

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actions.setBackground(new Color(15, 15, 25));

        JButton btnRefrescar = buildPrimaryButton("Refrescar");
        btnRefrescar.addActionListener(e -> cargarOrdenes());

        JButton btnNuevoPaciente = buildPrimaryButton("Nuevo paciente");
        btnNuevoPaciente.addActionListener(e -> {
            if (crearPacienteDialog()) cargarOrdenes();
        });

        JButton btnNuevaOrden = buildPrimaryButton("Nueva solicitud");
        btnNuevaOrden.addActionListener(e -> {
            if (crearOrdenDialog()) cargarOrdenes();
        });

        actions.add(btnRefrescar);
        actions.add(btnNuevoPaciente);
        actions.add(btnNuevaOrden);

        ordenesModel = new DefaultTableModel(
                new Object[]{"ID", "Paciente", "Laboratorio", "Estado", "Fecha"}, 0
        ) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        ordenesTable = new JTable(ordenesModel);
        ordenesTable.setRowHeight(22);
        ordenesTable.setFillsViewportHeight(true);
        ordenesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scroll = new JScrollPane(ordenesTable);
        center.add(actions, BorderLayout.NORTH);
        center.add(scroll, BorderLayout.CENTER);

        root.add(topBar, BorderLayout.NORTH);
        root.add(center, BorderLayout.CENTER);
        add(root);

        SwingUtilities.invokeLater(this::cargarOrdenes);
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

    private void cargarOrdenes() {
        ordenesModel.setRowCount(0);
        try {
            List<OrdenAnalisis> ordenes = ordenService.listarPorClinica(usuario.getIdClinica());
            for (OrdenAnalisis o : ordenes) {
                String paciente = o.getNombrePaciente() != null ? o.getNombrePaciente() : "ID:" + o.getIdPaciente();
                String lab = o.getNombreLaboratorio() != null ? o.getNombreLaboratorio() : "ID:" + o.getIdLaboratorio();
                String fecha = o.getFechaCreacion() != null ? o.getFechaCreacion().toLocalDate().toString() : "S/D";
                ordenesModel.addRow(new Object[]{o.getId(), paciente, lab, o.getEstado(), fecha});
            }
        } catch (SQLException ex) {
            showError("Error al cargar órdenes", ex);
        }
    }

    private boolean crearPacienteDialog() {
        JTextField tfDuenoNombre = new JTextField();
        JTextField tfDuenoApellido = new JTextField();
        JTextField tfDuenoTel = new JTextField();
        JTextField tfDuenoEmail = new JTextField();

        JTextField tfPacienteNombre = new JTextField();
        JTextField tfEspecie = new JTextField();
        JTextField tfRaza = new JTextField();
        JTextField tfFechaNac = new JTextField();

        Object[] fields = {
                "── Dueño ──",
                "Nombre:", tfDuenoNombre,
                "Apellido:", tfDuenoApellido,
                "Teléfono:", tfDuenoTel,
                "Email:", tfDuenoEmail,
                " ",
                "── Paciente ──",
                "Nombre mascota:", tfPacienteNombre,
                "Especie:", tfEspecie,
                "Raza (opcional):", tfRaza,
                "Fecha nac (YYYY-MM-DD, opcional):", tfFechaNac
        };

        int ok = JOptionPane.showConfirmDialog(
                this, fields, "Registrar nuevo paciente",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );
        if (ok != JOptionPane.OK_OPTION) return false;

        try {
            Dueno d = pacienteService.registrarDueno(
                    tfDuenoNombre.getText().trim(),
                    tfDuenoApellido.getText().trim(),
                    tfDuenoTel.getText().trim(),
                    tfDuenoEmail.getText().trim(),
                    usuario.getIdClinica()
            );

            LocalDate fecha = null;
            String fechaStr = tfFechaNac.getText().trim();
            if (!fechaStr.isEmpty()) {
                try { fecha = LocalDate.parse(fechaStr); }
                catch (DateTimeParseException e) {
                    JOptionPane.showMessageDialog(this, "Fecha inválida. Se omitirá.", "Aviso", JOptionPane.WARNING_MESSAGE);
                }
            }

            Paciente p = pacienteService.registrarPaciente(
                    tfPacienteNombre.getText().trim(),
                    tfEspecie.getText().trim(),
                    tfRaza.getText().trim().isEmpty() ? null : tfRaza.getText().trim(),
                    fecha,
                    d.getId(),
                    usuario.getIdClinica()
            );

            JOptionPane.showMessageDialog(this,
                    "Paciente registrado con ID: " + p.getId(),
                    "OK", JOptionPane.INFORMATION_MESSAGE);
            return true;
        } catch (SQLException | IllegalArgumentException ex) {
            showError("No se pudo registrar el paciente", ex);
            return false;
        }
    }

    private boolean crearOrdenDialog() {
        try {
            List<Paciente> pacientes = pacienteService.listarPorClinica(usuario.getIdClinica());
            if (pacientes.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No hay pacientes. Registrá uno primero.",
                        "Atención", JOptionPane.WARNING_MESSAGE);
                return false;
            }
            Paciente paciente = (Paciente) JOptionPane.showInputDialog(
                    this,
                    "Seleccioná el paciente:",
                    "Nueva solicitud",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    pacientes.toArray(),
                    pacientes.get(0)
            );
            if (paciente == null) return false;

            List<Laboratorio> labs = new dao.LaboratorioDAO().listarActivos();
            if (labs.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No hay laboratorios disponibles.",
                        "Atención", JOptionPane.WARNING_MESSAGE);
                return false;
            }
            Laboratorio lab = (Laboratorio) JOptionPane.showInputDialog(
                    this,
                    "Seleccioná el laboratorio:",
                    "Nueva solicitud",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    labs.toArray(),
                    labs.get(0)
            );
            if (lab == null) return false;

            List<TipoAnalisis> catalogo = ordenService.listarCatalogoLaboratorio(lab.getId());
            if (catalogo.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "El laboratorio no tiene análisis en su catálogo.",
                        "Atención", JOptionPane.WARNING_MESSAGE);
                return false;
            }

            JList<TipoAnalisis> list = new JList<>(catalogo.toArray(new TipoAnalisis[0]));
            list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            JScrollPane scroll = new JScrollPane(list);
            scroll.setPreferredSize(new Dimension(420, 220));

            JTextField tfObs = new JTextField();
            Object[] fields = {
                    "Seleccioná uno o más análisis (Ctrl/Shift para multiselección):",
                    scroll,
                    "Observaciones (opcional):",
                    tfObs
            };
            int ok = JOptionPane.showConfirmDialog(
                    this, fields, "Nueva solicitud",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
            );
            if (ok != JOptionPane.OK_OPTION) return false;

            List<TipoAnalisis> seleccionados = list.getSelectedValuesList();
            if (seleccionados == null || seleccionados.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Debés seleccionar al menos un análisis.",
                        "Atención", JOptionPane.WARNING_MESSAGE);
                return false;
            }

            List<Integer> ids = new ArrayList<>();
            for (TipoAnalisis t : seleccionados) ids.add(t.getId());

            OrdenAnalisis orden = ordenService.crearOrden(
                    paciente.getId(),
                    usuario.getIdClinica(),
                    lab.getId(),
                    usuario.getId(),
                    tfObs.getText().trim().isEmpty() ? null : tfObs.getText().trim(),
                    ids
            );

            JOptionPane.showMessageDialog(this,
                    "Orden #" + orden.getId() + " creada. Estado: PENDIENTE.",
                    "OK", JOptionPane.INFORMATION_MESSAGE);
            return true;
        } catch (SQLException | IllegalArgumentException ex) {
            showError("No se pudo crear la solicitud", ex);
            return false;
        }
    }

    private void showError(String title, Exception ex) {
        JOptionPane.showMessageDialog(
                this,
                (ex.getMessage() != null ? ex.getMessage() : ex.toString()),
                title,
                JOptionPane.ERROR_MESSAGE
        );
    }
}
