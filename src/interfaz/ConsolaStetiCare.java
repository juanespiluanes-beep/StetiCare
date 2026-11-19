package interfaz;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import modelo.CanalCita;
import modelo.Cita;
import modelo.Dueno;
import modelo.EstadoCita;
import modelo.Mascota;
import modelo.Peluquero;
import modelo.RegistroServicio;
import modelo.TipoPelaje;
import modelo.TipoServicio;
import negocio.GestorClientes.DatosNuevaMascota;
import negocio.GestorPeluqueros.ReporteOcupacion;
import negocio.GestorServicios.EstadoProximoServicio;
import negocio.SistemaStetiCare;


public class ConsolaStetiCare {
    private final SistemaStetiCare sistema;
    private final Scanner scanner;

    public ConsolaStetiCare(SistemaStetiCare sistema) {
        this.sistema = sistema;
        this.scanner = new Scanner(System.in);
    }

    public void iniciar() {
        int opcion = -1;
        System.out.println("=================================================================");
        System.out.println("        STETICARE - SISTEMA DE GESTIÓN DE PELUQUERÍA CANINA      ");
        System.out.println("                    Caso de estudio: StetiDog's                  ");
        System.out.println("=================================================================");

        while (opcion != 0) {
            mostrarMenuPrincipal();
            opcion = leerEntero("Seleccione una opción: ");
            System.out.println();

            try {
                switch (opcion) {
                    case 1 -> menuModuloClientes();
                    case 2 -> menuModuloAgenda();
                    case 3 -> menuModuloServicios();
                    case 4 -> menuModuloPeluqueros();
                    case 5 -> System.out.println(sistema.getConfiguracion());
                    case 0 -> System.out.println("Saliendo de StetiCare. ¡Hasta pronto!");
                    default -> System.out.println("[AVISO] Opción no válida. Intente nuevamente.");
                }
            } catch (Exception e) {
                System.out.println("[ERROR] " + e.getMessage());
            }
            System.out.println();
        }
    }

    private void mostrarMenuPrincipal() {
        System.out.println("MENU");
        System.out.println("1. Módulo 1: Clientes y Mascotas (RF1 - RF3)");
        System.out.println("2. Módulo 2: Agenda de Citas (RF4 - RF7)");
        System.out.println("3. Módulo 3: Servicios y Ciclo de Cuidado (RF8 - RF10)");
        System.out.println("4. Módulo 4: Peluqueros y Carga de Trabajo (RF11 - RF13)");
        System.out.println("5. Ver parámetros configurables del sistema");
        System.out.println("0. Salir");
        System.out.println("-----------------------------------------------------------------");
    }


    private void menuModuloClientes() {
        System.out.println("CLIENTES Y MASCOTAS ---");
        System.out.println("1. Registrar dueño y mascotas");
        System.out.println("2. Agregar mascota a un dueño existente");
        System.out.println("3. Consultar ficha e historial de mascota");
        System.out.println("4. Simular clasificación por tamaño y pelaje");
        System.out.println("5. Listar todos los dueños y mascotas registrados");
        System.out.println("0. Volver al menú principal");

        int op = leerEntero("Seleccione una opción: ");
        switch (op) {
            case 1 -> ejecutarRF1RegistrarDuenoYMascotas();
            case 2 -> ejecutarAgregarMascotaDuenoExistente();
            case 3 -> ejecutarRF2ConsultarFichaMascota();
            case 4 -> ejecutarRF3ClasificarMascota();
            case 5 -> listarClientesYMascotas();
            case 0 -> { }
            default -> System.out.println("[AVISO] Opción no válida.");
        }
    }

