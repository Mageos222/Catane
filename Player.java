import java.util.LinkedList;

public class Player {

    private final String name;
    protected final int number;
    protected Ressource ressources;

    private int score;

    protected LinkedList<Colony> colonies;
    private LinkedList<Port> ports;

    public Player(int number) {
        this.number = number;
        this.name = (isBot()?"Bot ":"Player ")+(number+1);

        ressources = new Ressource();
        this.colonies = new LinkedList<>();
        this.ports = new LinkedList<>();

        this.score = 0;
    }

    public void addColony(Colony c) { this.colonies.add(c); }

    public void collect(int value) {
        colonies.forEach(c -> ressources.add(c.collect(value)));
    }
    public void collect() {
        colonies.forEach(c -> ressources.add(c.collect()));
    }

    public int getRessource(int i) { 
        return ressources.getRessource(i); 
    }
    public Ressource getRessources() { return ressources; }

    public boolean possesse(Ressource r) { return ressources.contain(r); }
    public void pay(Ressource r) { ressources.remove(r); }
    public void receive(Ressource r) { ressources.add(r); }
    public void addPort(int port) {
        Port p = new Port(port, port==5?3:2);
        ports.add(p);
    }
    public Port[] getPorts() { 
        Object[] arr = this.ports.toArray(); 
        Port[] res = new Port[arr.length];
        for(int i = 0; i < arr.length; i++)
            res[i] = (Port)arr[i];
        return res;
    }
    public Port getPort(int i) { 
        for(Port port : ports) 
            if(port.getType() == i)
                return port;
        throw new NullPointerException();
    }

    public boolean isBlocked() { 
        for(Colony col : colonies) 
            if(col.isBlocked()) return true;
        return false;
    }

    public boolean increment(int i) { 
        score += i; 
        return win();
    } 
    public boolean win() { return score >= 10; }

    @Override
    public String toString() {
        return "Player " + name + " : " + (isBot()?"Bot":"Human");
    }

    public String getName() { return this.name; }

    public boolean isBot() { return false; }
}   