import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Simulador {
    public static void main(String[] args) {
        Properties props = new Properties();

        try {
            // Leer archivo de configuración
            props.load(new FileInputStream("config.txt"));

            int numClientes = Integer.parseInt(props.getProperty("clientes"));
            int mensajesPorCliente = Integer.parseInt(props.getProperty("mensajes"));
            int numFiltros = Integer.parseInt(props.getProperty("filtros"));
            int numServidores = Integer.parseInt(props.getProperty("servidores"));
            int capacidadEntrada = Integer.parseInt(props.getProperty("capEntrada"));
            int capacidadEntrega = Integer.parseInt(props.getProperty("capEntrega"));

            // Crear buzones
            Buzon buzonEntrada = new Buzon(capacidadEntrada);
            Buzon buzonEntrega = new Buzon(capacidadEntrega);
            Buzon buzonCuarentena = new Buzon(Integer.MAX_VALUE); // ilimitado

            // Crear clientes
            for (int i = 1; i <= numClientes; i++) {
                ClienteEmisor cliente = new ClienteEmisor("Cliente" + i, mensajesPorCliente, buzonEntrada);
                new Thread(cliente).start();
            }

            // Crear filtros
            for (int i = 1; i < numFiltros; i++) {
                FiltroSpam filtro = new FiltroSpam(buzonEntrada, buzonEntrega, buzonCuarentena, numClientes);
                new Thread(filtro).start();
            }

            // Crear manejador de cuarentena
            ManejadorCuarentena manejador = new ManejadorCuarentena(buzonCuarentena, buzonEntrega);
            new Thread(manejador).start();

            // Crear servidores
            for (int i = 1; i <= numServidores; i++) {
                ServidorEntrega servidor = new ServidorEntrega("Servidor" + i, buzonEntrega);
                new Thread(servidor).start();
            }

        } catch (IOException e) {
            System.err.println("Error leyendo archivo de configuración: " + e.getMessage());
        }
    }
}