    private void ejecutarRF1RegistrarDuenoYMascotas() {
        System.out.println("\nREGISTRO DE DUEÑO Y MASCOTAS");
        String nombreDueno = leerTextoNoVacio("Nombre y apellido del dueño: ");
        String telefono = leerTextoNoVacio("Teléfono (10 dígitos): ");

        int cantidad = leerEnteroPositivo("¿Cuántas mascotas desea registrar para este dueño?: ");
        List<DatosNuevaMascota> listaMascotas = new ArrayList<>();

        for (int i = 1; i <= cantidad; i++) {
            System.out.println("\n-- Datos de la Mascota #" + i + " --");
            String nombreMascota = leerTextoNoVacio("Nombre de la mascota: ");
            String raza = leerTextoNoVacio("Raza: ");
            int edad = leerEnteroNoNegativo("Edad (en años): ");
            double peso = leerDoublePositivo("Peso (en kg): ");
            TipoPelaje pelaje = leerTipoPelaje();
            listaMascotas.add(new DatosNuevaMascota(nombreMascota, raza, edad, peso, pelaje));
        }

        Dueno registrado = sistema.getGestorClientes().registrarDuenoYMascotas(nombreDueno, telefono, listaMascotas);
        System.out.println("\n[OK] Dueño y mascotas registrados exitosamente:");
        System.out.println("  " + registrado);
        for (Mascota m : registrado.getMascotas()) {
            System.out.println("   -> " + m);
        }
    }

    private void ejecutarAgregarMascotaDuenoExistente() {
        List<Dueno> duenos = sistema.getGestorClientes().listarTodosLosDuenos();
        if (duenos.isEmpty()) {
            System.out.println("No hay dueños registrados.");
            return;
        }
        System.out.println("\nDueños registrados:");
        duenos.forEach(d -> System.out.println("  " + d));

        int idDueno = leerEnteroPositivo("Ingrese el ID del dueño: ");
        String nombreMascota = leerTextoNoVacio("Nombre de la mascota: ");
        String raza = leerTextoNoVacio("Raza: ");
        int edad = leerEnteroNoNegativo("Edad (en años): ");
        double peso = leerDoublePositivo("Peso (en kg): ");
        TipoPelaje pelaje = leerTipoPelaje();

        Mascota nueva = sistema.getGestorClientes().agregarMascotaADuenoExistente(
                idDueno, new DatosNuevaMascota(nombreMascota, raza, edad, peso, pelaje));
        System.out.println("[OK] Mascota agregada: " + nueva);
    }

    private void ejecutarRF2ConsultarFichaMascota() {
        System.out.println("\n CONSULTAR FICHA E HISTORIAL DE MASCOTA");
        String criterio = leerTextoNoVacio("Ingrese el nombre de la mascota o el teléfono del dueño: ");
        List<Mascota> encontradas = sistema.getGestorClientes().buscarMascotasPorNombreOTelefono(criterio);

        if (encontradas.isEmpty()) {
            System.out.println("Sin resultados.");
            return;
        }

        for (Mascota m : encontradas) {
            System.out.println("=================================================================");
            System.out.println("FICHA DE MASCOTA [ID #" + m.getId() + "]");
            System.out.println("  Nombre:     " + m.getNombre() + " | Raza: " + m.getRaza() + " | Edad: " + m.getEdad() + " años");
            System.out.println("  Peso:       " + m.getPesoKg() + " kg -> Categoría: " + m.getTamano());
            System.out.println("  Pelaje:     " + m.getTipoPelaje() + " (Ajuste tiempo: +" + m.getMinutosAdicionalesAtencion() + " min)");
            System.out.println("  Dueño:      " + m.getDueno().getNombreCompleto() + " (Tel: " + m.getDueno().getTelefono() + ")");

            List<RegistroServicio> historial = m.getHistorialOrdenado();
            if (historial.isEmpty()) {
                System.out.println("  Historial:  Sin servicios registrados aún.");
            } else {
                System.out.println("  Último servicio: " + historial.get(0).getFechaRealizacion()
                        + " (" + historial.get(0).getServicio().getNombre() + ")");
                System.out.println("  Historial completo (ordenado por fecha):");
                for (RegistroServicio reg : historial) {
                    System.out.println("    * " + reg);
                }
            }
        }
    }

