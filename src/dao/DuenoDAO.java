package dao;

import model.Dueno;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DuenoDAO {

    public List<Dueno> listarPorClinica(int idClinica) throws SQLException {
        List<Dueno> lista = new ArrayList<>();
        String sql = "SELECT * FROM duenos WHERE id_clinica = ? ORDER BY apellido, nombre";
        try (PreparedStatement ps = ConexionDB.getConexion().prepareStatement(sql)) {
            ps.setInt(1, idClinica);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public Dueno buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM duenos WHERE id = ?";
        try (PreparedStatement ps = ConexionDB.getConexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    public void insertar(Dueno d) throws SQLException {
        String sql = "INSERT INTO duenos (nombre, apellido, telefono, email, id_clinica) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = ConexionDB.getConexion().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, d.getNombre());
            ps.setString(2, d.getApellido());
            ps.setString(3, d.getTelefono());
            ps.setString(4, d.getEmail());
            ps.setInt(5, d.getIdClinica());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) d.setId(keys.getInt(1));
            }
        }
    }

    private Dueno mapear(ResultSet rs) throws SQLException {
        return new Dueno(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("apellido"),
                rs.getString("telefono"),
                rs.getString("email"),
                rs.getInt("id_clinica")
        );
    }
}
