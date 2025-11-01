import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class ManejadorCuarentena implements Runnable {
    private final Buzon buzonCuarentena;
    private final Buzon buzonEntrega;
    private final Random random = new Random();

    public ManejadorCuarentena(Buzon buzonCuarentena, Buzon buzonEntrega) {
        this.buzonCuarentena = buzonCuarentena;
        this.buzonEntrega = buzonEntrega;
    }

    @Override
    public void run() {
        List<Mensaje> enRevision = new LinkedList<>();
        boolean finRecibido = false;

        try {
            while (true) {
                // Drenar todo lo disponible en cuarentena sin bloquear indefinidamente
                while (true) {
                    Mensaje msg;
                    synchronized (buzonCuarentena) {
                        if (buzonCuarentena.isEmpty()) break;
                    }
                    msg = buzonCuarentena.take();

                    if (msg.getTipo() == Mensaje.Tipo.FIN) {
                        System.out.println("Manejador de cuarentena recibió FIN. Terminando revisión...");
                        finRecibido = true;
                        continue; // seguimos hasta vaciar enRevision
                    }
                    enRevision.add(msg);
                }

                // Progresar 1 “tick” (1s) sobre los mensajes en revisión
                List<Mensaje> listos = new LinkedList<>();
                for (Mensaje m : enRevision) {
                    int t = m.getTiempoCuarentena() - 1000;
                    m.setTiempoCuarentena(t);
                    if (t <= 0) {
                        // 1/7 se descarta; el resto pasa a entrega
                        int v = 1 + random.nextInt(21);
                        if (v % 7 == 0) {
                            System.out.println("Manejador descartó malicioso: " + m);
                        } else {
                            buzonEntrega.put(m);
                            System.out.println("Manejador pasó a entrega: " + m);
                        }
                        listos.add(m);
                    }
                }
                enRevision.removeAll(listos);

                // Criterio de salida: ya recibí FIN y no queda nada en revisión
                if (finRecibido && enRevision.isEmpty()) {
                    System.out.println("Manejador de cuarentena finalizado correctamente.");
                    break;
                }

                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Manejador de cuarentena interrumpido.");
        }
    }
}