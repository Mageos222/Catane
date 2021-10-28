public class Game {
    
    private Player[] players;
    private int turn;

    private Map map;

    public Game(Player[] players) {
        this.players = players;

        this.turn = 0;
        this.map = new Map();
    }

}
