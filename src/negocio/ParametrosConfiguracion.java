package negocio;

import modelo.EstadoCuidado;
import modelo.TamanoMascota;
import modelo.TipoPelaje;


public class ParametrosConfiguracion {
    
    private double pesoMaxPequena = 10.0;
    private double pesoMaxMediana = 25.0;

    
    private int minutosExtraMediana = 15;
    private int minutosExtraGrande = 30;
    private int minutosExtraPelajeMedio = 10;
    private int minutosExtraPelajeLargo = 20;

    
    private int diasAlertaAmarilla = 7;

    
    private double umbralOcupacionCasiLlena = 70.0;
    private double umbralOcupacionCompleta = 100.0;

    public TamanoMascota clasificarTamano(double pesoKg) {
        if (pesoKg <= 0) {
            throw new IllegalArgumentException("El peso debe ser mayor a 0 kg para clasificar a la mascota.");
        }
        if (pesoKg <= pesoMaxPequena) {
            return TamanoMascota.PEQUENA;
        } else if (pesoKg <= pesoMaxMediana) {
            return TamanoMascota.MEDIANA;
        } else {
            return TamanoMascota.GRANDE;
        }
    }

    public int calcularMinutosAdicionales(TamanoMascota tamano, TipoPelaje pelaje) {
        if (tamano == null || pelaje == null) {
            throw new IllegalArgumentException("El tamaño y el tipo de pelaje son obligatorios.");
        }
        int extraTamano = switch (tamano) {
            case PEQUENA -> 0;
            case MEDIANA -> minutosExtraMediana;
            case GRANDE -> minutosExtraGrande;
        };

        int extraPelaje = switch (pelaje) {
            case CORTO -> 0;
            case MEDIO -> minutosExtraPelajeMedio;
            case LARGO -> minutosExtraPelajeLargo;
        };

        return extraTamano + extraPelaje;
    }

    public EstadoCuidado evaluarEstadoCuidado(long diasRestantes) {
        if (diasRestantes > diasAlertaAmarilla) {
            return EstadoCuidado.VERDE;
        } else if (diasRestantes >= 0) {
            return EstadoCuidado.AMARILLO;
        } else {
            return EstadoCuidado.ROJO;
        }
    }

    public String evaluarEstadoOcupacion(double porcentajeOcupacion) {
        if (porcentajeOcupacion < umbralOcupacionCasiLlena) {
            return "Disponible";
        } else if (porcentajeOcupacion < umbralOcupacionCompleta) {
            return "Casi llena";
        } else {
            return "Completa";
        }
    }

    public double getPesoMaxPequena() {
        return pesoMaxPequena;
    }

    public double getPesoMaxMediana() {
        return pesoMaxMediana;
    }

    public int getMinutosExtraMediana() {
        return minutosExtraMediana;
    }

    public int getMinutosExtraGrande() {
        return minutosExtraGrande;
    }

    public int getMinutosExtraPelajeMedio() {
        return minutosExtraPelajeMedio;
    }

    public int getMinutosExtraPelajeLargo() {
        return minutosExtraPelajeLargo;
    }

    public int getDiasAlertaAmarilla() {
        return diasAlertaAmarilla;
    }

    public double getUmbralOcupacionCasiLlena() {
        return umbralOcupacionCasiLlena;
    }

    public double getUmbralOcupacionCompleta() {
        return umbralOcupacionCompleta;
    }

    @Override
    public String toString() {
        return String.format(
                "--- PARÁMETROS CONFIGURABLES DEL SISTEMA ---%n" +
                "* Tamaño Pequeña: <= %.1f kg (+0 min)%n" +
                "* Tamaño Mediana: > %.1f kg y <= %.1f kg (+%d min)%n" +
                "* Tamaño Grande:  > %.1f kg (+%d min)%n" +
                "* Pelaje Corto: +0 min | Medio: +%d min | Largo: +%d min%n" +
                "* Semáforo cuidado: Verde (> %d días) | Amarillo (0 a %d días) | Rojo (< 0 días)%n" +
                "* Ocupación jornada: Disponible (< %.0f%%) | Casi llena (%.0f%% - %.0f%%) | Completa (>= %.0f%%)",
                pesoMaxPequena, pesoMaxPequena, pesoMaxMediana, minutosExtraMediana,
                pesoMaxMediana, minutosExtraGrande,
                minutosExtraPelajeMedio, minutosExtraPelajeLargo,
                diasAlertaAmarilla, diasAlertaAmarilla,
                umbralOcupacionCasiLlena, umbralOcupacionCasiLlena, umbralOcupacionCompleta, umbralOcupacionCompleta
        );
    }
}
