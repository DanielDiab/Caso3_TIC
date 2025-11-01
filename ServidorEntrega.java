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
                    break;
                }

                System.out.println("[" + nombre + "] procesando mensaje: " + mensaje);
                Thread.sleep(100 + random.nextInt(200));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("[" + nombre + "] interrumpido.");
        } finally {
            System.out.println("[" + nombre + "] finalizó correctamente.");
        }
    }
}