import java.util.Random;

public class ClienteEmisor implements Runnable {
    private final String nombre;
    private final int numMensajes;
    private final Buzon buzonEntrada;
    private final Random random = new Random();

    public ClienteEmisor(String nombre, int numMensajes, Buzon buzonEntrada) {
        this.nombre = nombre;
        this.numMensajes = numMensajes;
        this.buzonEntrada = buzonEntrada;
    }

    @Override
    public void run() {
        try {
            // INI
            Mensaje ini = new Mensaje("INI-" + nombre, nombre, Mensaje.Tipo.INICIO, false);
            buzonEntrada.put(ini);
            System.out.println(nombre + " envió: " + ini);

            // Mensajes
            for (int i = 1; i <= numMensajes; i++) {
                boolean esSpam = random.nextBoolean();
                Mensaje m = new Mensaje(nombre + "-MSG-" + i, nombre, Mensaje.Tipo.NORMAL, esSpam);
                buzonEntrada.put(m);
                System.out.println(nombre + " envió: " + m);
                Thread.sleep(50 + random.nextInt(100));
            }

            // FIN
            Mensaje fin = new Mensaje("FIN-" + nombre, nombre, Mensaje.Tipo.FIN, false);
            buzonEntrada.put(fin);
            System.out.println(nombre + " envió: " + fin);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println(nombre + " fue interrumpido.");
        }
    }
}