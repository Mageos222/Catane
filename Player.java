import java.util.List;

public class Player {

    private final String name;
    private final boolean bot;
    private Ressource ressources;

    List<Colony> colonies;

    public Player(String name, boolean bot) {
        this.name = name;
        this.bot = bot;
    }

    @Override
    public String toString() {
        return "Player " + name + " : " + (bot?"Bot":"Human");
    }
}   