    private void ejecutarRF3ClasificarMascota() {
        System.out.println("\n CLASIFICACIÓN POR TAMAÑO Y PELAJE (REGLA DE NEGOCIO)");
        double peso = leerDouble("Ingrese el peso de la mascota en kg: ");
        TipoPelaje pelaje = leerTipoPelaje();
        String resultado = sistema.getGestorClientes().clasificarYCalcularAjuste(peso, pelaje);
        System.out.println("[OK] " + resultado);
    }

    private void listarClientesYMascotas() {
        List<Dueno> duenos = sistema.getGestorClientes().listarTodosLosDuenos();
        if (duenos.isEmpty()) {
            System.out.println("Sin registros.");
            return;
        }
        for (Dueno d : duenos) {
            System.out.println(d);
            for (Mascota m : d.getMascotas()) {
                System.out.println("   -> " + m);
            }
        }
    }


    private void menuModuloAgenda() {
        System.out.println("AGENDA DE CITAS ---");
        System.out.println("1. [RF4 / RF5] Programar nueva cita (con validación de cruces)");
        System.out.println("2. [RF6] Actualizar estado de cita (Cancelar / No asistió)");
        System.out.println("3. [RF7] Consultar agenda del día");
        System.out.println("0. Volver al menú principal");

        int op = leerEntero("Seleccione una opción: ");
        switch (op) {
            case 1 -> ejecutarRF4ProgramarCita();
            case 2 -> ejecutarRF6ActualizarEstadoCita();
            case 3 -> ejecutarRF7ConsultarAgendaDia();
            case 0 -> { }
            default -> System.out.println("[AVISO] Opción no válida.");
        }
    }

    private void ejecutarRF4ProgramarCita() {
        System.out.println("\n PROGRAMAR CITA");
        List<Mascota> mascotas = sistema.getGestorClientes().listarTodasLasMascotas();
        if (mascotas.isEmpty()) {
            System.out.println("No hay mascotas registradas. Registre primero un dueño y su mascota en el Módulo 1.");
            return;
        }
        System.out.println("Mascotas disponibles:");
        mascotas.forEach(m -> System.out.println("  " + m));
        int idMascota = leerEnteroPositivo("Seleccione ID de la mascota: ");
        Mascota mascota = sistema.getGestorClientes().buscarMascotaPorId(idMascota);
        if (mascota == null) {
            throw new IllegalArgumentException("Mascota no encontrada con ID " + idMascota);
        }

        System.out.println("\nServicios disponibles:");
        sistema.getGestorServicios().listarCatalogoServicios().forEach(s -> System.out.println("  " + s));
        int idServicio = leerEnteroPositivo("Seleccione ID del servicio: ");
        TipoServicio servicio = sistema.getGestorServicios().buscarServicioPorId(idServicio);
        if (servicio == null) {
            throw new IllegalArgumentException("Servicio no encontrado con ID " + idServicio);
        }

        System.out.println("\nPeluqueros disponibles:");
        sistema.getGestorPeluqueros().listarPeluqueros().forEach(p -> System.out.println("  " + p));
        int idPeluquero = leerEnteroPositivo("Seleccione ID del peluquero: ");
        Peluquero peluquero = sistema.getGestorPeluqueros().buscarPeluqueroPorId(idPeluquero);
        if (peluquero == null) {
            throw new IllegalArgumentException("Peluquero no encontrado con ID " + idPeluquero);
        }

        LocalDate fecha = leerFecha("Fecha de la cita (AAAA-MM-DD): ");
        LocalTime horaInicio = leerHora("Hora de inicio (HH:MM, formato 24h): ");
        CanalCita canal = leerCanalCita();

        Cita cita = sistema.getGestorAgenda().programarCita(mascota, servicio, peluquero, fecha, horaInicio, canal);
        System.out.println("\n[OK] Cita programada correctamente:");
        System.out.println("  " + cita);
    }

