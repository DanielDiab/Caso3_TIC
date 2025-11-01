public class Mensaje {
    public enum Tipo { INICIO, NORMAL, FIN }

    private final String id;
    private final String cliente;
    private final Tipo tipo;
    private final boolean esSpam;
    private int tiempoCuarentena; 

    public Mensaje(String id, String cliente, Tipo tipo, boolean esSpam) {
        this(id, cliente, tipo, esSpam, 0);
    }

    public Mensaje(String id, String cliente, Tipo tipo, boolean esSpam, int tiempoCuarentena) {
        this.id = id;
        this.cliente = cliente;
        this.tipo = tipo;
        this.esSpam = esSpam;
        this.tiempoCuarentena = tiempoCuarentena;
    }

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