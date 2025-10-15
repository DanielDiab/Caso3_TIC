import java.util.LinkedList;
import java.util.Queue;

public class Buzon {
    private final Queue<Mensaje> cola;
    private final int capacidad;

    public Buzon(int capacidad) {
        this.capacidad = capacidad;
        this.cola = new LinkedList<>();
    }

    // productor deposita un mensaje
    public synchronized void put(Mensaje mensaje) throws InterruptedException {
        while (cola.size() == capacidad) {
            wait(); // espera pasiva si está lleno
        }
        cola.add(mensaje);
        notifyAll(); // despierta a los consumidores
    }

    // consumidor toma un mensaje
    public synchronized Mensaje take() throws InterruptedException {
        while (cola.isEmpty()) {
            wait(); // espera pasiva si está vacío
        }
        Mensaje mensaje = cola.poll();
        notifyAll(); // despierta a los productores
        return mensaje;
    }

    public synchronized boolean isEmpty() {
        return cola.isEmpty();
    }

    public synchronized int size() {
        return cola.size();
    }

    public int getCapacidad() {
        return capacidad;
    }
}