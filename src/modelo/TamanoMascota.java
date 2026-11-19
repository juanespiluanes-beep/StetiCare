package modelo;

public enum TamanoMascota {
    PEQUENA("Pequeña"),
    MEDIANA("Mediana"),
    GRANDE("Grande");

    private final String descripcion;

    TamanoMascota(String descripcion) {
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
