package dao;

import model.Clinica;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClinicaDAO {

    public List<Clinica> listarActivas() throws SQLException {
        List<Clinica> lista = new ArrayList<>();
        String sql = "SELECT * FROM clinicas WHERE activa = TRUE ORDER BY nombre";
        try (Statement st = ConexionDB.getConexion().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public Clinica buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM clinicas WHERE id = ?";
        try (PreparedStatement ps = ConexionDB.getConexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    public void insertar(Clinica c) throws SQLException {
        String sql = "INSERT INTO clinicas (nombre, direccion, telefono, email) VALUES (?,?,?,?)";
        try (PreparedStatement ps = ConexionDB.getConexion().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getNombre());
            ps.setString(2, c.getDireccion());
            ps.setString(3, c.getTelefono());
            ps.setString(4, c.getEmail());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) c.setId(keys.getInt(1));
            }
        }
    }

    public void actualizar(Clinica c) throws SQLException {
        String sql = "UPDATE clinicas SET nombre=?, direccion=?, telefono=?, email=?, activa=? WHERE id=?";
        try (PreparedStatement ps = ConexionDB.getConexion().prepareStatement(sql)) {
            ps.setString(1, c.getNombre());
            ps.setString(2, c.getDireccion());
            ps.setString(3, c.getTelefono());
            ps.setString(4, c.getEmail());
            ps.setBoolean(5, c.isActiva());
            ps.setInt(6, c.getId());
            ps.executeUpdate();
        }
    }

    private Clinica mapear(ResultSet rs) throws SQLException {
        return new Clinica(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("direccion"),
                rs.getString("telefono"),
                rs.getString("email"),
                rs.getBoolean("activa")
        );
    }
}
