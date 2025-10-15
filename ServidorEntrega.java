import java.util.Random;

public class ServidorEntrega implements Runnable {
    private final String nombre;
    private final Buzon buzonEntrega;
    private final Random random = new Random();

    public ServidorEntrega(String nombre, Buzon buzonEntrega) {
        this.nombre = nombre;
        this.buzonEntrega = buzonEntrega;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Mensaje mensaje = buzonEntrega.take();

                if (mensaje.getTipo() == Mensaje.Tipo.FIN) {
                    System.out.println("[" + nombre + "] recibió FIN. Terminando...");
                    break; // fin del servidor
                }

                // Simular procesamiento
                System.out.println("[" + nombre + "] procesando mensaje: " + mensaje);
                Thread.sleep(500 + random.nextInt(1000));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("[" + nombre + "] interrumpido.");
        }
    }
}