package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrdenAnalisis {
    private int id;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaUltimoCambio;
    private EstadoOrden estado;
    private String observaciones;
    private int idPaciente;
    private int idClinica;
    private int idLaboratorio;
    private int idVeterinario;

    // Datos para visualización (join desde BD)
    private String nombrePaciente;
    private String nombreLaboratorio;
    private String nombreVeterinario;
    private List<TipoAnalisis> analisisSolicitados = new ArrayList<>();

    public OrdenAnalisis() {}

    public OrdenAnalisis(int id, LocalDateTime fechaCreacion, EstadoOrden estado,
                         String observaciones, int idPaciente, int idClinica,
                         int idLaboratorio, int idVeterinario) {
        this.id = id;
        this.fechaCreacion = fechaCreacion;
        this.estado = estado;
        this.observaciones = observaciones;
        this.idPaciente = idPaciente;
        this.idClinica = idClinica;
        this.idLaboratorio = idLaboratorio;
        this.idVeterinario = idVeterinario;
    }

    public int getId()                    { return id; }
    public LocalDateTime getFechaCreacion()     { return fechaCreacion; }
    public LocalDateTime getFechaUltimoCambio() { return fechaUltimoCambio; }
    public EstadoOrden getEstado()              { return estado; }
    public String getObservaciones()      { return observaciones; }
    public int getIdPaciente()            { return idPaciente; }
    public int getIdClinica()             { return idClinica; }
    public int getIdLaboratorio()         { return idLaboratorio; }
    public int getIdVeterinario()         { return idVeterinario; }
    public String getNombrePaciente()     { return nombrePaciente; }
    public String getNombreLaboratorio()  { return nombreLaboratorio; }
    public String getNombreVeterinario()  { return nombreVeterinario; }
    public List<TipoAnalisis> getAnalisisSolicitados() { return analisisSolicitados; }

    public void setId(int id)                           { this.id = id; }
    public void setFechaCreacion(LocalDateTime f)        { this.fechaCreacion = f; }
    public void setFechaUltimoCambio(LocalDateTime f)   { this.fechaUltimoCambio = f; }
    public void setEstado(EstadoOrden estado)            { this.estado = estado; }
    public void setObservaciones(String o)              { this.observaciones = o; }
    public void setIdPaciente(int idPaciente)           { this.idPaciente = idPaciente; }
    public void setIdClinica(int idClinica)             { this.idClinica = idClinica; }
    public void setIdLaboratorio(int idLaboratorio)     { this.idLaboratorio = idLaboratorio; }
    public void setIdVeterinario(int idVeterinario)     { this.idVeterinario = idVeterinario; }
    public void setNombrePaciente(String n)             { this.nombrePaciente = n; }
    public void setNombreLaboratorio(String n)          { this.nombreLaboratorio = n; }
    public void setNombreVeterinario(String n)          { this.nombreVeterinario = n; }
    public void setAnalisisSolicitados(List<TipoAnalisis> lista) { this.analisisSolicitados = lista; }

    @Override
    public String toString() {
        return String.format("Orden #%d | %s | Estado: %s | Lab: %s | Fecha: %s",
                id,
                nombrePaciente != null ? nombrePaciente : "Paciente ID:" + idPaciente,
                estado,
                nombreLaboratorio != null ? nombreLaboratorio : "Lab ID:" + idLaboratorio,
                fechaCreacion != null ? fechaCreacion.toLocalDate().toString() : "S/D");
    }
}
