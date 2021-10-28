public class Player {

    private final String name;
    private final boolean bot;

    public Player(String name, boolean bot) {
        this.name = name;
        this.bot = bot;
    }

    @Override
    public String toString() {
        return "Player " + name + " : " + (bot?"Bot":"Human");
    }
}   