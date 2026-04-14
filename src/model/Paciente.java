package model;

import java.time.LocalDate;

public class Paciente {
    private int id;
    private String nombre;
    private String especie;
    private String raza;
    private LocalDate fechaNacimiento;
    private int idDueno;
    private int idClinica;

    // Datos denormalizados para visualización (no persisten en BD)
    private String nombreDueno;

    public Paciente() {}

    public Paciente(int id, String nombre, String especie, String raza,
                    LocalDate fechaNacimiento, int idDueno, int idClinica) {
        this.id = id;
        this.nombre = nombre;
        this.especie = especie;
        this.raza = raza;
        this.fechaNacimiento = fechaNacimiento;
        this.idDueno = idDueno;
        this.idClinica = idClinica;
    }

    public int getId()                    { return id; }
    public String getNombre()             { return nombre; }
    public String getEspecie()            { return especie; }
    public String getRaza()               { return raza; }
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public int getIdDueno()               { return idDueno; }
    public int getIdClinica()             { return idClinica; }
    public String getNombreDueno()        { return nombreDueno; }

    public void setId(int id)                          { this.id = id; }
    public void setNombre(String nombre)               { this.nombre = nombre; }
    public void setEspecie(String especie)             { this.especie = especie; }
    public void setRaza(String raza)                   { this.raza = raza; }
    public void setFechaNacimiento(LocalDate f)        { this.fechaNacimiento = f; }
    public void setIdDueno(int idDueno)                { this.idDueno = idDueno; }
    public void setIdClinica(int idClinica)            { this.idClinica = idClinica; }
    public void setNombreDueno(String nombreDueno)     { this.nombreDueno = nombreDueno; }

    @Override
    public String toString() {
        return String.format("[%d] %s (%s - %s) | Dueño: %s",
                id, nombre, especie, raza != null ? raza : "S/D",
                nombreDueno != null ? nombreDueno : "ID:" + idDueno);
    }
}
