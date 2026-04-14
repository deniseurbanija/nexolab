package model;

public class Laboratorio {
    private int id;
    private String nombre;
    private String direccion;
    private String telefono;
    private String email;
    private boolean activo;

    public Laboratorio() {}

    public Laboratorio(int id, String nombre, String direccion, String telefono, String email, boolean activo) {
        this.id = id;
        this.nombre = nombre;
        this.direccion = direccion;
        this.telefono = telefono;
        this.email = email;
        this.activo = activo;
    }

    public int getId()           { return id; }
    public String getNombre()    { return nombre; }
    public String getDireccion() { return direccion; }
    public String getTelefono()  { return telefono; }
    public String getEmail()     { return email; }
    public boolean isActivo()    { return activo; }

    public void setId(int id)             { this.id = id; }
    public void setNombre(String nombre)  { this.nombre = nombre; }
    public void setDireccion(String d)    { this.direccion = d; }
    public void setTelefono(String t)     { this.telefono = t; }
    public void setEmail(String e)        { this.email = e; }
    public void setActivo(boolean activo) { this.activo = activo; }

    @Override
    public String toString() {
        return String.format("[%d] %s - %s", id, nombre, telefono);
    }
}
