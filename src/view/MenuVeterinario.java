package view;

import model.*;
import service.OrdenService;
import service.PacienteService;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MenuVeterinario {

    private final Usuario        usuario;
    private final PacienteService pacienteService = new PacienteService();
    private final OrdenService   ordenService     = new OrdenService();
    private final Scanner        sc;

    public MenuVeterinario(Usuario usuario, Scanner sc) {
        this.usuario = usuario;
        this.sc = sc;
    }

    public void mostrar() {
        boolean salir = false;
        while (!salir) {
            System.out.println("\n╔══════════════════════════════════════╗");
            System.out.println("║   NEXOLAB - Panel Veterinario         ║");
            System.out.println("║   " + usuario.getNombreCompleto());
            System.out.println("╠══════════════════════════════════════╣");
            System.out.println("║  1. Gestionar Pacientes               ║");
            System.out.println("║  2. Nueva Orden de Análisis           ║");
            System.out.println("║  3. Consultar Estado de Órdenes       ║");
            System.out.println("║  4. Historial de Análisis de Paciente ║");
            System.out.println("║  0. Cerrar Sesión                     ║");
            System.out.println("╚══════════════════════════════════════╝");
            System.out.print("Opción: ");

            String opcion = sc.nextLine().trim();
            try {
                switch (opcion) {
                    case "1" -> menuPacientes();
                    case "2" -> nuevaOrden();
                    case "3" -> consultarEstadoOrdenes();
                    case "4" -> historialPaciente();
                    case "0" -> salir = true;
                    default  -> System.out.println("[!] Opción inválida.");
                }
            } catch (SQLException e) {
                System.err.println("[ERROR BD] " + e.getMessage());
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.err.println("[ERROR] " + e.getMessage());
            }
        }
    }

    // ── Gestión de Pacientes ─────────────────────────────────────────

    private void menuPacientes() throws SQLException {
        System.out.println("\n--- Gestión de Pacientes ---");
        System.out.println("1. Listar pacientes");
        System.out.println("2. Buscar por nombre");
        System.out.println("3. Registrar nuevo paciente");
        System.out.println("0. Volver");
        System.out.print("Opción: ");

        switch (sc.nextLine().trim()) {
            case "1" -> listarPacientes();
            case "2" -> buscarPaciente();
            case "3" -> registrarPaciente();
            case "0" -> {}
            default  -> System.out.println("[!] Opción inválida.");
        }
    }

    private void listarPacientes() throws SQLException {
        List<Paciente> lista = pacienteService.listarPorClinica(usuario.getIdClinica());
        if (lista.isEmpty()) {
            System.out.println("No hay pacientes registrados en su clínica.");
            return;
        }
        System.out.println("\n── Pacientes ──────────────────────────");
        lista.forEach(System.out::println);
    }

    private void buscarPaciente() throws SQLException {
        System.out.print("Nombre a buscar: ");
        String nombre = sc.nextLine().trim();
        List<Paciente> lista = pacienteService.buscarPorNombre(nombre, usuario.getIdClinica());
        if (lista.isEmpty()) System.out.println("Sin resultados.");
        else lista.forEach(System.out::println);
    }

    private void registrarPaciente() throws SQLException {
        System.out.println("\n── Nuevo Paciente ─────────────────────");

        // Seleccionar o crear dueño
        List<Dueno> duenos = pacienteService.listarDuenosPorClinica(usuario.getIdClinica());
        Dueno dueno = null;
        if (!duenos.isEmpty()) {
            System.out.println("Dueños registrados:");
            duenos.forEach(System.out::println);
            System.out.print("ID de dueño (Enter para crear nuevo): ");
            String idStr = sc.nextLine().trim();
            if (!idStr.isEmpty()) {
                int idDueno = Integer.parseInt(idStr);
                dueno = duenos.stream().filter(d -> d.getId() == idDueno).findFirst().orElse(null);
            }
        }
        if (dueno == null) {
            System.out.println("Registrando nuevo dueño:");
            System.out.print("  Nombre: ");      String nom = sc.nextLine().trim();
            System.out.print("  Apellido: ");    String ape = sc.nextLine().trim();
            System.out.print("  Teléfono: ");    String tel = sc.nextLine().trim();
            System.out.print("  Email: ");       String mail = sc.nextLine().trim();
            dueno = pacienteService.registrarDueno(nom, ape, tel, mail, usuario.getIdClinica());
            System.out.println("Dueño creado con ID: " + dueno.getId());
        }

        System.out.print("Nombre de la mascota: ");    String nombre = sc.nextLine().trim();
        System.out.print("Especie (Canino/Felino/...): "); String especie = sc.nextLine().trim();
        System.out.print("Raza (opcional): ");         String raza = sc.nextLine().trim();
        System.out.print("Fecha de nacimiento (YYYY-MM-DD, opcional): "); String fechaStr = sc.nextLine().trim();

        LocalDate fecha = null;
        if (!fechaStr.isEmpty()) {
            try { fecha = LocalDate.parse(fechaStr); }
            catch (DateTimeParseException e) { System.out.println("[!] Fecha inválida, se omitirá."); }
        }

        Paciente p = pacienteService.registrarPaciente(nombre, especie,
                raza.isEmpty() ? null : raza, fecha, dueno.getId(), usuario.getIdClinica());
        System.out.println("Paciente registrado con ID: " + p.getId());
    }

    // ── Nueva Orden ──────────────────────────────────────────────────

    private void nuevaOrden() throws SQLException {
        System.out.println("\n── Nueva Orden de Análisis ────────────");

        // 1. Seleccionar paciente
        List<Paciente> pacientes = pacienteService.listarPorClinica(usuario.getIdClinica());
        if (pacientes.isEmpty()) {
            System.out.println("No hay pacientes. Registre uno primero.");
            return;
        }
        pacientes.forEach(System.out::println);
        System.out.print("ID del paciente: ");
        int idPaciente = Integer.parseInt(sc.nextLine().trim());
        Paciente paciente = pacienteService.buscarPorId(idPaciente);
        if (paciente == null || paciente.getIdClinica() != usuario.getIdClinica()) {
            System.out.println("[!] Paciente no encontrado en su clínica.");
            return;
        }

        // 2. Seleccionar laboratorio
        List<Laboratorio> labs = new dao.LaboratorioDAO().listarActivos();
        if (labs.isEmpty()) {
            System.out.println("No hay laboratorios disponibles.");
            return;
        }
        System.out.println("\nLaboratorios disponibles:");
        labs.forEach(System.out::println);
        System.out.print("ID del laboratorio: ");
        int idLab = Integer.parseInt(sc.nextLine().trim());
        Laboratorio lab = labs.stream().filter(l -> l.getId() == idLab).findFirst().orElse(null);
        if (lab == null) { System.out.println("[!] Laboratorio no válido."); return; }

        // 3. Seleccionar análisis del catálogo
        List<TipoAnalisis> catalogo = ordenService.listarCatalogoLaboratorio(idLab);
        if (catalogo.isEmpty()) {
            System.out.println("El laboratorio no tiene análisis en su catálogo.");
            return;
        }
        System.out.println("\nCatálogo de análisis:");
        catalogo.forEach(System.out::println);
        System.out.println("Ingrese IDs separados por coma (ej: 1,3): ");
        String[] partes = sc.nextLine().trim().split(",");
        List<Integer> seleccionados = new ArrayList<>();
        for (String parte : partes) {
            try { seleccionados.add(Integer.parseInt(parte.trim())); }
            catch (NumberFormatException ignored) {}
        }

        System.out.print("Observaciones (opcional): ");
        String obs = sc.nextLine().trim();

        OrdenAnalisis orden = ordenService.crearOrden(idPaciente, usuario.getIdClinica(),
                idLab, usuario.getId(), obs.isEmpty() ? null : obs, seleccionados);
        System.out.println("\n[OK] Orden #" + orden.getId() + " creada para " +
                paciente.getNombre() + ". Estado: PENDIENTE.");
    }

    // ── Consultar estado órdenes ────────────────────────────────────

    private void consultarEstadoOrdenes() throws SQLException {
        List<OrdenAnalisis> ordenes = ordenService.listarPorClinica(usuario.getIdClinica());
        if (ordenes.isEmpty()) { System.out.println("No hay órdenes registradas."); return; }
        System.out.println("\n── Órdenes de su Clínica ──────────────");
        ordenes.forEach(System.out::println);
    }

    // ── Historial por paciente ──────────────────────────────────────

    private void historialPaciente() throws SQLException {
        System.out.print("ID del paciente: ");
        int idPaciente = Integer.parseInt(sc.nextLine().trim());
        Paciente p = pacienteService.buscarPorId(idPaciente);
        if (p == null || p.getIdClinica() != usuario.getIdClinica()) {
            System.out.println("[!] Paciente no encontrado en su clínica.");
            return;
        }

        System.out.println("\n── Historial de " + p.getNombre() + " ─────────────");
        List<OrdenAnalisis> historial = ordenService.listarHistorialPaciente(idPaciente);
        if (historial.isEmpty()) { System.out.println("Sin análisis previos."); return; }

        for (OrdenAnalisis o : historial) {
            System.out.println(o);
            System.out.println("  Análisis: " + o.getAnalisisSolicitados().stream()
                    .map(TipoAnalisis::getNombre)
                    .reduce((a, b) -> a + ", " + b).orElse("—"));
            if (o.getEstado() == EstadoOrden.FINALIZADA) {
                Resultado r = ordenService.buscarResultado(o.getId());
                if (r != null) System.out.println("  Resultado: " + r);
            }
        }
    }
}
