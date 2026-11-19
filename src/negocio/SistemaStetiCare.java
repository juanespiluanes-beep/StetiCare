package negocio;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.EnumSet;
import java.util.List;
import modelo.CanalCita;
import modelo.Cita;
import modelo.Dueno;
import modelo.Mascota;
import modelo.Peluquero;
import modelo.TipoPelaje;
import modelo.TipoServicio;
import negocio.GestorClientes.DatosNuevaMascota;


public class SistemaStetiCare {
    private final ParametrosConfiguracion configuracion;
    private final GestorClientes gestorClientes;
    private final GestorPeluqueros gestorPeluqueros;
    private final GestorAgenda gestorAgenda;
    private final GestorServicios gestorServicios;

    public SistemaStetiCare() {
        this.configuracion = new ParametrosConfiguracion();
        this.gestorClientes = new GestorClientes(configuracion);
        this.gestorPeluqueros = new GestorPeluqueros(configuracion);
        this.gestorAgenda = new GestorAgenda();
        this.gestorServicios = new GestorServicios(configuracion);
        cargarDatosIniciales();
    }

    private void cargarDatosIniciales() {
        
        TipoServicio banoBasico = gestorServicios.registrarTipoServicio("Baño básico y secado", 45, 30, 15.00);
        TipoServicio banoYCorte = gestorServicios.registrarTipoServicio("Baño y corte completo", 60, 45, 25.00);
        TipoServicio deslanado = gestorServicios.registrarTipoServicio("Deslanado y cepillado profundo", 75, 60, 30.00);
        TipoServicio corteHigienico = gestorServicios.registrarTipoServicio("Corte higiénico y uñas", 30, 30, 12.00);

        
        Peluquero p1 = gestorPeluqueros.registrarPeluquero(
                "Camila Torres",
                EnumSet.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
                        DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY),
                LocalTime.of(8, 0),
                LocalTime.of(17, 0)
        );

        Peluquero p2 = gestorPeluqueros.registrarPeluquero(
                "Mateo Salazar",
                EnumSet.of(DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY,
                        DayOfWeek.FRIDAY, DayOfWeek.SATURDAY),
                LocalTime.of(9, 0),
                LocalTime.of(18, 0)
        );

        
        Dueno d1 = gestorClientes.registrarDuenoYMascotas(
                "Andrea Paredes", "0991234567",
                List.of(
                        new DatosNuevaMascota("Max", "Golden Retriever", 4, 29.5, TipoPelaje.LARGO),
                        new DatosNuevaMascota("Milo", "Pug", 2, 8.2, TipoPelaje.CORTO)
                )
        );

        Dueno d2 = gestorClientes.registrarDuenoYMascotas(
                "Carlos Benítez", "0987654321",
                List.of(
                        new DatosNuevaMascota("Luna", "Schnauzer", 5, 11.0, TipoPelaje.MEDIO)
                )
        );

        Dueno d3 = gestorClientes.registrarDuenoYMascotas(
                "Sofía Villacís", "0994455667",
                List.of(
                        new DatosNuevaMascota("Rocky", "French Poodle", 3, 7.5, TipoPelaje.LARGO)
                )
        );

        Mascota max = d1.getMascotas().get(0);
        Mascota milo = d1.getMascotas().get(1);
        Mascota luna = d2.getMascotas().get(0);
        Mascota rocky = d3.getMascotas().get(0);


        LocalDate hoy = LocalDate.now();

      
        LocalDate fechaHistMax = ajustarADiaLaboral(hoy.minusDays(52), p1);
        Cita histMax = gestorAgenda.registrarCitaPrecargada(
                max, banoYCorte, p1, fechaHistMax, LocalTime.of(9, 0), CanalCita.WHATSAPP);
        gestorServicios.registrarServicioRealizado(histMax, "Sin nudos, piel sana.");

        
        LocalDate fechaHistLuna = ajustarADiaLaboral(hoy.minusDays(41), p2);
        Cita histLuna = gestorAgenda.registrarCitaPrecargada(
                luna, banoYCorte, p2, fechaHistLuna, LocalTime.of(10, 0), CanalCita.LLAMADA);
        gestorServicios.registrarServicioRealizado(histLuna, "Corte estándar de raza.");

        
        LocalDate fechaHistMilo = ajustarADiaLaboral(hoy.minusDays(10), p1);
        Cita histMilo = gestorAgenda.registrarCitaPrecargada(
                milo, banoBasico, p1, fechaHistMilo, LocalTime.of(11, 0), CanalCita.WHATSAPP);
        gestorServicios.registrarServicioRealizado(histMilo, "Buen comportamiento.");

        
        LocalDate manana = hoy.plusDays(1);
        while (!p1.trabajaElDia(manana.getDayOfWeek())) {
            manana = manana.plusDays(1);
        }

        gestorAgenda.registrarCitaPrecargada(
                max, deslanado, p1, manana, LocalTime.of(9, 0), CanalCita.WHATSAPP);
        gestorAgenda.registrarCitaPrecargada(
                rocky, banoYCorte, p1, manana, LocalTime.of(11, 30), CanalCita.LLAMADA);
    }

    private LocalDate ajustarADiaLaboral(LocalDate fecha, Peluquero peluquero) {
        LocalDate ajustada = fecha;
        while (!peluquero.trabajaElDia(ajustada.getDayOfWeek())) {
            ajustada = ajustada.minusDays(1);
        }
        return ajustada;
    }

    public ParametrosConfiguracion getConfiguracion() {
        return configuracion;
    }

    public GestorClientes getGestorClientes() {
        return gestorClientes;
    }

    public GestorPeluqueros getGestorPeluqueros() {
        return gestorPeluqueros;
    }

    public GestorAgenda getGestorAgenda() {
        return gestorAgenda;
    }

    public GestorServicios getGestorServicios() {
        return gestorServicios;
    }
}
