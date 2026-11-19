package negocio;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import modelo.CanalCita;
import modelo.Cita;
import modelo.EstadoCita;
import modelo.Mascota;
import modelo.Peluquero;
import modelo.TipoServicio;


public class GestorAgenda {
    private final List<Cita> citas;
    private int secuenciaCita;

    public GestorAgenda() {
        this.citas = new ArrayList<>();
        this.secuenciaCita = 1;
    }


    public Cita programarCita(Mascota mascota, TipoServicio servicio, Peluquero peluquero,
                              LocalDate fecha, LocalTime horaInicio, CanalCita canal) {
        if (mascota == null || servicio == null || peluquero == null || fecha == null || horaInicio == null || canal == null) {
            throw new IllegalArgumentException("Todos los datos de la cita son obligatorios.");
        }

        LocalDate hoy = LocalDate.now();
        if (fecha.isBefore(hoy)) {
            throw new IllegalArgumentException("No se puede programar una cita en una fecha pasada (" + fecha + ").");
        }
        if (fecha.isEqual(hoy) && horaInicio.isBefore(LocalTime.now())) {
            throw new IllegalArgumentException("La hora de inicio (" + horaInicio + ") ya pasó para el día de hoy.");
        }

        int duracionTotalMin = servicio.getDuracionBaseMinutos() + mascota.getMinutosAdicionalesAtencion();
        LocalTime horaFin = horaInicio.plusMinutes(duracionTotalMin);

        // RF5: Regla de negocio de detección de cruces y jornada
        validarDisponibilidad(peluquero, fecha, horaInicio, duracionTotalMin);

        Cita nuevaCita = new Cita(secuenciaCita++, mascota, servicio, peluquero, fecha, horaInicio, horaFin, canal);
        citas.add(nuevaCita);
        return nuevaCita;
    }


    public Cita registrarCitaPrecargada(Mascota mascota, TipoServicio servicio, Peluquero peluquero,
                                        LocalDate fecha, LocalTime horaInicio, CanalCita canal) {
        int duracionTotalMin = servicio.getDuracionBaseMinutos() + mascota.getMinutosAdicionalesAtencion();
        LocalTime horaFin = horaInicio.plusMinutes(duracionTotalMin);
        validarDisponibilidad(peluquero, fecha, horaInicio, duracionTotalMin);

        Cita nuevaCita = new Cita(secuenciaCita++, mascota, servicio, peluquero, fecha, horaInicio, horaFin, canal);
        citas.add(nuevaCita);
        return nuevaCita;
    }

    public void validarDisponibilidad(Peluquero peluquero, LocalDate fecha,
                                      LocalTime horaInicio, int duracionMinutos) {
        if (peluquero == null || fecha == null || horaInicio == null) {
            throw new IllegalArgumentException("Peluquero, fecha y hora de inicio son obligatorios.");
        }
        if (duracionMinutos <= 0) {
            throw new IllegalArgumentException("La duración estimada debe ser mayor a 0 minutos.");
        }

        LocalTime horaFin = horaInicio.plusMinutes(duracionMinutos);

        if (!peluquero.trabajaElDia(fecha.getDayOfWeek())) {
            throw new IllegalStateException("Cita rechazada: El peluquero " + peluquero.getNombreCompleto()
                    + " no labora el día " + fecha + " (días laborales: " + peluquero.getDiasTrabajoFormateado()
                    + "). Consulte los horarios libres (RF13).");
        }

        if (horaInicio.isBefore(peluquero.getHoraEntrada()) || horaFin.isAfter(peluquero.getHoraSalida())) {
            throw new IllegalStateException("Cita rechazada: El horario solicitado (" + horaInicio + " - " + horaFin
                    + ") está fuera de la jornada de " + peluquero.getNombreCompleto()
                    + " (" + peluquero.getHoraEntrada() + " - " + peluquero.getHoraSalida()
                    + "). Consulte los horarios libres (RF13).");
        }

        for (Cita existente : citas) {
            if (existente.getPeluquero().getId() == peluquero.getId()
                    && existente.getFecha().equals(fecha)
                    && existente.isActiva()) {

                // Hay superposición si: inicioNueva < finExistente && finNueva > inicioExistente
                boolean hayChoque = horaInicio.isBefore(existente.getHoraFin())
                        && horaFin.isAfter(existente.getHoraInicio());

                if (hayChoque) {
                    throw new IllegalStateException(String.format(
                            "Cita rechazada por cruce de horario: Choca con la Cita #%d de '%s' (%s - %s) asignada a %s. " +
                            "Consulte los horarios libres disponibles (RF13).",
                            existente.getId(), existente.getMascota().getNombre(),
                            existente.getHoraInicio(), existente.getHoraFin(), peluquero.getNombreCompleto()));
                }
            }
        }
    }


