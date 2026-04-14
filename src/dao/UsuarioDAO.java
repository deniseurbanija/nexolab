package dao;

import model.Rol;
import model.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    public Usuario buscarPorUsername(String username) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE username = ? AND activo = TRUE";
        try (PreparedStatement ps = ConexionDB.getConexion().prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    public Usuario buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM usuarios WHERE id = ?";
        try (PreparedStatement ps = ConexionDB.getConexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    public List<Usuario> listarTodos() throws SQLException {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuarios WHERE activo = TRUE ORDER BY nombre_completo";
        try (Statement st = ConexionDB.getConexion().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    public void insertar(Usuario u) throws SQLException {
        String sql = "INSERT INTO usuarios (username, password_hash, nombre_completo, rol, id_clinica, id_laboratorio) VALUES (?,?,?,?,?,?)";
        try (PreparedStatement ps = ConexionDB.getConexion().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, u.getUsername());
            ps.setString(2, u.getPasswordHash());
            ps.setString(3, u.getNombreCompleto());
            ps.setString(4, u.getRol().name());
            setNullableInt(ps, 5, u.getIdClinica());
            setNullableInt(ps, 6, u.getIdLaboratorio());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) u.setId(keys.getInt(1));
            }
        }
    }

    private Usuario mapear(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setId(rs.getInt("id"));
        u.setUsername(rs.getString("username"));
        u.setPasswordHash(rs.getString("password_hash"));
        u.setNombreCompleto(rs.getString("nombre_completo"));
        u.setRol(Rol.fromString(rs.getString("rol")));
        int idCli = rs.getInt("id_clinica");
        u.setIdClinica(rs.wasNull() ? null : idCli);
        int idLab = rs.getInt("id_laboratorio");
        u.setIdLaboratorio(rs.wasNull() ? null : idLab);
        u.setActivo(rs.getBoolean("activo"));
        return u;
    }

    private void setNullableInt(PreparedStatement ps, int idx, Integer valor) throws SQLException {
        if (valor == null) ps.setNull(idx, Types.INTEGER);
        else               ps.setInt(idx, valor);
    }
}
