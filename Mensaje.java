public class Mensaje {
    public enum Tipo {
        INICIO, NORMAL, FIN
    }

    private final String id;        // identificador único (cliente + secuencia)
    private final String cliente;   // nombre o id del cliente emisor
    private final Tipo tipo;        // INICIO, NORMAL, FIN
    private final boolean esSpam;   // flag para correos normales
    private int tiempoCuarentena;   // usado cuando va a cuarentena

    public Mensaje(String id, String cliente, Tipo tipo, boolean esSpam) {
        this.id = id;
        this.cliente = cliente;
        this.tipo = tipo;
        this.esSpam = esSpam;
        this.tiempoCuarentena = 0;
    }

    // getters y setters
    public String getId() { return id; }
    public String getCliente() { return cliente; }
    public Tipo getTipo() { return tipo; }
    public boolean esSpam() { return esSpam; }
    public int getTiempoCuarentena() { return tiempoCuarentena; }
    public void setTiempoCuarentena(int t) { this.tiempoCuarentena = t; }

    @Override
    public String toString() {
        return "Mensaje{" +
                "id='" + id + '\'' +
                ", cliente='" + cliente + '\'' +
                ", tipo=" + tipo +
                ", esSpam=" + esSpam +
                ", tiempoCuarentena=" + tiempoCuarentena +
                '}';
    }
}