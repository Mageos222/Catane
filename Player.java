import java.util.ArrayList;

public class Player {

    private final String name;
    protected final int number;
    protected Ressource ressources;

    private int score;

    protected ArrayList<Colony> colonies;
    private ArrayList<Port> ports;
    private ArrayList<Card> cards;

    private int longestRoad;
    private int bonus;

    public Player(int number) {
        this.number = number;
        this.name = (isBot()?"Bot ":"Player ")+(number+1);

        ressources = new Ressource();
        this.colonies = new ArrayList<>();
        this.ports = new ArrayList<>();
        this.cards = new ArrayList<>();

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
            if(col.isBlocked() != -1) return true;
        return false;
    }

    public boolean increment(int i) { 
        score += i; 
        return win();
    } 
    public boolean win() { return (score+bonus) >= 10; }
 
    public void addCard(Card card) { this.cards.add(card); }
    public ArrayList<Card> getCards() { return this.cards; }
    public void playCard(int i) {
        this.cards.get(i).use();
        this.cards.remove(i);
    }

    public int getScore() { return (this.score + this.bonus); }

    @Override
    public String toString() {
        return "Player " + name + " : " + (isBot()?"Bot":"Human");
    }

    public String getName() { return this.name; }

    public boolean isBot() { return false; }

    public ArrayList<Colony> getColonies() { return this.colonies; }

    public void setLongestRoad(int size) { this.longestRoad = size; }
    public int getLongestRoad() { return this.longestRoad; }
    public void setBonus(int b) { this.bonus = b; }
}   