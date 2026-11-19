package interfaz;

import negocio.SistemaStetiCare;

public class Main {
    public static void main(String[] args) {
        SistemaStetiCare sistema = new SistemaStetiCare();
        ConsolaStetiCare consola = new ConsolaStetiCare(sistema);
        consola.iniciar();
    }
}
