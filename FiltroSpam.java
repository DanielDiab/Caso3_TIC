import java.util.Random;

public class FiltroSpam implements Runnable {
    private final Buzon buzonEntrada;
    private final Buzon buzonEntrega;
    private final Buzon buzonCuarentena;
    private final int totalClientes;
    private int finRecibidos = 0;
    private final Random random = new Random();

    public FiltroSpam(Buzon buzonEntrada, Buzon buzonEntrega, Buzon buzonCuarentena, int totalClientes) {
        this.buzonEntrada = buzonEntrada;
        this.buzonEntrega = buzonEntrega;
        this.buzonCuarentena = buzonCuarentena;
        this.totalClientes = totalClientes;
    }

    @Override
    public void run() {
        try {
            while (true) {
                Mensaje mensaje = buzonEntrada.take();
                System.out.println("Filtro procesando: " + mensaje);

                if (mensaje.getTipo() == Mensaje.Tipo.INICIO) {
                    // solo reenviamos al buzón de entrega para que servidores sepan
                    buzonEntrega.put(mensaje);
                } 
                else if (mensaje.getTipo() == Mensaje.Tipo.NORMAL) {
                    if (mensaje.esSpam()) {
                        // asignar tiempo de cuarentena
                        int tiempo = 10000 + random.nextInt(10000);
                        mensaje.setTiempoCuarentena(tiempo);
                        buzonCuarentena.put(mensaje);
                        System.out.println("Filtro envió a cuarentena: " + mensaje);
                    } else {
                        buzonEntrega.put(mensaje);
                        System.out.println("Filtro envió a entrega: " + mensaje);
                    }
                } 
                else if (mensaje.getTipo() == Mensaje.Tipo.FIN) {
                    finRecibidos++;
                    // reenviamos también a cuarentena para que manejador sepa
                    buzonCuarentena.put(mensaje);
                    System.out.println("Filtro recibió FIN de cliente. Total recibidos: " + finRecibidos);

                    if (finRecibidos == totalClientes) {
                        // cuando ya terminaron todos los clientes → mandar FIN a entrega
                        Mensaje finEntrega = new Mensaje("FIN-ENTREGA", "Sistema", Mensaje.Tipo.FIN, false);
                        buzonEntrega.put(finEntrega);
                        System.out.println("Filtro envió FIN a entrega.");
                        break; // este filtro termina
                    }
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Filtro interrumpido.");
        }
    }
}