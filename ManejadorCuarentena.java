import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class ManejadorCuarentena implements Runnable {
    private final Buzon buzonCuarentena;
    private final Buzon buzonEntrega;
    private final Random random = new Random();
    private boolean activo = true;

    public ManejadorCuarentena(Buzon buzonCuarentena, Buzon buzonEntrega) {
        this.buzonCuarentena = buzonCuarentena;
        this.buzonEntrega = buzonEntrega;
    }

    @Override
    public void run() {
        List<Mensaje> enRevision = new LinkedList<>();

        try {
            while (activo) {
                // 1. Revisar si hay mensajes nuevos en cuarentena
                while (!buzonCuarentena.isEmpty()) {
                    Mensaje msg = buzonCuarentena.take();
                    if (msg.getTipo() == Mensaje.Tipo.FIN) {
                        // recibimos fin → marcar para terminar
                        System.out.println("Manejador de cuarentena recibió FIN.");
                        activo = false;
                        break;
                    }
                    enRevision.add(msg);
                }

                // 2. Procesar mensajes en revisión
                List<Mensaje> procesados = new LinkedList<>();
                for (Mensaje msg : enRevision) {
                    int tiempo = msg.getTiempoCuarentena() - 1;
                    msg.setTiempoCuarentena(tiempo);

                    if (tiempo <= 0) {
                        // decidir si pasa a entrega o se descarta
                        int valor = 1 + random.nextInt(21);
                        if (valor % 7 == 0) {
                            System.out.println("Manejador descartó mensaje malicioso: " + msg);
                        } else {
                            buzonEntrega.put(msg);
                            System.out.println("Manejador pasó a entrega: " + msg);
                        }
                        procesados.add(msg);
                    }
                }

                // eliminar los ya procesados
                enRevision.removeAll(procesados);

                // 3. esperar 1 segundo antes de siguiente ciclo
                Thread.sleep(1000);
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Manejador de cuarentena interrumpido.");
        }
    }
}