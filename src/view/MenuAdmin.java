package view;

import dao.ClinicaDAO;
import dao.LaboratorioDAO;
import dao.UsuarioDAO;
import model.*;
import service.AuthService;

import java.sql.SQLException;
import java.util.Scanner;

public class MenuAdmin {

    private final ClinicaDAO     clinicaDAO     = new ClinicaDAO();
    private final LaboratorioDAO laboratorioDAO = new LaboratorioDAO();
    private final UsuarioDAO     usuarioDAO     = new UsuarioDAO();
    private final Scanner        sc;

    public MenuAdmin(Scanner sc) {
        this.sc = sc;
    }

    public void mostrar() {
        boolean salir = false;
        while (!salir) {
            System.out.println("\n╔══════════════════════════════════════╗");
            System.out.println("║   NEXOLAB - Panel Administrador       ║");
            System.out.println("╠══════════════════════════════════════╣");
            System.out.println("║  1. Listar Clínicas                   ║");
            System.out.println("║  2. Registrar Clínica                 ║");
            System.out.println("║  3. Listar Laboratorios               ║");
            System.out.println("║  4. Registrar Laboratorio             ║");
            System.out.println("║  5. Listar Usuarios                   ║");
            System.out.println("║  6. Registrar Usuario                 ║");
            System.out.println("║  0. Cerrar Sesión                     ║");
            System.out.println("╚══════════════════════════════════════╝");
            System.out.print("Opción: ");

            String opcion = sc.nextLine().trim();
            try {
                switch (opcion) {
                    case "1" -> clinicaDAO.listarActivas().forEach(System.out::println);
                    case "2" -> registrarClinica();
                    case "3" -> laboratorioDAO.listarActivos().forEach(System.out::println);
                    case "4" -> registrarLaboratorio();
                    case "5" -> usuarioDAO.listarTodos().forEach(System.out::println);
                    case "6" -> registrarUsuario();
                    case "0" -> salir = true;
                    default  -> System.out.println("[!] Opción inválida.");
                }
            } catch (SQLException e) {
                System.err.println("[ERROR BD] " + e.getMessage());
            }
        }
    }

    private void registrarClinica() throws SQLException {
        System.out.println("\n── Nueva Clínica ───────────────────────");
        System.out.print("Nombre: ");     String nombre = sc.nextLine().trim();
        System.out.print("Dirección: ");  String dir    = sc.nextLine().trim();
        System.out.print("Teléfono: ");   String tel    = sc.nextLine().trim();
        System.out.print("Email: ");      String email  = sc.nextLine().trim();

        Clinica c = new Clinica();
        c.setNombre(nombre); c.setDireccion(dir); c.setTelefono(tel); c.setEmail(email);
        clinicaDAO.insertar(c);
        System.out.println("[OK] Clínica registrada con ID: " + c.getId());
    }

    private void registrarLaboratorio() throws SQLException {
        System.out.println("\n── Nuevo Laboratorio ───────────────────");
        System.out.print("Nombre: ");     String nombre = sc.nextLine().trim();
        System.out.print("Dirección: ");  String dir    = sc.nextLine().trim();
        System.out.print("Teléfono: ");   String tel    = sc.nextLine().trim();
        System.out.print("Email: ");      String email  = sc.nextLine().trim();

        Laboratorio l = new Laboratorio();
        l.setNombre(nombre); l.setDireccion(dir); l.setTelefono(tel); l.setEmail(email);
        laboratorioDAO.insertar(l);
        System.out.println("[OK] Laboratorio registrado con ID: " + l.getId());
    }

    private void registrarUsuario() throws SQLException {
        System.out.println("\n── Nuevo Usuario ───────────────────────");
        System.out.print("Username: ");        String user  = sc.nextLine().trim();
        System.out.print("Contraseña: ");      String pass  = sc.nextLine().trim();
        System.out.print("Nombre completo: "); String nombre = sc.nextLine().trim();
        System.out.println("Rol: 1=ADMIN  2=VETERINARIO  3=LABORATORIO");
        System.out.print("Opción: ");

        Rol rol = switch (sc.nextLine().trim()) {
            case "1" -> Rol.ADMIN;
            case "2" -> Rol.VETERINARIO;
            case "3" -> Rol.LABORATORIO;
            default  -> null;
        };
        if (rol == null) { System.out.println("[!] Rol inválido."); return; }

        Integer idClinica = null, idLab = null;
        if (rol == Rol.VETERINARIO) {
            System.out.print("ID de la clínica: ");
            idClinica = Integer.parseInt(sc.nextLine().trim());
        } else if (rol == Rol.LABORATORIO) {
            System.out.print("ID del laboratorio: ");
            idLab = Integer.parseInt(sc.nextLine().trim());
        }

        Usuario u = new Usuario();
        u.setUsername(user);
        u.setPasswordHash(AuthService.hashSHA256(pass));
        u.setNombreCompleto(nombre);
        u.setRol(rol);
        u.setIdClinica(idClinica);
        u.setIdLaboratorio(idLab);
        usuarioDAO.insertar(u);
        System.out.println("[OK] Usuario creado con ID: " + u.getId());
    }
}
