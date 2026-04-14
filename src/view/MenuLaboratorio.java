package view;

import model.*;
import service.OrdenService;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class MenuLaboratorio {

    private final Usuario      usuario;
    private final OrdenService ordenService = new OrdenService();
    private final Scanner      sc;

    public MenuLaboratorio(Usuario usuario, Scanner sc) {
        this.usuario = usuario;
        this.sc = sc;
    }

    public void mostrar() {
        boolean salir = false;
        while (!salir) {
            System.out.println("\n╔══════════════════════════════════════╗");
            System.out.println("║   NEXOLAB - Panel Laboratorio         ║");
            System.out.println("║   " + usuario.getNombreCompleto());
            System.out.println("╠══════════════════════════════════════╣");
            System.out.println("║  1. Ver Órdenes Pendientes            ║");
            System.out.println("║  2. Ver Todas las Órdenes             ║");
            System.out.println("║  3. Actualizar Estado de Orden        ║");
            System.out.println("║  4. Cargar Resultado                  ║");
            System.out.println("║  0. Cerrar Sesión                     ║");
            System.out.println("╚══════════════════════════════════════╝");
            System.out.print("Opción: ");

            String opcion = sc.nextLine().trim();
            try {
                switch (opcion) {
                    case "1" -> verPendientes();
                    case "2" -> verTodas();
                    case "3" -> actualizarEstado();
                    case "4" -> cargarResultado();
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

    private void verPendientes() throws SQLException {
        List<OrdenAnalisis> lista = ordenService.listarPendientesLaboratorio(usuario.getIdLaboratorio());
        System.out.println("\n── Órdenes Activas ────────────────────");
        if (lista.isEmpty()) System.out.println("No hay órdenes pendientes.");
        else lista.forEach(System.out::println);
    }

    private void verTodas() throws SQLException {
        List<OrdenAnalisis> lista = ordenService.listarTodasLaboratorio(usuario.getIdLaboratorio());
        System.out.println("\n── Todas las Órdenes ───────────────────");
        if (lista.isEmpty()) System.out.println("Sin órdenes registradas.");
        else lista.forEach(System.out::println);
    }

    private void actualizarEstado() throws SQLException {
        System.out.print("ID de la orden: ");
        int idOrden = Integer.parseInt(sc.nextLine().trim());

        OrdenAnalisis orden = ordenService.buscarPorId(idOrden);
        if (orden == null) { System.out.println("[!] Orden no encontrada."); return; }
        if (orden.getIdLaboratorio() != usuario.getIdLaboratorio()) {
            System.out.println("[!] Esta orden no pertenece a su laboratorio."); return;
        }

        System.out.println("Estado actual: " + orden.getEstado());
        System.out.println("Nuevo estado:");
        System.out.println("  1. RECIBIDA");
        System.out.println("  2. EN_PROCESO");
        System.out.println("  3. FINALIZADA");
        System.out.println("  4. CANCELADA");
        System.out.print("Opción: ");

        EstadoOrden nuevoEstado = switch (sc.nextLine().trim()) {
            case "1" -> EstadoOrden.RECIBIDA;
            case "2" -> EstadoOrden.EN_PROCESO;
            case "3" -> EstadoOrden.FINALIZADA;
            case "4" -> EstadoOrden.CANCELADA;
            default  -> null;
        };

        if (nuevoEstado == null) { System.out.println("[!] Opción inválida."); return; }

        ordenService.cambiarEstado(idOrden, nuevoEstado);
        System.out.println("[OK] Orden #" + idOrden + " actualizada a: " + nuevoEstado);
    }

    private void cargarResultado() throws SQLException {
        System.out.print("ID de la orden (debe estar FINALIZADA): ");
        int idOrden = Integer.parseInt(sc.nextLine().trim());

        OrdenAnalisis orden = ordenService.buscarPorId(idOrden);
        if (orden == null) { System.out.println("[!] Orden no encontrada."); return; }
        if (orden.getIdLaboratorio() != usuario.getIdLaboratorio()) {
            System.out.println("[!] Esta orden no pertenece a su laboratorio."); return;
        }

        System.out.print("Ruta del archivo PDF (opcional, Enter para omitir): ");
        String ruta = sc.nextLine().trim();
        System.out.print("Observaciones del resultado: ");
        String obs = sc.nextLine().trim();

        Resultado r = ordenService.cargarResultado(idOrden, obs.isEmpty() ? null : obs,
                ruta.isEmpty() ? null : ruta, usuario.getId());
        System.out.println("[OK] Resultado cargado con ID: " + r.getId());
    }
}
