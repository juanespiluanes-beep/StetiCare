package modelo;

public class TipoServicio {
    private int id;
    private String nombre;
    private int duracionBaseMinutos;
    private int frecuenciaRecomendadaDias;
    private double precioBase;

    public TipoServicio(int id, String nombre, int duracionBaseMinutos,
                        int frecuenciaRecomendadaDias, double precioBase) {
        setId(id);
        setNombre(nombre);
        setDuracionBaseMinutos(duracionBaseMinutos);
        setFrecuenciaRecomendadaDias(frecuenciaRecomendadaDias);
        setPrecioBase(precioBase);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID del servicio debe ser mayor a cero.");
        }
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del servicio es obligatorio.");
        }
        this.nombre = nombre.trim();
    }

    public int getDuracionBaseMinutos() {
        return duracionBaseMinutos;
    }

    public void setDuracionBaseMinutos(int duracionBaseMinutos) {
        if (duracionBaseMinutos <= 0) {
            throw new IllegalArgumentException("La duración base debe ser mayor a 0 minutos.");
        }
        this.duracionBaseMinutos = duracionBaseMinutos;
    }

    public int getFrecuenciaRecomendadaDias() {
        return frecuenciaRecomendadaDias;
    }

    public void setFrecuenciaRecomendadaDias(int frecuenciaRecomendadaDias) {
        if (frecuenciaRecomendadaDias <= 0) {
            throw new IllegalArgumentException("La frecuencia recomendada debe ser mayor a 0 días.");
        }
        this.frecuenciaRecomendadaDias = frecuenciaRecomendadaDias;
    }

    public double getPrecioBase() {
        return precioBase;
    }

    public void setPrecioBase(double precioBase) {
        if (precioBase < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo.");
        }
        this.precioBase = precioBase;
    }

    @Override
    public String toString() {
        return String.format("ID: %d | %s | Base: %d min | Ciclo: cada %d días | $%.2f",
                id, nombre, duracionBaseMinutos, frecuenciaRecomendadaDias, precioBase);
    }
}
