package model;

import java.time.LocalDateTime;

public class Resultado {
    private int id;
    private int idOrden;
    private LocalDateTime fechaCarga;
    private String observaciones;
    private String rutaArchivo;
    private int idTecnico;

    // Para visualización
    private String nombreTecnico;

    public Resultado() {}

    public Resultado(int id, int idOrden, LocalDateTime fechaCarga,
                     String observaciones, String rutaArchivo, int idTecnico) {
        this.id = id;
        this.idOrden = idOrden;
        this.fechaCarga = fechaCarga;
        this.observaciones = observaciones;
        this.rutaArchivo = rutaArchivo;
        this.idTecnico = idTecnico;
    }

    public int getId()                    { return id; }
    public int getIdOrden()               { return idOrden; }
    public LocalDateTime getFechaCarga()  { return fechaCarga; }
    public String getObservaciones()      { return observaciones; }
    public String getRutaArchivo()        { return rutaArchivo; }
    public int getIdTecnico()             { return idTecnico; }
    public String getNombreTecnico()      { return nombreTecnico; }

    public void setId(int id)                       { this.id = id; }
    public void setIdOrden(int idOrden)             { this.idOrden = idOrden; }
    public void setFechaCarga(LocalDateTime f)      { this.fechaCarga = f; }
    public void setObservaciones(String o)          { this.observaciones = o; }
    public void setRutaArchivo(String r)            { this.rutaArchivo = r; }
    public void setIdTecnico(int idTecnico)         { this.idTecnico = idTecnico; }
    public void setNombreTecnico(String n)          { this.nombreTecnico = n; }

    @Override
    public String toString() {
        return String.format("Resultado [Orden #%d] | Técnico: %s | Fecha: %s | Archivo: %s",
                idOrden,
                nombreTecnico != null ? nombreTecnico : "ID:" + idTecnico,
                fechaCarga != null ? fechaCarga.toLocalDate().toString() : "S/D",
                rutaArchivo != null ? rutaArchivo : "Sin archivo");
    }
}
