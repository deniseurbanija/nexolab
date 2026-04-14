package service;

import dao.OrdenAnalisisDAO;
import dao.ResultadoDAO;
import dao.TipoAnalisisDAO;
import model.EstadoOrden;
import model.OrdenAnalisis;
import model.Resultado;
import model.TipoAnalisis;

import java.sql.SQLException;
import java.util.List;

public class OrdenService {

    private final OrdenAnalisisDAO ordenDAO     = new OrdenAnalisisDAO();
    private final ResultadoDAO     resultadoDAO = new ResultadoDAO();
    private final TipoAnalisisDAO  tipoDAO      = new TipoAnalisisDAO();

    // ----------------------------------------------------------------
    // Consultas
    // ----------------------------------------------------------------

    public List<OrdenAnalisis> listarPorClinica(int idClinica) throws SQLException {
        return ordenDAO.listarPorClinica(idClinica);
    }

    public List<OrdenAnalisis> listarHistorialPaciente(int idPaciente) throws SQLException {
        List<OrdenAnalisis> ordenes = ordenDAO.listarPorPaciente(idPaciente);
        for (OrdenAnalisis o : ordenes) {
            o.setAnalisisSolicitados(tipoDAO.listarPorOrden(o.getId()));
        }
        return ordenes;
    }

    public List<OrdenAnalisis> listarPendientesLaboratorio(int idLaboratorio) throws SQLException {
        return ordenDAO.listarPendientesPorLaboratorio(idLaboratorio);
    }

    public List<OrdenAnalisis> listarTodasLaboratorio(int idLaboratorio) throws SQLException {
        return ordenDAO.listarPorLaboratorio(idLaboratorio);
    }

    public OrdenAnalisis buscarPorId(int id) throws SQLException {
        OrdenAnalisis o = ordenDAO.buscarPorId(id);
        if (o != null) o.setAnalisisSolicitados(tipoDAO.listarPorOrden(o.getId()));
        return o;
    }

    public List<TipoAnalisis> listarCatalogoLaboratorio(int idLaboratorio) throws SQLException {
        return tipoDAO.listarPorLaboratorio(idLaboratorio);
    }

    public Resultado buscarResultado(int idOrden) throws SQLException {
        return resultadoDAO.buscarPorOrden(idOrden);
    }

    // ----------------------------------------------------------------
    // Creación de orden (CU-02)
    // ----------------------------------------------------------------

    public OrdenAnalisis crearOrden(int idPaciente, int idClinica, int idLaboratorio,
                                    int idVeterinario, String observaciones,
                                    List<Integer> idsTipoAnalisis) throws SQLException {
        if (idsTipoAnalisis == null || idsTipoAnalisis.isEmpty())
            throw new IllegalArgumentException("Debe seleccionar al menos un tipo de análisis.");

        OrdenAnalisis orden = new OrdenAnalisis();
        orden.setIdPaciente(idPaciente);
        orden.setIdClinica(idClinica);
        orden.setIdLaboratorio(idLaboratorio);
        orden.setIdVeterinario(idVeterinario);
        orden.setObservaciones(observaciones);
        orden.setEstado(EstadoOrden.PENDIENTE);

        int idOrden = ordenDAO.insertar(orden);
        ordenDAO.insertarItems(idOrden, idsTipoAnalisis);
        return orden;
    }

    // ----------------------------------------------------------------
    // Cambio de estado (CU-06)
    // ----------------------------------------------------------------

    /**
     * Cambia el estado de una orden aplicando las reglas de negocio.
     * Lanza IllegalStateException si la transición no es válida.
     */
    public void cambiarEstado(int idOrden, EstadoOrden nuevoEstado) throws SQLException {
        OrdenAnalisis orden = ordenDAO.buscarPorId(idOrden);
        if (orden == null)
            throw new IllegalArgumentException("No existe la orden con ID " + idOrden);

        EstadoOrden estadoActual = orden.getEstado();

        if (!validarTransicion(orden, nuevoEstado)) {
            throw new IllegalStateException(
                    "Transición inválida: " + estadoActual + " → " + nuevoEstado);
        }

        ordenDAO.actualizarEstado(idOrden, nuevoEstado);
    }

    /**
     * TODO(human): Implementá este método.
     *
     * Reglas acordadas para el MVP:
     *
     *  Flujo principal:  PENDIENTE → RECIBIDA → EN_PROCESO → FINALIZADA
     *
     *  Reglas especiales:
     *  1. RECIBIDA → FINALIZADA está permitido (saltar EN_PROCESO si el estudio fue rápido).
     *  2. EN_PROCESO → RECIBIDA está permitido SOLO si han pasado menos de 10 minutos
     *     desde el último cambio de estado (corrección de error reciente).
     *     Usá orden.getFechaUltimoCambio() y java.time.LocalDateTime.now().
     *  3. Cualquier estado activo (PENDIENTE, RECIBIDA, EN_PROCESO) puede ir a CANCELADA.
     *  4. FINALIZADA y CANCELADA son estados terminales: no permiten ningún cambio.
     *  5. No se permiten otros retrocesos ni saltos (ej: PENDIENTE → FINALIZADA está prohibido).
     */
    private boolean validarTransicion(OrdenAnalisis orden, EstadoOrden nuevoEstado) {
        // Tu código va aquí
        return false;
    }

    // ----------------------------------------------------------------
    // Carga de resultado (CU-07)
    // ----------------------------------------------------------------

    public Resultado cargarResultado(int idOrden, String observaciones,
                                     String rutaArchivo, int idTecnico) throws SQLException {
        OrdenAnalisis orden = ordenDAO.buscarPorId(idOrden);
        if (orden == null)
            throw new IllegalArgumentException("No existe la orden con ID " + idOrden);
        if (orden.getEstado() != EstadoOrden.FINALIZADA)
            throw new IllegalStateException("Solo se puede cargar resultado en órdenes FINALIZADAS.");
        if (resultadoDAO.buscarPorOrden(idOrden) != null)
            throw new IllegalStateException("La orden ya tiene un resultado cargado.");

        Resultado r = new Resultado();
        r.setIdOrden(idOrden);
        r.setObservaciones(observaciones);
        r.setRutaArchivo(rutaArchivo);
        r.setIdTecnico(idTecnico);
        resultadoDAO.insertar(r);
        return r;
    }
}
