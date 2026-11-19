package modelo;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public class Cita {
    private int id;
    private Mascota mascota;
    private TipoServicio servicio;
    private Peluquero peluquero;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private CanalCita canal;
    private EstadoCita estado;
    private String motivoCambioEstado;

    public Cita(int id, Mascota mascota, TipoServicio servicio, Peluquero peluquero,
                LocalDate fecha, LocalTime horaInicio, LocalTime horaFin, CanalCita canal) {
        setId(id);
        setMascota(mascota);
        setServicio(servicio);
        setPeluquero(peluquero);
        setFecha(fecha);
        setHorario(horaInicio, horaFin);
        setCanal(canal);
        this.estado = EstadoCita.PROGRAMADA;
        this.motivoCambioEstado = "";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID de la cita debe ser mayor a cero.");
        }
        this.id = id;
    }

    public Mascota getMascota() {
        return mascota;
    }

    public void setMascota(Mascota mascota) {
        if (mascota == null) {
            throw new IllegalArgumentException("La mascota es obligatoria para programar una cita.");
        }
        this.mascota = mascota;
    }

    public TipoServicio getServicio() {
        return servicio;
    }

    public void setServicio(TipoServicio servicio) {
        if (servicio == null) {
            throw new IllegalArgumentException("El servicio es obligatorio para programar una cita.");
        }
        this.servicio = servicio;
    }

    public Peluquero getPeluquero() {
        return peluquero;
    }

    public void setPeluquero(Peluquero peluquero) {
        if (peluquero == null) {
            throw new IllegalArgumentException("El peluquero es obligatorio para programar una cita.");
        }
        this.peluquero = peluquero;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        if (fecha == null) {
            throw new IllegalArgumentException("La fecha de la cita es obligatoria.");
        }
        this.fecha = fecha;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public void setHorario(LocalTime horaInicio, LocalTime horaFin) {
        if (horaInicio == null || horaFin == null) {
            throw new IllegalArgumentException("La hora de inicio y fin son obligatorias.");
        }
        if (!horaFin.isAfter(horaInicio)) {
            throw new IllegalArgumentException("La hora de fin debe ser posterior a la hora de inicio.");
        }
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
    }

    public CanalCita getCanal() {
        return canal;
    }

    public void setCanal(CanalCita canal) {
        if (canal == null) {
            throw new IllegalArgumentException("El canal de solicitud (Llamada o WhatsApp) es obligatorio.");
        }
        this.canal = canal;
    }

    public EstadoCita getEstado() {
        return estado;
    }

    public void setEstado(EstadoCita estado) {
        if (estado == null) {
            throw new IllegalArgumentException("El estado de la cita no puede ser nulo.");
        }
        this.estado = estado;
    }

    public String getMotivoCambioEstado() {
        return motivoCambioEstado;
    }

    public void setMotivoCambioEstado(String motivoCambioEstado) {
        this.motivoCambioEstado = (motivoCambioEstado == null) ? "" : motivoCambioEstado.trim();
    }

    public boolean isActiva() {
        return estado == EstadoCita.PROGRAMADA || estado == EstadoCita.ATENDIDA;
    }

    public long getDuracionMinutos() {
        return ChronoUnit.MINUTES.between(horaInicio, horaFin);
    }

    @Override
    public String toString() {
        String extraMotivo = motivoCambioEstado.isEmpty() ? "" : " | Motivo: " + motivoCambioEstado;
        return String.format("Cita #%d | %s %s-%s (%d min) | Mascota: %s (Dueño: %s) | Servicio: %s | Peluquero: %s | Canal: %s | Estado: %s%s",
                id, fecha, horaInicio, horaFin, getDuracionMinutos(),
                mascota.getNombre(), mascota.getDueno().getNombreCompleto(),
                servicio.getNombre(), peluquero.getNombreCompleto(), canal, estado, extraMotivo);
    }
}