    private void ejecutarRF6ActualizarEstadoCita() {
        System.out.println("\n[RF6] ACTUALIZAR ESTADO DE CITA (REGLA DE NEGOCIO)");
        List<Cita> todas = sistema.getGestorAgenda().listarTodasLasCitas();
        if (todas.isEmpty()) {
            System.out.println("No hay citas registradas.");
            return;
        }
        todas.forEach(c -> System.out.println("  " + c));

        int idCita = leerEnteroPositivo("Ingrese el ID de la cita a actualizar: ");
        System.out.println("Nuevo estado: 1. Cancelada | 2. No asistió");
        int tipoEstado = leerEntero("Seleccione opción: ");
        EstadoCita nuevoEstado = switch (tipoEstado) {
            case 1 -> EstadoCita.CANCELADA;
            case 2 -> EstadoCita.NO_ASISTIO;
            default -> throw new IllegalArgumentException("Opción de estado inválida.");
        };

        System.out.print("Motivo (opcional, presione Enter para omitir): ");
        String motivo = scanner.nextLine();

        Cita actualizada = sistema.getGestorAgenda().actualizarEstadoCita(idCita, nuevoEstado, motivo);
        System.out.println("[OK] Estado actualizado y horario liberado: " + actualizada);
    }

    private void ejecutarRF7ConsultarAgendaDia() {
        System.out.println("\n[RF7] CONSULTAR AGENDA DEL DÍA");
        LocalDate fecha = leerFecha("Ingrese la fecha a consultar (AAAA-MM-DD): ");

        System.out.println("Peluqueros:");
        sistema.getGestorPeluqueros().listarPeluqueros().forEach(p -> System.out.println("  " + p));
        int idPel = leerEnteroNoNegativo("Ingrese ID del peluquero (o 0 para ver todos): ");
        Integer filtroPeluquero = (idPel == 0) ? null : idPel;

        List<Cita> agenda = sistema.getGestorAgenda().consultarAgendaDelDia(fecha, filtroPeluquero);
        if (agenda.isEmpty()) {
            System.out.println("Sin citas para la fecha " + fecha + ".");
            return;
        }

        System.out.println("\n--- AGENDA DEL " + fecha + " ---");
        for (Cita c : agenda) {
            System.out.println("  " + c);
        }
    }

    private void menuModuloServicios() {
        System.out.println("SERVICIOS Y CICLO DE CUIDADO ---");
        System.out.println("1. Registrar servicio realizado (cerrar cita como Atendida)");
        System.out.println("2. Calcular estado del próximo servicio (Semáforo)");
        System.out.println("3. Consultar mascotas con servicio por vencer");
        System.out.println("0. Volver al menú principal");

        int op = leerEntero("Seleccione una opción: ");
        switch (op) {
            case 1 -> ejecutarRF8RegistrarServicioRealizado();
            case 2 -> ejecutarRF9CalcularEstadoProximoServicio();
            case 3 -> ejecutarRF10ConsultarMascotasPorVencer();
            case 0 -> { }
            default -> System.out.println("[AVISO] Opción no válida.");
        }
    }

    private void ejecutarRF8RegistrarServicioRealizado() {
        System.out.println("\n[RF8] REGISTRAR SERVICIO REALIZADO");
        List<Cita> todas = sistema.getGestorAgenda().listarTodasLasCitas();
        if (todas.isEmpty()) {
            System.out.println("No existen citas registradas.");
            return;
        }
        todas.forEach(c -> System.out.println("  " + c));

        int idCita = leerEnteroPositivo("Ingrese el ID de la cita en estado 'Programada': ");
        Cita cita = sistema.getGestorAgenda().buscarCitaPorId(idCita);
        if (cita == null) {
            throw new IllegalArgumentException("No existe una cita con ID #" + idCita + ".");
        }

        System.out.print("Observaciones del servicio (opcional, Enter para omitir): ");
        String observaciones = scanner.nextLine();

        RegistroServicio reg = sistema.getGestorServicios().registrarServicioRealizado(cita, observaciones);
        System.out.println("[OK] Cita marcada como 'Atendida' y guardada en el historial de " + cita.getMascota().getNombre() + ":");
        System.out.println("  " + reg);
    }