    public Cita actualizarEstadoCita(int idCita, EstadoCita nuevoEstado, String motivo) {
        Cita cita = buscarCitaPorId(idCita);
        if (cita == null) {
            throw new IllegalArgumentException("No se encontró ninguna cita con el ID #" + idCita + ".");
        }
        if (nuevoEstado == null) {
            throw new IllegalArgumentException("Debe indicar un nuevo estado válido.");
        }

        if (cita.getEstado() != EstadoCita.PROGRAMADA) {
            throw new IllegalStateException("Transición no válida: La Cita #" + idCita
                    + " se encuentra en estado '" + cita.getEstado()
                    + "' y no puede ser modificada.");
        }

        if (nuevoEstado != EstadoCita.CANCELADA && nuevoEstado != EstadoCita.NO_ASISTIO) {
            throw new IllegalArgumentException("Desde este módulo solo se puede cambiar el estado a 'Cancelada' o 'No asistió' "
                    + "(para marcar como 'Atendida', utilice RF8 - Registrar servicio realizado).");
        }

        if (nuevoEstado == EstadoCita.NO_ASISTIO) {
            LocalDateTime inicioCita = LocalDateTime.of(cita.getFecha(), cita.getHoraInicio());
            if (!LocalDateTime.now().isAfter(inicioCita)) {
                throw new IllegalStateException("Transición no válida: Solo se puede marcar 'No asistió' después de la hora de inicio de la cita ("
                        + cita.getFecha() + " " + cita.getHoraInicio() + ").");
            }
        }

        cita.setEstado(nuevoEstado);
        cita.setMotivoCambioEstado(motivo);
        return cita;
    }


    public List<Cita> consultarAgendaDelDia(LocalDate fecha, Integer idPeluqueroOpcional) {
        if (fecha == null) {
            throw new IllegalArgumentException("La fecha de consulta es obligatoria.");
        }

        List<Cita> resultado = new ArrayList<>();
        for (Cita c : citas) {
            if (c.getFecha().equals(fecha)) {
                if (idPeluqueroOpcional == null || c.getPeluquero().getId() == idPeluqueroOpcional) {
                    resultado.add(c);
                }
            }
        }

        resultado.sort(Comparator.comparing(Cita::getHoraInicio)
                .thenComparing(c -> c.getPeluquero().getNombreCompleto()));
        return resultado;
    }

    public List<Cita> obtenerCitasActivasPeluqueroFecha(int idPeluquero, LocalDate fecha) {
        List<Cita> activas = new ArrayList<>();
        for (Cita c : citas) {
            if (c.getPeluquero().getId() == idPeluquero
                    && c.getFecha().equals(fecha)
                    && c.isActiva()) {
                activas.add(c);
            }
        }
        return activas;
    }

    public Cita buscarCitaPorId(int idCita) {
        for (Cita c : citas) {
            if (c.getId() == idCita) {
                return c;
            }
        }
        return null;
    }

    public List<Cita> listarCitasProgramadas() {
        List<Cita> programadas = new ArrayList<>();
        for (Cita c : citas) {
            if (c.getEstado() == EstadoCita.PROGRAMADA) {
                programadas.add(c);
            }
        }
        programadas.sort(Comparator.comparing(Cita::getFecha).thenComparing(Cita::getHoraInicio));
        return programadas;
    }

    public List<Cita> listarTodasLasCitas() {
        return Collections.unmodifiableList(citas);
    }
}
