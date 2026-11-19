package modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Mascota {
    private int id;
    private String nombre;
    private String raza;
    private int edad;
    private double pesoKg;
    private TipoPelaje tipoPelaje;
    private TamanoMascota tamano;
    private int minutosAdicionalesAtencion;
    private Dueno dueno;
    private final List<RegistroServicio> historialServicios;

    public Mascota(int id, String nombre, String raza, int edad, double pesoKg,
                   TipoPelaje tipoPelaje, TamanoMascota tamano, int minutosAdicionalesAtencion) {
        setId(id);
        setNombre(nombre);
        setRaza(raza);
        setEdad(edad);
        setPesoKg(pesoKg);
        setTipoPelaje(tipoPelaje);
        setTamano(tamano);
        setMinutosAdicionalesAtencion(minutosAdicionalesAtencion);
        this.historialServicios = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID de la mascota debe ser mayor a cero.");
        }
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la mascota es obligatorio.");
        }
        this.nombre = nombre.trim();
    }

    public String getRaza() {
        return raza;
    }

    public void setRaza(String raza) {
        if (raza == null || raza.trim().isEmpty()) {
            throw new IllegalArgumentException("La raza de la mascota es obligatoria.");
        }
        this.raza = raza.trim();
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        if (edad < 0 || edad > 35) {
            throw new IllegalArgumentException("La edad de la mascota debe estar entre 0 y 35 años.");
        }
        this.edad = edad;
    }

    public double getPesoKg() {
        return pesoKg;
    }

    public void setPesoKg(double pesoKg) {
        if (pesoKg <= 0) {
            throw new IllegalArgumentException("El peso de la mascota debe ser mayor a 0 kg.");
        }
        this.pesoKg = pesoKg;
    }

    public TipoPelaje getTipoPelaje() {
        return tipoPelaje;
    }

    public void setTipoPelaje(TipoPelaje tipoPelaje) {
        if (tipoPelaje == null) {
            throw new IllegalArgumentException("El tipo de pelaje es obligatorio.");
        }
        this.tipoPelaje = tipoPelaje;
    }

    public TamanoMascota getTamano() {
        return tamano;
    }

    public void setTamano(TamanoMascota tamano) {
        if (tamano == null) {
            throw new IllegalArgumentException("La categoría de tamaño es obligatoria.");
        }
        this.tamano = tamano;
    }

    public int getMinutosAdicionalesAtencion() {
        return minutosAdicionalesAtencion;
    }

    public void setMinutosAdicionalesAtencion(int minutosAdicionalesAtencion) {
        if (minutosAdicionalesAtencion < 0) {
            throw new IllegalArgumentException("Los minutos adicionales no pueden ser negativos.");
        }
        this.minutosAdicionalesAtencion = minutosAdicionalesAtencion;
    }

    public Dueno getDueno() {
        return dueno;
    }

    public void setDueno(Dueno dueno) {
        this.dueno = dueno;
    }

    public void agregarRegistroServicio(RegistroServicio registro) {
        if (registro == null) {
            throw new IllegalArgumentException("El registro de servicio no puede ser nulo.");
        }
        historialServicios.add(registro);
    }

    public List<RegistroServicio> getHistorialOrdenado() {
        List<RegistroServicio> copia = new ArrayList<>(historialServicios);
        copia.sort(Comparator.comparing(RegistroServicio::getFechaRealizacion).reversed()
                .thenComparing(Comparator.comparingInt(RegistroServicio::getId).reversed()));
        return Collections.unmodifiableList(copia);
    }

    public RegistroServicio getUltimoServicio() {
        List<RegistroServicio> ordenado = getHistorialOrdenado();
        return ordenado.isEmpty() ? null : ordenado.get(0);
    }

    @Override
    public String toString() {
        String nombreDueno = (dueno != null) ? dueno.getNombreCompleto() : "Sin asignar";
        return String.format("ID: %d | %s (%s) | %.1f kg - %s | Pelaje: %s (+%d min) | Dueño: %s",
                id, nombre, raza, pesoKg, tamano, tipoPelaje, minutosAdicionalesAtencion, nombreDueno);
    }
}