    private void ejecutarRF9CalcularEstadoProximoServicio() {
        System.out.println("\n[RF9] CALCULAR ESTADO DEL PRÓXIMO SERVICIO (REGLA DE NEGOCIO)");
        System.out.println("1. Consultar una mascota específica");
        System.out.println("2. Consultar todas las mascotas");
        int modo = leerEntero("Seleccione opción: ");

        LocalDate fechaConsulta = leerFechaConDefecto("Fecha de consulta (AAAA-MM-DD, Enter para hoy " + LocalDate.now() + "): ");

        if (modo == 1) {
            sistema.getGestorClientes().listarTodasLasMascotas().forEach(m -> System.out.println("  " + m));
            int idMascota = leerEnteroPositivo("Ingrese ID de la mascota: ");
            Mascota m = sistema.getGestorClientes().buscarMascotaPorId(idMascota);
            if (m == null) {
                throw new IllegalArgumentException("Mascota no encontrada.");
            }
            imprimirEstadoProximoServicio(sistema.getGestorServicios().calcularEstadoProximoServicio(m, fechaConsulta));
        } else if (modo == 2) {
            for (Mascota m : sistema.getGestorClientes().listarTodasLasMascotas()) {
                imprimirEstadoProximoServicio(sistema.getGestorServicios().calcularEstadoProximoServicio(m, fechaConsulta));
            }
        } else {
            System.out.println("[AVISO] Opción inválida.");
        }
    }

    private void imprimirEstadoProximoServicio(EstadoProximoServicio info) {
        if (info.ultimoServicio == null) {
            System.out.printf("  * %-12s (Dueño: %-16s) -> Sin historial%n",
                    info.mascota.getNombre(), info.mascota.getDueno().getNombreCompleto());
        } else {
            System.out.printf("  * %-12s | Último: %s (%s) | Próximo: %s | Días restantes: %4d | Estado: %s%n",
                    info.mascota.getNombre(),
                    info.ultimoServicio.getFechaRealizacion(),
                    info.ultimoServicio.getServicio().getNombre(),
                    info.fechaProximoServicio,
                    info.diasRestantes,
                    info.estadoCuidado);
        }
    }

    private void ejecutarRF10ConsultarMascotasPorVencer() {
        System.out.println("\n[RF10] CONSULTAR MASCOTAS CON SERVICIO POR VENCER");
        System.out.println("Filtro: 1. Amarillo | 2. Rojo | 3. Ambos (Amarillo y Rojo)");
        int opFiltro = leerEntero("Seleccione filtro: ");
        String filtro = switch (opFiltro) {
            case 1 -> "AMARILLO";
            case 2 -> "ROJO";
            case 3 -> "AMBOS";
            default -> throw new IllegalArgumentException("Opción de filtro inválida.");
        };

        LocalDate fechaConsulta = leerFechaConDefecto("Fecha de consulta (AAAA-MM-DD, Enter para hoy " + LocalDate.now() + "): ");

        List<EstadoProximoServicio> pendientes = sistema.getGestorServicios()
                .consultarMascotasPorVencer(sistema.getGestorClientes().listarTodasLasMascotas(), filtro, fechaConsulta);

        if (pendientes.isEmpty()) {
            System.out.println("Sin pendientes.");
            return;
        }

        System.out.println("\n--- MASCOTAS CON SERVICIO POR VENCER (ORDENADAS POR URGENCIA) ---");
        for (EstadoProximoServicio e : pendientes) {
            System.out.printf("  [%s] Mascota: %-10s | Dueño: %-16s | Tel: %s | Vence: %s | Días: %d%n",
                    e.estadoCuidado.name(),
                    e.mascota.getNombre(),
                    e.mascota.getDueno().getNombreCompleto(),
                    e.mascota.getDueno().getTelefono(),
                    e.fechaProximoServicio,
                    e.diasRestantes);
        }
    }


