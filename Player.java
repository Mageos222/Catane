import java.util.LinkedList;
import java.util.List;

public class Player {

    private final String name;
    private final boolean bot;
    private Ressource ressources;

    List<Colony> colonies;

    public Player(String name, boolean bot) {
        this.name = name;
        this.bot = bot;

        ressources = new Ressource();
        this.colonies = new LinkedList<>();
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
    public boolean possesse(Ressource r) { return ressources.contain(r); }
    public void pay(Ressource r) { ressources.remove(r); }

    @Override
    public String toString() {
        return "Player " + name + " : " + (bot?"Bot":"Human");
    }
}   