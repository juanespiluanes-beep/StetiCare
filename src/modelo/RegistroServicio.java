package modelo;

import java.time.LocalDate;

public class RegistroServicio {
    private int id;
    private Mascota mascota;
    private TipoServicio servicio;
    private Peluquero peluquero;
    private LocalDate fechaRealizacion;
    private String observaciones;

    public RegistroServicio(int id, Mascota mascota, TipoServicio servicio,
                            Peluquero peluquero, LocalDate fechaRealizacion, String observaciones) {
        setId(id);
        setMascota(mascota);
        setServicio(servicio);
        setPeluquero(peluquero);
        setFechaRealizacion(fechaRealizacion);
        setObservaciones(observaciones);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID del registro debe ser mayor a cero.");
        }
        this.id = id;
    }

    public Mascota getMascota() {
        return mascota;
    }

    public void setMascota(Mascota mascota) {
        if (mascota == null) {
            throw new IllegalArgumentException("La mascota es obligatoria.");
        }
        this.mascota = mascota;
    }

    public TipoServicio getServicio() {
        return servicio;
    }

    public void setServicio(TipoServicio servicio) {
        if (servicio == null) {
            throw new IllegalArgumentException("El tipo de servicio es obligatorio.");
        }
        this.servicio = servicio;
    }

    public Peluquero getPeluquero() {
        return peluquero;
    }

    public void setPeluquero(Peluquero peluquero) {
        if (peluquero == null) {
            throw new IllegalArgumentException("El peluquero es obligatorio.");
        }
        this.peluquero = peluquero;
    }

    public LocalDate getFechaRealizacion() {
        return fechaRealizacion;
    }

    public void setFechaRealizacion(LocalDate fechaRealizacion) {
        if (fechaRealizacion == null) {
            throw new IllegalArgumentException("La fecha de realización es obligatoria.");
        }
        this.fechaRealizacion = fechaRealizacion;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = (observaciones == null || observaciones.trim().isEmpty())
                ? "Sin observaciones"
                : observaciones.trim();
    }

    @Override
    public String toString() {
        return String.format("[%s] %s | Atendido por: %s | Obs: %s",
                fechaRealizacion, servicio.getNombre(), peluquero.getNombreCompleto(), observaciones);
    }
}
