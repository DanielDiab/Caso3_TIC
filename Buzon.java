import java.util.LinkedList;
import java.util.Queue;

public class Buzon {
    private final Queue<Mensaje> cola = new LinkedList<>();
    private final int capacidad; // Integer.MAX_VALUE => ilimitado

    public Buzon(int capacidad) {
        this.capacidad = capacidad;
    }

    public synchronized void put(Mensaje mensaje) throws InterruptedException {
        while (cola.size() == capacidad) {
            wait();
        }
        cola.add(mensaje);
        notifyAll();
    }

    public synchronized Mensaje take() throws InterruptedException {
        while (cola.isEmpty()) {
            wait();
        }
        Mensaje m = cola.poll();
        notifyAll();
        return m;
    }

    public synchronized boolean isEmpty() { return cola.isEmpty(); }
    public synchronized int size() { return cola.size(); }
    public int getCapacidad() { return capacidad; }
}