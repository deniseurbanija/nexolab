package model;

public class TipoAnalisis {
    private int id;
    private String nombre;
    private String descripcion;
    private int idLaboratorio;

    public TipoAnalisis() {}

    public TipoAnalisis(int id, String nombre, String descripcion, int idLaboratorio) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.idLaboratorio = idLaboratorio;
    }

    public int getId()              { return id; }
    public String getNombre()       { return nombre; }
    public String getDescripcion()  { return descripcion; }
    public int getIdLaboratorio()   { return idLaboratorio; }

    public void setId(int id)                   { this.id = id; }
    public void setNombre(String nombre)        { this.nombre = nombre; }
    public void setDescripcion(String d)        { this.descripcion = d; }
    public void setIdLaboratorio(int idLab)     { this.idLaboratorio = idLab; }

    @Override
    public String toString() {
        return String.format("[%d] %s", id, nombre);
    }
}
