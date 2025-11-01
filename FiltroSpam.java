import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class FiltroSpam implements Runnable {

    public static class CoordinadorFin {
        final int totalClientes;
        final int totalFiltros;
        final int totalServidores;
        final AtomicInteger finClientes = new AtomicInteger(0);
        final AtomicBoolean finEmitido = new AtomicBoolean(false);

        public CoordinadorFin(int totalClientes, int totalFiltros, int totalServidores) {
            this.totalClientes = totalClientes;
            this.totalFiltros = totalFiltros;
            this.totalServidores = totalServidores;
        }
    }

    private final String nombre;
    private final Buzon buzonEntrada;
    private final Buzon buzonEntrega;
    private final Buzon buzonCuarentena;
    private final CoordinadorFin coord;
    private final Random random = new Random();

    public FiltroSpam(String nombre,
                      Buzon buzonEntrada,
                      Buzon buzonEntrega,
                      Buzon buzonCuarentena,
                      CoordinadorFin coord) {
        this.nombre = nombre;
        this.buzonEntrada = buzonEntrada;
        this.buzonEntrega = buzonEntrega;
        this.buzonCuarentena = buzonCuarentena;
        this.coord = coord;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Mensaje mensaje = buzonEntrada.take();
                System.out.println("[" + nombre + "] procesando: " + mensaje);

                if (mensaje.getTipo() == Mensaje.Tipo.INICIO) {
                    buzonEntrega.put(mensaje);
                } else if (mensaje.getTipo() == Mensaje.Tipo.NORMAL) {
                    if (mensaje.esSpam()) {
                        // asignar tiempo de cuarentena aleatorio 10–20s
                        int t = 10_000 + random.nextInt(10_000);
                        mensaje.setTiempoCuarentena(t);
                        buzonCuarentena.put(mensaje);
                        System.out.println("[" + nombre + "] a cuarentena: " + mensaje);
                    } else {
                        buzonEntrega.put(mensaje);
                        System.out.println("[" + nombre + "] a entrega: " + mensaje);
                    }
                } else { // FIN de un cliente
                    int c = coord.finClientes.incrementAndGet();
                    System.out.println("[" + nombre + "] recibió FIN de cliente. Total: " + c);

                    // Cuando TODOS los clientes terminaron, un único filtro emite FIN global
                    if (c == coord.totalClientes && coord.finEmitido.compareAndSet(false, true)) {
                        System.out.println("[" + nombre + "] Todos los clientes terminaron. Emisión de FIN global...");

                        // FIN para desbloquear a TODOS los servidores de entrega
                        for (int i = 0; i < coord.totalServidores; i++) {
                            buzonEntrega.put(new Mensaje("FIN-ENTREGA-" + i, "Sistema", Mensaje.Tipo.FIN, false));
                        }
                        // FIN para el manejador de cuarentena
                        buzonCuarentena.put(new Mensaje("FIN-CUARENTENA", "Sistema", Mensaje.Tipo.FIN, false));

                        // FIN para que TODOS los filtros que sigan en take() despierten y salgan
                        for (int i = 0; i < coord.totalFiltros; i++) {
                            buzonEntrada.put(new Mensaje("FIN-FILTRO-" + i, "Sistema", Mensaje.Tipo.FIN, false));
                        }
                    }
                }
                // Si ya se emitió FIN global y la entrada se vació, este filtro puede terminar
                if (coord.finEmitido.get() && buzonEntrada.isEmpty()) {
                    System.out.println("[" + nombre + "] fin global activo y entrada vacía. Saliendo...");
                    break;
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("[" + nombre + "] interrumpido.");
        }
    }
}