package dao;

import model.Resultado;

import java.sql.*;

public class ResultadoDAO {

    public Resultado buscarPorOrden(int idOrden) throws SQLException {
        String sql = "SELECT r.*, u.nombre_completo AS nombre_tecnico " +
                     "FROM resultados r JOIN usuarios u ON r.id_tecnico = u.id " +
                     "WHERE r.id_orden = ?";
        try (PreparedStatement ps = ConexionDB.getConexion().prepareStatement(sql)) {
            ps.setInt(1, idOrden);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    public void insertar(Resultado r) throws SQLException {
        String sql = "INSERT INTO resultados (id_orden, observaciones, ruta_archivo, id_tecnico) VALUES (?,?,?,?)";
        try (PreparedStatement ps = ConexionDB.getConexion().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, r.getIdOrden());
            ps.setString(2, r.getObservaciones());
            ps.setString(3, r.getRutaArchivo());
            ps.setInt(4, r.getIdTecnico());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) r.setId(keys.getInt(1));
            }
        }
    }

    private Resultado mapear(ResultSet rs) throws SQLException {
        Resultado r = new Resultado();
        r.setId(rs.getInt("id"));
        r.setIdOrden(rs.getInt("id_orden"));
        Timestamp ts = rs.getTimestamp("fecha_carga");
        if (ts != null) r.setFechaCarga(ts.toLocalDateTime());
        r.setObservaciones(rs.getString("observaciones"));
        r.setRutaArchivo(rs.getString("ruta_archivo"));
        r.setIdTecnico(rs.getInt("id_tecnico"));
        r.setNombreTecnico(rs.getString("nombre_tecnico"));
        return r;
    }
}
