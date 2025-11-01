import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Simulador {

    public static void main(String[] args) {
        Properties props = new Properties();

        try {
            props.load(new FileInputStream("config.txt"));

            final int clientes = Integer.parseInt(props.getProperty("clientes"));
            final int mensajes = Integer.parseInt(props.getProperty("mensajes"));
            final int filtros  = Integer.parseInt(props.getProperty("filtros"));
            final int servidores = Integer.parseInt(props.getProperty("servidores"));
            final int capEntrada = Integer.parseInt(props.getProperty("capEntrada"));
            final int capEntrega = Integer.parseInt(props.getProperty("capEntrega"));

            System.out.println("=== INICIO DE LA SIMULACIÓN ===");
            System.out.println("Clientes: " + clientes + " | Filtros: " + filtros + " | Servidores: " + servidores + "\n");

            // Buzones
            Buzon buzonEntrada   = new Buzon(capEntrada);
            Buzon buzonEntrega   = new Buzon(capEntrega);
            Buzon buzonCuarentena= new Buzon(Integer.MAX_VALUE); // ilimitado

            // Coordinador de FIN global (compartido por todos los filtros)
            FiltroSpam.CoordinadorFin coord =
                new FiltroSpam.CoordinadorFin(clientes, filtros, servidores);

            List<Thread> hilos = new ArrayList<>();

            // Clientes
            for (int i = 1; i <= clientes; i++) {
                Thread t = new Thread(new ClienteEmisor("Cliente" + i, mensajes, buzonEntrada), "Cliente-" + i);
                hilos.add(t);
                t.start();
            }

            // Filtros
            for (int i = 1; i <= filtros; i++) {
                Thread t = new Thread(new FiltroSpam("Filtro" + i, buzonEntrada, buzonEntrega, buzonCuarentena, coord),
                        "Filtro-" + i);
                hilos.add(t);
                t.start();
            }

            // Manejador de cuarentena (uno)
            Thread manejador = new Thread(new ManejadorCuarentena(buzonCuarentena, buzonEntrega), "ManejadorCuarentena");
            hilos.add(manejador);
            manejador.start();

            // Servidores
            for (int i = 1; i <= servidores; i++) {
                Thread t = new Thread(new ServidorEntrega("Servidor" + i, buzonEntrega), "Servidor-" + i);
                hilos.add(t);
                t.start();
            }

            // Esperar a que todo termine
            for (Thread t : hilos) {
                t.join();
            }

            System.out.println("\n=== Simulación completada. Todos los hilos finalizaron correctamente. ===");

        } catch (IOException e) {
            System.err.println("Error leyendo config.txt: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("Simulación interrumpida.");
        }
    }
}