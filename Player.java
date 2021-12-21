import java.util.LinkedList;

public class Player {

    private final String name;
    private final boolean bot;
    private Ressource ressources;

    private int score;

    LinkedList<Colony> colonies;

    public Player(String name, boolean bot) {
        this.name = name;
        this.bot = bot;

        ressources = new Ressource();
        this.colonies = new LinkedList<>();

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

    public boolean isBlocked() { 
        for(Colony col : colonies) 
            if(col.isBlocked()) return true;
        return false;
     }

    public void increment(int i) { 
        score += i; 
        if(win()) System.out.println(name + " win");
    } 
    public boolean win() { return score >= 10; }

    @Override
    public String toString() {
        return "Player " + name + " : " + (bot?"Bot":"Human");
    }
}   