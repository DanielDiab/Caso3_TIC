import java.util.Random;

public class ClienteEmisor implements Runnable {
    private final String nombre;      // identificador del cliente
    private final int numMensajes;    // cantidad de correos a enviar
    private final Buzon buzonEntrada; // buzón compartido
    private final Random random;

    public ClienteEmisor(String nombre, int numMensajes, Buzon buzonEntrada) {
        this.nombre = nombre;
        this.numMensajes = numMensajes;
        this.buzonEntrada = buzonEntrada;
        this.random = new Random();
    }

    @Override
    public void run() {
        try {
            // 1. Mensaje de INICIO
            Mensaje inicio = new Mensaje("INI-" + nombre, nombre, Mensaje.Tipo.INICIO, false);
            buzonEntrada.put(inicio);
            System.out.println(nombre + " envió: " + inicio);

            // 2. Mensajes normales
            for (int i = 1; i <= numMensajes; i++) {
                boolean esSpam = random.nextBoolean(); // true = spam, false = válido
                String id = nombre + "-MSG-" + i;
                Mensaje msg = new Mensaje(id, nombre, Mensaje.Tipo.NORMAL, esSpam);
                buzonEntrada.put(msg);
                System.out.println(nombre + " envió: " + msg);

                // simular un pequeño retraso
                Thread.sleep(100 + random.nextInt(200));
            }

            // 3. Mensaje de FIN
            Mensaje fin = new Mensaje("FIN-" + nombre, nombre, Mensaje.Tipo.FIN, false);
            buzonEntrada.put(fin);
            System.out.println(nombre + " envió: " + fin);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println(nombre + " fue interrumpido.");
        }
    }
}