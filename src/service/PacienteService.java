package service;

import dao.DuenoDAO;
import dao.PacienteDAO;
import model.Dueno;
import model.Paciente;

import java.sql.SQLException;
import java.util.List;

public class PacienteService {

    private final PacienteDAO pacienteDAO = new PacienteDAO();
    private final DuenoDAO duenoDAO       = new DuenoDAO();

    public List<Paciente> listarPorClinica(int idClinica) throws SQLException {
        return pacienteDAO.listarPorClinica(idClinica);
    }

    public List<Paciente> buscarPorNombre(String nombre, int idClinica) throws SQLException {
        return pacienteDAO.buscarPorNombreEnClinica(nombre, idClinica);
    }

    public Paciente buscarPorId(int id) throws SQLException {
        return pacienteDAO.buscarPorId(id);
    }

    public List<Dueno> listarDuenosPorClinica(int idClinica) throws SQLException {
        return duenoDAO.listarPorClinica(idClinica);
    }

    public Dueno registrarDueno(String nombre, String apellido, String telefono,
                                String email, int idClinica) throws SQLException {
        Dueno d = new Dueno();
        d.setNombre(nombre);
        d.setApellido(apellido);
        d.setTelefono(telefono);
        d.setEmail(email);
        d.setIdClinica(idClinica);
        duenoDAO.insertar(d);
        return d;
    }

    public Paciente registrarPaciente(String nombre, String especie, String raza,
                                      java.time.LocalDate fechaNac, int idDueno,
                                      int idClinica) throws SQLException {
        if (nombre == null || nombre.isBlank())
            throw new IllegalArgumentException("El nombre del paciente no puede estar vacío.");
        if (especie == null || especie.isBlank())
            throw new IllegalArgumentException("Debe indicarse la especie.");

        Paciente p = new Paciente();
        p.setNombre(nombre.trim());
        p.setEspecie(especie.trim());
        p.setRaza(raza != null ? raza.trim() : null);
        p.setFechaNacimiento(fechaNac);
        p.setIdDueno(idDueno);
        p.setIdClinica(idClinica);
        pacienteDAO.insertar(p);
        return p;
    }

    public void actualizarPaciente(Paciente p) throws SQLException {
        pacienteDAO.actualizar(p);
    }
}