    private void menuModuloPeluqueros() {
        System.out.println("PELUQUEROS Y CARGA DE TRABAJO ---");
        System.out.println("1. Registrar peluquero y jornada");
        System.out.println("2. Calcular ocupación de jornada");
        System.out.println("3. Consultar horarios libres de un peluquero");
        System.out.println("4. Listar peluqueros registrados");
        System.out.println("0. Volver al menú principal");

        int op = leerEntero("Seleccione una opción: ");
        switch (op) {
            case 1 -> ejecutarRF11RegistrarPeluquero();
            case 2 -> ejecutarRF12CalcularOcupacion();
            case 3 -> ejecutarRF13ConsultarHorariosLibres();
            case 4 -> sistema.getGestorPeluqueros().listarPeluqueros().forEach(p -> System.out.println("  " + p));
            case 0 -> { }
            default -> System.out.println("[AVISO] Opción no válida.");
        }
    }

    private void ejecutarRF11RegistrarPeluquero() {
        System.out.println("\n REGISTRAR PELUQUERO Y JORNADA");
        String nombre = leerTextoNoVacio("Nombre completo del peluquero: ");
        System.out.println("Ingrese los números de los días de trabajo separados por coma:");
        System.out.println("  1=Lunes, 2=Martes, 3=Miércoles, 4=Jueves, 5=Viernes, 6=Sábado, 7=Domingo");
        String diasTexto = leerTextoNoVacio("Días (ej. 1,2,3,4,5,6): ");

        Set<DayOfWeek> diasTrabajo = EnumSet.noneOf(DayOfWeek.class);
        for (String token : diasTexto.split(",")) {
            try {
                int numDia = Integer.parseInt(token.trim());
                diasTrabajo.add(DayOfWeek.of(numDia));
            } catch (Exception ex) {
                throw new IllegalArgumentException("Día inválido '" + token.trim() + "'. Use números del 1 al 7 separados por coma.");
            }
        }

        LocalTime entrada = leerHora("Hora de entrada (HH:MM): ");
        LocalTime salida = leerHora("Hora de salida (HH:MM): ");

        Peluquero p = sistema.getGestorPeluqueros().registrarPeluquero(nombre, diasTrabajo, entrada, salida);
        System.out.println("[OK] Peluquero registrado: " + p);
    }

    private void ejecutarRF12CalcularOcupacion() {
        System.out.println("\nCALCULAR OCUPACIÓN DE LA JORNADA (REGLA DE NEGOCIO)");
        sistema.getGestorPeluqueros().listarPeluqueros().forEach(p -> System.out.println("  " + p));
        int idPel = leerEnteroPositivo("Seleccione ID del peluquero: ");
        LocalDate fecha = leerFecha("Fecha a evaluar (AAAA-MM-DD): ");

        List<Cita> citasDia = sistema.getGestorAgenda().obtenerCitasActivasPeluqueroFecha(idPel, fecha);
        ReporteOcupacion rep = sistema.getGestorPeluqueros().calcularOcupacionJornada(idPel, fecha, citasDia);

        if (!rep.trabajaEseDia) {
            System.out.println("[INFO] El peluquero " + rep.peluquero.getNombreCompleto() + " no labora el día " + fecha + ".");
            return;
        }

        System.out.printf("[OK] Peluquero: %s | Fecha: %s%n", rep.peluquero.getNombreCompleto(), rep.fecha);
        System.out.printf("     Minutos ocupados: %d / %d min | Ocupación: %.1f%% | Estado: %s%n",
                rep.minutosOcupados, rep.minutosJornada, rep.porcentajeOcupacion, rep.estadoJornada);
    }

