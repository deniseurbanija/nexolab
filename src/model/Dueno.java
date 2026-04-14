package model;

public class Dueno {
    private int id;
    private String nombre;
    private String apellido;
    private String telefono;
    private String email;
    private int idClinica;

    public Dueno() {}

    public Dueno(int id, String nombre, String apellido, String telefono, String email, int idClinica) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.email = email;
        this.idClinica = idClinica;
    }

    public int getId()          { return id; }
    public String getNombre()   { return nombre; }
    public String getApellido() { return apellido; }
    public String getTelefono() { return telefono; }
    public String getEmail()    { return email; }
    public int getIdClinica()   { return idClinica; }

    public void setId(int id)             { this.id = id; }
    public void setNombre(String nombre)  { this.nombre = nombre; }
    public void setApellido(String a)     { this.apellido = a; }
    public void setTelefono(String t)     { this.telefono = t; }
    public void setEmail(String e)        { this.email = e; }
    public void setIdClinica(int c)       { this.idClinica = c; }

    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }

    @Override
    public String toString() {
        return String.format("[%d] %s %s - %s", id, nombre, apellido, telefono);
    }
}
