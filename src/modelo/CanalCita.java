package modelo;

public enum CanalCita {
    LLAMADA("Llamada telefónica"),
    WHATSAPP("WhatsApp");

    private final String descripcion;

    CanalCita(String descripcion) {
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