    private void ejecutarRF13ConsultarHorariosLibres() {
        System.out.println("\nCONSULTAR HORARIOS LIBRES DE UN PELUQUERO");
        sistema.getGestorPeluqueros().listarPeluqueros().forEach(p -> System.out.println("  " + p));
        int idPel = leerEnteroPositivo("Seleccione ID del peluquero: ");
        LocalDate fecha = leerFecha("Fecha a consultar (AAAA-MM-DD): ");
        int duracion = leerEnteroPositivo("Duración requerida en minutos (ej. 60): ");

        List<Cita> citasDia = sistema.getGestorAgenda().obtenerCitasActivasPeluqueroFecha(idPel, fecha);
        List<String> libres = sistema.getGestorPeluqueros().consultarHorariosLibres(idPel, fecha, duracion, citasDia);

        if (libres.isEmpty()) {
            System.out.println("Sin horarios libres.");
            return;
        }

        System.out.println("\n--- ESPACIOS DISPONIBLES (" + fecha + ") ---");
        for (String tramo : libres) {
            System.out.println("  * " + tramo);
        }
    }

 
    private String leerTextoNoVacio(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String entrada = scanner.nextLine().trim();
            if (!entrada.isEmpty()) {
                return entrada;
            }
            System.out.println("[VALIDACIÓN] Este campo es obligatorio. Intente nuevamente.");
        }
    }

    private int leerEntero(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String linea = scanner.nextLine().trim();
            try {
                return Integer.parseInt(linea);
            } catch (NumberFormatException e) {
                System.out.println("[VALIDACIÓN] Debe ingresar un número entero válido.");
            }
        }
    }

    private int leerEnteroPositivo(String mensaje) {
        while (true) {
            int val = leerEntero(mensaje);
            if (val > 0) {
                return val;
            }
            System.out.println("[VALIDACIÓN] El valor debe ser mayor a cero.");
        }
    }

    private int leerEnteroNoNegativo(String mensaje) {
        while (true) {
            int val = leerEntero(mensaje);
            if (val >= 0) {
                return val;
            }
            System.out.println("[VALIDACIÓN] El valor no puede ser negativo.");
        }
    }

    private double leerDouble(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String linea = scanner.nextLine().trim().replace(",", ".");
            try {
                return Double.parseDouble(linea);
            } catch (NumberFormatException e) {
                System.out.println("[VALIDACIÓN] Debe ingresar un valor numérico válido.");
            }
        }
    }

    private double leerDoublePositivo(String mensaje) {
        while (true) {
            double val = leerDouble(mensaje);
            if (val > 0) {
                return val;
            }
            System.out.println("[VALIDACIÓN] El valor debe ser mayor a cero.");
        }
    }

    private LocalDate leerFecha(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String linea = scanner.nextLine().trim();
            try {
                return LocalDate.parse(linea);
            } catch (DateTimeParseException e) {
                System.out.println("[VALIDACIÓN] Formato de fecha inválido. Use AAAA-MM-DD (ej. 2026-11-20).");
            }
        }
    }

    private LocalDate leerFechaConDefecto(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String linea = scanner.nextLine().trim();
            if (linea.isEmpty()) {
                return LocalDate.now();
            }
            try {
                return LocalDate.parse(linea);
            } catch (DateTimeParseException e) {
                System.out.println("[VALIDACIÓN] Formato de fecha inválido. Use AAAA-MM-DD o presione Enter.");
            }
        }
    }

    private LocalTime leerHora(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String linea = scanner.nextLine().trim();
            try {
                return LocalTime.parse(linea);
            } catch (DateTimeParseException e) {
                System.out.println("[VALIDACIÓN] Formato de hora inválido. Use HH:MM en formato 24h (ej. 09:30 o 14:00).");
            }
        }
    }

    private TipoPelaje leerTipoPelaje() {
        while (true) {
            System.out.println("Tipo de pelaje: 1. Corto | 2. Medio | 3. Largo");
            int op = leerEntero("Seleccione opción: ");
            switch (op) {
                case 1 -> { return TipoPelaje.CORTO; }
                case 2 -> { return TipoPelaje.MEDIO; }
                case 3 -> { return TipoPelaje.LARGO; }
                default -> System.out.println("[VALIDACIÓN] Seleccione 1, 2 o 3.");
            }
        }
    }

    private CanalCita leerCanalCita() {
        while (true) {
            System.out.println("Canal de solicitud: 1. Llamada | 2. WhatsApp");
            int op = leerEntero("Seleccione opción: ");
            switch (op) {
                case 1 -> { return CanalCita.LLAMADA; }
                case 2 -> { return CanalCita.WHATSAPP; }
                default -> System.out.println("[VALIDACIÓN] Seleccione 1 o 2.");
            }
        }
    }
}
