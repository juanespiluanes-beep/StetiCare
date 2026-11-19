package negocio;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import modelo.Cita;
import modelo.EstadoCita;
import modelo.EstadoCuidado;
import modelo.Mascota;
import modelo.RegistroServicio;
import modelo.TipoServicio;


public class GestorServicios {
    private final List<TipoServicio> catalogoServicios;
    private final List<RegistroServicio> historialGlobal;
    private final ParametrosConfiguracion config;
    private int secuenciaServicio;
    private int secuenciaRegistro;

    public GestorServicios(ParametrosConfiguracion config) {
        this.catalogoServicios = new ArrayList<>();
        this.historialGlobal = new ArrayList<>();
        this.config = config;
        this.secuenciaServicio = 1;
        this.secuenciaRegistro = 1;
    }


    public static class EstadoProximoServicio {
        public final Mascota mascota;
        public final RegistroServicio ultimoServicio;
        public final LocalDate fechaProximoServicio;
        public final long diasRestantes;
        public final EstadoCuidado estadoCuidado;

        public EstadoProximoServicio(Mascota mascota, RegistroServicio ultimoServicio,
                                     LocalDate fechaProximoServicio, long diasRestantes,
                                     EstadoCuidado estadoCuidado) {
            this.mascota = mascota;
            this.ultimoServicio = ultimoServicio;
            this.fechaProximoServicio = fechaProximoServicio;
            this.diasRestantes = diasRestantes;
            this.estadoCuidado = estadoCuidado;
        }
    }

    public TipoServicio registrarTipoServicio(String nombre, int duracionBaseMin,
                                              int frecuenciaDias, double precioBase) {
        TipoServicio servicio = new TipoServicio(secuenciaServicio++, nombre, duracionBaseMin, frecuenciaDias, precioBase);
        catalogoServicios.add(servicio);
        return servicio;
    }


    public RegistroServicio registrarServicioRealizado(Cita cita, String observaciones) {
        if (cita == null) {
            throw new IllegalArgumentException("La cita seleccionada no existe.");
        }
        if (cita.getEstado() != EstadoCita.PROGRAMADA) {
            throw new IllegalStateException("Solo se puede registrar un servicio sobre una cita en estado 'Programada'. "
                    + "Estado actual: '" + cita.getEstado() + "'.");
        }
        if (cita.getFecha().isAfter(LocalDate.now())) {
            throw new IllegalStateException("No se puede registrar como atendida una cita cuya fecha es futura ("
                    + cita.getFecha() + ").");
        }

        cita.setEstado(EstadoCita.ATENDIDA);

        RegistroServicio registro = new RegistroServicio(
                secuenciaRegistro++,
                cita.getMascota(),
                cita.getServicio(),
                cita.getPeluquero(),
                cita.getFecha(),
                observaciones
        );

        cita.getMascota().agregarRegistroServicio(registro);
        historialGlobal.add(registro);
        return registro;
    }


    public EstadoProximoServicio calcularEstadoProximoServicio(Mascota mascota, LocalDate fechaConsulta) {
        if (mascota == null) {
            throw new IllegalArgumentException("La mascota es obligatoria.");
        }
        if (fechaConsulta == null) {
            fechaConsulta = LocalDate.now();
        }

        RegistroServicio ultimo = mascota.getUltimoServicio();
        if (ultimo == null) {
            return new EstadoProximoServicio(mascota, null, null, 0, EstadoCuidado.SIN_HISTORIAL);
        }

        int plazoDias = ultimo.getServicio().getFrecuenciaRecomendadaDias();
        LocalDate fechaProximo = ultimo.getFechaRealizacion().plusDays(plazoDias);
        long diasRestantes = ChronoUnit.DAYS.between(fechaConsulta, fechaProximo);
        EstadoCuidado estado = config.evaluarEstadoCuidado(diasRestantes);

        return new EstadoProximoServicio(mascota, ultimo, fechaProximo, diasRestantes, estado);
    }


    public List<EstadoProximoServicio> consultarMascotasPorVencer(List<Mascota> mascotas,
                                                                  String filtro, LocalDate fechaConsulta) {
        if (fechaConsulta == null) {
            fechaConsulta = LocalDate.now();
        }
        String filtroNorm = (filtro == null) ? "AMBOS" : filtro.trim().toUpperCase();

        List<EstadoProximoServicio> pendientes = new ArrayList<>();
        for (Mascota m : mascotas) {
            EstadoProximoServicio eval = calcularEstadoProximoServicio(m, fechaConsulta);
            if (eval.estadoCuidado == EstadoCuidado.SIN_HISTORIAL || eval.estadoCuidado == EstadoCuidado.VERDE) {
                continue;
            }

            boolean incluir = switch (filtroNorm) {
                case "AMARILLO" -> eval.estadoCuidado == EstadoCuidado.AMARILLO;
                case "ROJO" -> eval.estadoCuidado == EstadoCuidado.ROJO;
                case "AMBOS" -> (eval.estadoCuidado == EstadoCuidado.AMARILLO
                        || eval.estadoCuidado == EstadoCuidado.ROJO);
                default -> throw new IllegalArgumentException("Filtro inválido. Use AMARILLO, ROJO o AMBOS.");
            };

            if (incluir) {
                pendientes.add(eval);
            }
        }

        
        pendientes.sort(Comparator.comparingLong(e -> e.diasRestantes));
        return pendientes;
    }

    public TipoServicio buscarServicioPorId(int idServicio) {
        for (TipoServicio s : catalogoServicios) {
            if (s.getId() == idServicio) {
                return s;
            }
        }
        return null;
    }

    public List<TipoServicio> listarCatalogoServicios() {
        return Collections.unmodifiableList(catalogoServicios);
    }
}
