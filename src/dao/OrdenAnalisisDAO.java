package dao;

import model.EstadoOrden;
import model.OrdenAnalisis;
import model.TipoAnalisis;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrdenAnalisisDAO {

    private static final String SELECT_BASE =
            "SELECT o.*, p.nombre AS nombre_paciente, " +
            "       l.nombre AS nombre_laboratorio, " +
            "       u.nombre_completo AS nombre_veterinario " +
            "FROM ordenes_analisis o " +
            "JOIN pacientes p ON o.id_paciente = p.id " +
            "JOIN laboratorios l ON o.id_laboratorio = l.id " +
            "JOIN usuarios u ON o.id_veterinario = u.id ";

    public OrdenAnalisis buscarPorId(int id) throws SQLException {
        String sql = SELECT_BASE + "WHERE o.id = ?";
        try (PreparedStatement ps = ConexionDB.getConexion().prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    public List<OrdenAnalisis> listarPorClinica(int idClinica) throws SQLException {
        List<OrdenAnalisis> lista = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE o.id_clinica = ? ORDER BY o.fecha_creacion DESC";
        try (PreparedStatement ps = ConexionDB.getConexion().prepareStatement(sql)) {
            ps.setInt(1, idClinica);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public List<OrdenAnalisis> listarPorPaciente(int idPaciente) throws SQLException {
        List<OrdenAnalisis> lista = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE o.id_paciente = ? ORDER BY o.fecha_creacion DESC";
        try (PreparedStatement ps = ConexionDB.getConexion().prepareStatement(sql)) {
            ps.setInt(1, idPaciente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public List<OrdenAnalisis> listarPorLaboratorio(int idLaboratorio) throws SQLException {
        List<OrdenAnalisis> lista = new ArrayList<>();
        String sql = SELECT_BASE + "WHERE o.id_laboratorio = ? ORDER BY o.fecha_creacion ASC";
        try (PreparedStatement ps = ConexionDB.getConexion().prepareStatement(sql)) {
            ps.setInt(1, idLaboratorio);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public List<OrdenAnalisis> listarPendientesPorLaboratorio(int idLaboratorio) throws SQLException {
        List<OrdenAnalisis> lista = new ArrayList<>();
        String sql = SELECT_BASE +
                "WHERE o.id_laboratorio = ? AND o.estado IN ('PENDIENTE','RECIBIDA','EN_PROCESO') " +
                "ORDER BY o.fecha_creacion ASC";
        try (PreparedStatement ps = ConexionDB.getConexion().prepareStatement(sql)) {
            ps.setInt(1, idLaboratorio);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public int insertar(OrdenAnalisis o) throws SQLException {
        String sql = "INSERT INTO ordenes_analisis (estado, observaciones, id_paciente, id_clinica, id_laboratorio, id_veterinario) " +
                     "VALUES (?,?,?,?,?,?)";
        try (PreparedStatement ps = ConexionDB.getConexion().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, EstadoOrden.PENDIENTE.name());
            ps.setString(2, o.getObservaciones());
            ps.setInt(3, o.getIdPaciente());
            ps.setInt(4, o.getIdClinica());
            ps.setInt(5, o.getIdLaboratorio());
            ps.setInt(6, o.getIdVeterinario());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int idGenerado = keys.getInt(1);
                    o.setId(idGenerado);
                    return idGenerado;
                }
            }
        }
        return -1;
    }

    public void insertarItems(int idOrden, List<Integer> idsTipoAnalisis) throws SQLException {
        String sql = "INSERT INTO orden_items (id_orden, id_tipo_analisis) VALUES (?,?)";
        try (PreparedStatement ps = ConexionDB.getConexion().prepareStatement(sql)) {
            for (int idTipo : idsTipoAnalisis) {
                ps.setInt(1, idOrden);
                ps.setInt(2, idTipo);
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }

    public void actualizarEstado(int idOrden, EstadoOrden nuevoEstado) throws SQLException {
        String sql = "UPDATE ordenes_analisis SET estado = ? WHERE id = ?";
        try (PreparedStatement ps = ConexionDB.getConexion().prepareStatement(sql)) {
            ps.setString(1, nuevoEstado.name());
            ps.setInt(2, idOrden);
            ps.executeUpdate();
        }
    }

    private OrdenAnalisis mapear(ResultSet rs) throws SQLException {
        OrdenAnalisis o = new OrdenAnalisis();
        o.setId(rs.getInt("id"));
        Timestamp ts = rs.getTimestamp("fecha_creacion");
        if (ts != null) o.setFechaCreacion(ts.toLocalDateTime());
        Timestamp tsUlt = rs.getTimestamp("fecha_ultimo_cambio");
        if (tsUlt != null) o.setFechaUltimoCambio(tsUlt.toLocalDateTime());
        o.setEstado(EstadoOrden.fromString(rs.getString("estado")));
        o.setObservaciones(rs.getString("observaciones"));
        o.setIdPaciente(rs.getInt("id_paciente"));
        o.setIdClinica(rs.getInt("id_clinica"));
        o.setIdLaboratorio(rs.getInt("id_laboratorio"));
        o.setIdVeterinario(rs.getInt("id_veterinario"));
        o.setNombrePaciente(rs.getString("nombre_paciente"));
        o.setNombreLaboratorio(rs.getString("nombre_laboratorio"));
        o.setNombreVeterinario(rs.getString("nombre_veterinario"));
        return o;
    }
}
