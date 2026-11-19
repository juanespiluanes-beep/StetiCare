package modelo;

public enum TipoPelaje {
    CORTO("Corto"),
    MEDIO("Medio"),
    LARGO("Largo");

    private final String descripcion;

    TipoPelaje(String descripcion) {
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
