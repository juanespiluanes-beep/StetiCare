package modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Dueno {
    private int id;
    private String nombreCompleto;
    private String telefono;
    private final List<Mascota> mascotas;

    public Dueno(int id, String nombreCompleto, String telefono) {
        setId(id);
        setNombreCompleto(nombreCompleto);
        setTelefono(telefono);
        this.mascotas = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID del dueño debe ser mayor a cero.");
        }
        this.id = id;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        if (nombreCompleto == null || nombreCompleto.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre y apellido del dueño son obligatorios.");
        }
        this.nombreCompleto = nombreCompleto.trim();
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        if (telefono == null || !telefono.trim().matches("\\d{10}")) {
            throw new IllegalArgumentException("El teléfono del dueño debe contener exactamente 10 dígitos numéricos.");
        }
        this.telefono = telefono.trim();
    }

    public List<Mascota> getMascotas() {
        return Collections.unmodifiableList(mascotas);
    }

    public void agregarMascota(Mascota mascota) {
        if (mascota == null) {
            throw new IllegalArgumentException("La mascota no puede ser nula.");
        }
        if (!mascotas.contains(mascota)) {
            mascotas.add(mascota);
        }
    }

    @Override
    public String toString() {
        return String.format("ID: %d | %s | Tel: %s | Mascotas: %d",
                id, nombreCompleto, telefono, mascotas.size());
    }
}
