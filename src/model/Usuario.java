package model;

public class Usuario {
    private int id;
    private String username;
    private String passwordHash;
    private String nombreCompleto;
    private Rol rol;
    private Integer idClinica;
    private Integer idLaboratorio;
    private boolean activo;

    public Usuario() {}

    public Usuario(int id, String username, String passwordHash, String nombreCompleto,
                   Rol rol, Integer idClinica, Integer idLaboratorio, boolean activo) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.nombreCompleto = nombreCompleto;
        this.rol = rol;
        this.idClinica = idClinica;
        this.idLaboratorio = idLaboratorio;
        this.activo = activo;
    }

    public int getId()                  { return id; }
    public String getUsername()         { return username; }
    public String getPasswordHash()     { return passwordHash; }
    public String getNombreCompleto()   { return nombreCompleto; }
    public Rol getRol()                 { return rol; }
    public Integer getIdClinica()       { return idClinica; }
    public Integer getIdLaboratorio()   { return idLaboratorio; }
    public boolean isActivo()           { return activo; }

    public void setId(int id)                         { this.id = id; }
    public void setUsername(String username)           { this.username = username; }
    public void setPasswordHash(String passwordHash)   { this.passwordHash = passwordHash; }
    public void setNombreCompleto(String n)            { this.nombreCompleto = n; }
    public void setRol(Rol rol)                        { this.rol = rol; }
    public void setIdClinica(Integer idClinica)        { this.idClinica = idClinica; }
    public void setIdLaboratorio(Integer idLab)        { this.idLaboratorio = idLab; }
    public void setActivo(boolean activo)              { this.activo = activo; }

    @Override
    public String toString() {
        return String.format("[%d] %s (%s) - %s", id, nombreCompleto, username, rol);
    }
}
