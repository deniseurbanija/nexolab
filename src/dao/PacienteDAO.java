package dao;

import model.Paciente;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PacienteDAO {

    private static final String SELECT_CON_DUENO =
            "SELECT p.*, CONCAT(d.nombre,' ',d.apellido) AS nombre_dueno " +
            "FROM pacientes p JOIN duenos d ON p.id_dueno = d.id ";

    public List<Paciente> listarPorClinica(int idClinica) throws SQLException {
        List<Paciente> lista = new ArrayList<>();
        String sql = SELECT_CON_DUENO + "WHERE p.id_clinica = ? ORDER BY p.nombre";
        try (PreparedStatement ps = ConexionDB.getConexion().prepareStatement(sql)) {
            ps.setInt(1, idClinica);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public Paciente buscarPorId(int id) throws SQLException {
        String sql = SELECT_CON_DUENO + "WHERE p.id = ?";
        try (PreparedStatement ps = ConexionDB.getConexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    public List<Paciente> buscarPorNombreEnClinica(String nombre, int idClinica) throws SQLException {
        List<Paciente> lista = new ArrayList<>();
        String sql = SELECT_CON_DUENO + "WHERE p.id_clinica = ? AND p.nombre LIKE ? ORDER BY p.nombre";
        try (PreparedStatement ps = ConexionDB.getConexion().prepareStatement(sql)) {
            ps.setInt(1, idClinica);
            ps.setString(2, "%" + nombre + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public void insertar(Paciente p) throws SQLException {
        String sql = "INSERT INTO pacientes (nombre, especie, raza, fecha_nacimiento, id_dueno, id_clinica) VALUES (?,?,?,?,?,?)";
        try (PreparedStatement ps = ConexionDB.getConexion().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, p.getNombre());
            ps.setString(2, p.getEspecie());
            ps.setString(3, p.getRaza());
            if (p.getFechaNacimiento() != null)
                ps.setDate(4, Date.valueOf(p.getFechaNacimiento()));
            else
                ps.setNull(4, Types.DATE);
            ps.setInt(5, p.getIdDueno());
            ps.setInt(6, p.getIdClinica());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) p.setId(keys.getInt(1));
            }
        }
    }

    public void actualizar(Paciente p) throws SQLException {
        String sql = "UPDATE pacientes SET nombre=?, especie=?, raza=?, fecha_nacimiento=? WHERE id=?";
        try (PreparedStatement ps = ConexionDB.getConexion().prepareStatement(sql)) {
            ps.setString(1, p.getNombre());
            ps.setString(2, p.getEspecie());
            ps.setString(3, p.getRaza());
            if (p.getFechaNacimiento() != null)
                ps.setDate(4, Date.valueOf(p.getFechaNacimiento()));
            else
                ps.setNull(4, Types.DATE);
            ps.setInt(5, p.getId());
            ps.executeUpdate();
        }
    }

    private Paciente mapear(ResultSet rs) throws SQLException {
        Paciente p = new Paciente();
        p.setId(rs.getInt("id"));
        p.setNombre(rs.getString("nombre"));
        p.setEspecie(rs.getString("especie"));
        p.setRaza(rs.getString("raza"));
        Date fecha = rs.getDate("fecha_nacimiento");
        if (fecha != null) p.setFechaNacimiento(fecha.toLocalDate());
        p.setIdDueno(rs.getInt("id_dueno"));
        p.setIdClinica(rs.getInt("id_clinica"));
        p.setNombreDueno(rs.getString("nombre_dueno"));
        return p;
    }
}
