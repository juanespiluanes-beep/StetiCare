package modelo;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public class Peluquero {
    private int id;
    private String nombreCompleto;
    private final Set<DayOfWeek> diasTrabajo;
    private LocalTime horaEntrada;
    private LocalTime horaSalida;

    public Peluquero(int id, String nombreCompleto, Set<DayOfWeek> diasTrabajo,
                     LocalTime horaEntrada, LocalTime horaSalida) {
        setId(id);
        setNombreCompleto(nombreCompleto);
        if (diasTrabajo == null || diasTrabajo.isEmpty()) {
            throw new IllegalArgumentException("Debe seleccionar al menos un día de trabajo para el peluquero.");
        }
        this.diasTrabajo = EnumSet.copyOf(diasTrabajo);
        setHorario(horaEntrada, horaSalida);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID del peluquero debe ser mayor a cero.");
        }
        this.id = id;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        if (nombreCompleto == null || nombreCompleto.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del peluquero es obligatorio.");
        }
        this.nombreCompleto = nombreCompleto.trim();
    }

    public Set<DayOfWeek> getDiasTrabajo() {
        return Collections.unmodifiableSet(diasTrabajo);
    }

    public LocalTime getHoraEntrada() {
        return horaEntrada;
    }

    public LocalTime getHoraSalida() {
        return horaSalida;
    }

    public void setHorario(LocalTime horaEntrada, LocalTime horaSalida) {
        if (horaEntrada == null || horaSalida == null) {
            throw new IllegalArgumentException("Las horas de entrada y salida son obligatorias.");
        }
        if (!horaSalida.isAfter(horaEntrada)) {
            throw new IllegalArgumentException("La hora de salida (" + horaSalida
                    + ") debe ser posterior a la hora de entrada (" + horaEntrada + ").");
        }
        this.horaEntrada = horaEntrada;
        this.horaSalida = horaSalida;
    }

    public boolean trabajaElDia(DayOfWeek dia) {
        return diasTrabajo.contains(dia);
    }

    public long getMinutosJornada() {
        return ChronoUnit.MINUTES.between(horaEntrada, horaSalida);
    }

    public String getDiasTrabajoFormateado() {
        Locale es = new Locale("es", "EC");
        List<DayOfWeek> ordenados = new ArrayList<>(diasTrabajo);
        ordenados.sort(Comparator.comparingInt(DayOfWeek::getValue));
        return ordenados.stream()
                .map(d -> d.getDisplayName(TextStyle.SHORT, es).toUpperCase())
                .collect(Collectors.joining(", "));
    }

    @Override
    public String toString() {
        return String.format("ID: %d | %s | Días: [%s] | Jornada: %s - %s (%d min)",
                id, nombreCompleto, getDiasTrabajoFormateado(), horaEntrada, horaSalida, getMinutosJornada());
    }
}
