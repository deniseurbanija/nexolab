package dao;

import model.Laboratorio;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LaboratorioDAO {

    public List<Laboratorio> listarActivos() throws SQLException {
        List<Laboratorio> lista = new ArrayList<>();
        String sql = "SELECT * FROM laboratorios WHERE activo = TRUE ORDER BY nombre";
        try (Statement st = ConexionDB.getConexion().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public Laboratorio buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM laboratorios WHERE id = ?";
        try (PreparedStatement ps = ConexionDB.getConexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    public void insertar(Laboratorio l) throws SQLException {
        String sql = "INSERT INTO laboratorios (nombre, direccion, telefono, email) VALUES (?,?,?,?)";
        try (PreparedStatement ps = ConexionDB.getConexion().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, l.getNombre());
            ps.setString(2, l.getDireccion());
            ps.setString(3, l.getTelefono());
            ps.setString(4, l.getEmail());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) l.setId(keys.getInt(1));
            }
        }
    }

    public void actualizar(Laboratorio l) throws SQLException {
        String sql = "UPDATE laboratorios SET nombre=?, direccion=?, telefono=?, email=?, activo=? WHERE id=?";
        try (PreparedStatement ps = ConexionDB.getConexion().prepareStatement(sql)) {
            ps.setString(1, l.getNombre());
            ps.setString(2, l.getDireccion());
            ps.setString(3, l.getTelefono());
            ps.setString(4, l.getEmail());
            ps.setBoolean(5, l.isActivo());
            ps.setInt(6, l.getId());
            ps.executeUpdate();
        }
    }

    private Laboratorio mapear(ResultSet rs) throws SQLException {
        return new Laboratorio(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("direccion"),
                rs.getString("telefono"),
                rs.getString("email"),
                rs.getBoolean("activo")
        );
    }
}
