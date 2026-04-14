package dao;

import model.TipoAnalisis;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TipoAnalisisDAO {

    public List<TipoAnalisis> listarPorLaboratorio(int idLaboratorio) throws SQLException {
        List<TipoAnalisis> lista = new ArrayList<>();
        String sql = "SELECT * FROM tipos_analisis WHERE id_laboratorio = ? ORDER BY nombre";
        try (PreparedStatement ps = ConexionDB.getConexion().prepareStatement(sql)) {
            ps.setInt(1, idLaboratorio);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public List<TipoAnalisis> listarPorOrden(int idOrden) throws SQLException {
        List<TipoAnalisis> lista = new ArrayList<>();
        String sql = "SELECT ta.* FROM tipos_analisis ta " +
                     "JOIN orden_items oi ON ta.id = oi.id_tipo_analisis " +
                     "WHERE oi.id_orden = ?";
        try (PreparedStatement ps = ConexionDB.getConexion().prepareStatement(sql)) {
            ps.setInt(1, idOrden);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public TipoAnalisis buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM tipos_analisis WHERE id = ?";
        try (PreparedStatement ps = ConexionDB.getConexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    private TipoAnalisis mapear(ResultSet rs) throws SQLException {
        return new TipoAnalisis(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("descripcion"),
                rs.getInt("id_laboratorio")
        );
    }
}
