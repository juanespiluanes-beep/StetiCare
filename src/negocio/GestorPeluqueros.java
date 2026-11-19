package negocio;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import modelo.Cita;
import modelo.Peluquero;


public class GestorPeluqueros {
    private final List<Peluquero> peluqueros;
    private final ParametrosConfiguracion config;
    private int secuenciaPeluquero;

    public GestorPeluqueros(ParametrosConfiguracion config) {
        this.peluqueros = new ArrayList<>();
        this.config = config;
        this.secuenciaPeluquero = 1;
    }


    public static class ReporteOcupacion {
        public final Peluquero peluquero;
        public final LocalDate fecha;
        public final boolean trabajaEseDia;
        public final long minutosOcupados;
        public final long minutosJornada;
        public final double porcentajeOcupacion;
        public final String estadoJornada;

        public ReporteOcupacion(Peluquero peluquero, LocalDate fecha, boolean trabajaEseDia,
                                long minutosOcupados, long minutosJornada,
                                double porcentajeOcupacion, String estadoJornada) {
            this.peluquero = peluquero;
            this.fecha = fecha;
            this.trabajaEseDia = trabajaEseDia;
            this.minutosOcupados = minutosOcupados;
            this.minutosJornada = minutosJornada;
            this.porcentajeOcupacion = porcentajeOcupacion;
            this.estadoJornada = estadoJornada;
        }
    }

  
    public Peluquero registrarPeluquero(String nombreCompleto, Set<DayOfWeek> diasTrabajo,
                                        LocalTime horaEntrada, LocalTime horaSalida) {
        Peluquero nuevo = new Peluquero(secuenciaPeluquero++, nombreCompleto, diasTrabajo, horaEntrada, horaSalida);
        peluqueros.add(nuevo);
        return nuevo;
    }

 
    public ReporteOcupacion calcularOcupacionJornada(int idPeluquero, LocalDate fecha, List<Cita> citasActivasDia) {
        Peluquero peluquero = buscarPeluqueroPorId(idPeluquero);
        if (peluquero == null) {
            throw new IllegalArgumentException("No existe un peluquero con ID " + idPeluquero + ".");
        }
        if (fecha == null) {
            throw new IllegalArgumentException("La fecha es obligatoria.");
        }

        if (!peluquero.trabajaElDia(fecha.getDayOfWeek())) {
            return new ReporteOcupacion(peluquero, fecha, false, 0, 0, 0.0, "No labora este día");
        }

        long minutosJornada = peluquero.getMinutosJornada();
        long minutosOcupados = 0;
        for (Cita c : citasActivasDia) {
            if (c.isActiva()) {
                minutosOcupados += c.getDuracionMinutos();
            }
        }

        double porcentaje = (minutosJornada > 0) ? (minutosOcupados * 100.0) / minutosJornada : 0.0;
        String estado = config.evaluarEstadoOcupacion(porcentaje);

        return new ReporteOcupacion(peluquero, fecha, true, minutosOcupados, minutosJornada, porcentaje, estado);
    }


    public List<String> consultarHorariosLibres(int idPeluquero, LocalDate fecha,
                                                int duracionRequeridaMin, List<Cita> citasActivasDia) {
        Peluquero peluquero = buscarPeluqueroPorId(idPeluquero);
        if (peluquero == null) {
            throw new IllegalArgumentException("No existe un peluquero con ID " + idPeluquero + ".");
        }
        if (fecha == null) {
            throw new IllegalArgumentException("La fecha es obligatoria.");
        }
        if (duracionRequeridaMin <= 0) {
            throw new IllegalArgumentException("La duración requerida debe ser mayor a 0 minutos.");
        }

        if (!peluquero.trabajaElDia(fecha.getDayOfWeek())) {
            return Collections.emptyList();
        }

        List<Cita> ordenadas = new ArrayList<>();
        for (Cita c : citasActivasDia) {
            if (c.isActiva()) {
                ordenadas.add(c);
            }
        }
        ordenadas.sort(Comparator.comparing(Cita::getHoraInicio));

        List<String> opcionesHorarios = new ArrayList<>();
        LocalTime cursor = peluquero.getHoraEntrada();
        LocalTime finJornada = peluquero.getHoraSalida();

        for (Cita cita : ordenadas) {
            if (cita.getHoraInicio().isAfter(cursor)) {
                agregarBloquesDisponibles(cursor, cita.getHoraInicio(), duracionRequeridaMin, opcionesHorarios);
            }
            if (cita.getHoraFin().isAfter(cursor)) {
                cursor = cita.getHoraFin();
            }
        }

        if (finJornada.isAfter(cursor)) {
            agregarBloquesDisponibles(cursor, finJornada, duracionRequeridaMin, opcionesHorarios);
        }

        return opcionesHorarios;
    }

    private void agregarBloquesDisponibles(LocalTime inicioHueco, LocalTime finHueco,
                                           int duracionRequeridaMin, List<String> resultados) {
        long minutosDisponibles = ChronoUnit.MINUTES.between(inicioHueco, finHueco);
        if (minutosDisponibles < duracionRequeridaMin) {
            return;
        }

        LocalTime ultimoInicioPosible = finHueco.minusMinutes(duracionRequeridaMin);
        List<String> iniciosSugeridos = new ArrayList<>();

        LocalTime candidato = inicioHueco;
        while (!candidato.isAfter(ultimoInicioPosible)) {
            iniciosSugeridos.add(candidato.toString());
            candidato = candidato.plusMinutes(30);
        }

        resultados.add(String.format("Tramo libre %s - %s (%d min disponibles) -> Horas de inicio posibles: %s",
                inicioHueco, finHueco, minutosDisponibles, String.join(", ", iniciosSugeridos)));
    }

    public Peluquero buscarPeluqueroPorId(int idPeluquero) {
        for (Peluquero p : peluqueros) {
            if (p.getId() == idPeluquero) {
                return p;
            }
        }
        return null;
    }

    public List<Peluquero> listarPeluqueros() {
        return Collections.unmodifiableList(peluqueros);
    }
}
