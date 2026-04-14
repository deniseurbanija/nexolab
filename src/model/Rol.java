package model;

public enum Rol {
    ADMIN, VETERINARIO, LABORATORIO;

    public static Rol fromString(String valor) {
        return Rol.valueOf(valor.toUpperCase());
    }
}
