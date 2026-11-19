package modelo;

public enum EstadoCuidado {
    VERDE("VERDE (Al día)"),
    AMARILLO("AMARILLO (Por vencer)"),
    ROJO("ROJO (Vencido)"),
    SIN_HISTORIAL("SIN HISTORIAL");

    private final String descripcion;

    EstadoCuidado(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    @Override
    public String toString() {
        return descripcion;
    }
}
