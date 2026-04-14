package model;

public enum EstadoOrden {
    PENDIENTE, RECIBIDA, EN_PROCESO, FINALIZADA, CANCELADA;

    public static EstadoOrden fromString(String valor) {
        return EstadoOrden.valueOf(valor.toUpperCase());
    }

    @Override
    public String toString() {
        return name().replace("_", " ");
    }
}
