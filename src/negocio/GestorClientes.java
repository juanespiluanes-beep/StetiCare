package negocio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import modelo.Dueno;
import modelo.Mascota;
import modelo.TamanoMascota;
import modelo.TipoPelaje;


public class GestorClientes {
    private final List<Dueno> duenos;
    private final List<Mascota> mascotas;
    private final ParametrosConfiguracion config;
    private int secuenciaDueno;
    private int secuenciaMascota;

    public GestorClientes(ParametrosConfiguracion config) {
        this.duenos = new ArrayList<>();
        this.mascotas = new ArrayList<>();
        this.config = config;
        this.secuenciaDueno = 1;
        this.secuenciaMascota = 1;
    }

 
    public static class DatosNuevaMascota {
        public final String nombre;
        public final String raza;
        public final int edad;
        public final double pesoKg;
        public final TipoPelaje pelaje;

        public DatosNuevaMascota(String nombre, String raza, int edad, double pesoKg, TipoPelaje pelaje) {
            this.nombre = nombre;
            this.raza = raza;
            this.edad = edad;
            this.pesoKg = pesoKg;
            this.pelaje = pelaje;
        }
    }


    public Dueno registrarDuenoYMascotas(String nombreCompleto, String telefono,
                                         List<DatosNuevaMascota> listaMascotas) {
        if (listaMascotas == null || listaMascotas.isEmpty()) {
            throw new IllegalArgumentException("Debe registrar al menos una mascota asociada al dueño.");
        }

        String telLimpio = (telefono == null) ? "" : telefono.trim();
        if (buscarDuenoPorTelefono(telLimpio) != null) {
            throw new IllegalArgumentException("Ya existe un dueño registrado con el teléfono " + telLimpio + ".");
        }

   
        List<Mascota> mascotasValidadas = new ArrayList<>();
        int tempIdMascota = secuenciaMascota;
        for (DatosNuevaMascota d : listaMascotas) {
            TamanoMascota tamano = config.clasificarTamano(d.pesoKg);
            int extraMin = config.calcularMinutosAdicionales(tamano, d.pelaje);
            Mascota m = new Mascota(tempIdMascota++, d.nombre, d.raza, d.edad, d.pesoKg, d.pelaje, tamano, extraMin);
            mascotasValidadas.add(m);
        }

        Dueno nuevoDueno = new Dueno(secuenciaDueno++, nombreCompleto, telLimpio);
        for (Mascota m : mascotasValidadas) {
            m.setDueno(nuevoDueno);
            nuevoDueno.agregarMascota(m);
            mascotas.add(m);
        }
        secuenciaMascota = tempIdMascota;
        duenos.add(nuevoDueno);

        return nuevoDueno;
    }


    public Mascota agregarMascotaADuenoExistente(int idDueno, DatosNuevaMascota datos) {
        Dueno dueno = buscarDuenoPorId(idDueno);
        if (dueno == null) {
            throw new IllegalArgumentException("No se encontró un dueño con ID " + idDueno + ".");
        }
        if (datos == null) {
            throw new IllegalArgumentException("Los datos de la mascota son obligatorios.");
        }

        TamanoMascota tamano = config.clasificarTamano(datos.pesoKg);
        int extraMin = config.calcularMinutosAdicionales(tamano, datos.pelaje);
        Mascota mascota = new Mascota(secuenciaMascota++, datos.nombre, datos.raza,
                datos.edad, datos.pesoKg, datos.pelaje, tamano, extraMin);
        mascota.setDueno(dueno);
        dueno.agregarMascota(mascota);
        mascotas.add(mascota);
        return mascota;
    }

    public List<Mascota> buscarMascotasPorNombreOTelefono(String criterio) {
        if (criterio == null || criterio.trim().isEmpty()) {
            throw new IllegalArgumentException("Debe ingresar el nombre de la mascota o el teléfono del dueño.");
        }
        String filtro = criterio.trim().toLowerCase();
        List<Mascota> resultados = new ArrayList<>();

        for (Mascota m : mascotas) {
            boolean coincideMascota = m.getNombre().toLowerCase().contains(filtro);
            boolean coincideTelefono = m.getDueno() != null && m.getDueno().getTelefono().equals(filtro);
            if (coincideMascota || coincideTelefono) {
                resultados.add(m);
            }
        }
        return resultados;
    }


    public String clasificarYCalcularAjuste(double pesoKg, TipoPelaje pelaje) {
        TamanoMascota tamano = config.clasificarTamano(pesoKg);
        int minutosExtra = config.calcularMinutosAdicionales(tamano, pelaje);
        return String.format("Categoría asignada: %s | Pelaje: %s | Tiempo adicional estimado: +%d minutos",
                tamano.getDescripcion(), pelaje.getDescripcion(), minutosExtra);
    }

    public Mascota buscarMascotaPorId(int idMascota) {
        for (Mascota m : mascotas) {
            if (m.getId() == idMascota) {
                return m;
            }
        }
        return null;
    }

    public Dueno buscarDuenoPorId(int idDueno) {
        for (Dueno d : duenos) {
            if (d.getId() == idDueno) {
                return d;
            }
        }
        return null;
    }

    public Dueno buscarDuenoPorTelefono(String telefono) {
        for (Dueno d : duenos) {
            if (d.getTelefono().equals(telefono)) {
                return d;
            }
        }
        return null;
    }

    public List<Mascota> listarTodasLasMascotas() {
        return Collections.unmodifiableList(mascotas);
    }

    public List<Dueno> listarTodosLosDuenos() {
        return Collections.unmodifiableList(duenos);
    }
}
