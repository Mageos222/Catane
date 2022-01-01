import java.util.Random;

public class Game {
    
    private Player[] players;
    private int turn;

    private Map map;
    private int size;

    private Canvas canvas;

    private int nbTurn;

    private int dice1Value;
    private int dice2Value;
    private int diceAnim = 0;

    private static final Ressource roadCost = new Ressource(0, 1, 0, 0, 1);
    private static final Ressource villageCost = new Ressource(1, 1, 1, 0, 1);
    private static final Ressource townCost = new Ressource(2, 0, 0, 3, 0);
    private static final Ressource cardCost = new Ressource(1, 0, 1, 1, 0);

    public Game(int size) {
        this.size = size;

        this.turn = 0;
    }

    public void init(Player[] players, Canvas canvas) {
        this.players = players;
        this.canvas = canvas;
    }

    public void run(Controller controller) {
        this.map = new Map(size, canvas);
        canvas.drawCanvas(players.length, size);

        for(Player player : players)
            if(player.isBot()) {
                Bot bot = (Bot)player;
                bot.setController(controller);
                bot.setMap(map);
            }
    }

    public void update() {
        playDiceAnim();
    }

    private void playDiceAnim() {
        canvas.playDiceAnim(diceAnim, dice1Value, dice2Value);
        if(diceAnim > 0) {
            if(diceAnim > 30) {
                for(Player player : players)
                    player.collect(dice1Value+dice2Value);
                updateText();
                diceAnim = -1;
            }
            diceAnim++;
        }
    }

    public void updateText() {
        for(int j = 0; j < players.length; j++)
            for(int i = 0; i < 5; i++) 
                canvas.getRessourceText()[j][i].renderer().setImages(String.valueOf(players[j].getRessource(i)));
    }

    public int changeTurn() {
        System.out.println("Turn " + nbTurn);
        canvas.getProfils()[turn].transform().scale(0.8);

        turn = (turn+1)%3;
        nbTurn++;
        
        canvas.getProfils()[turn].transform().scale(1.2);
        
        if(nbTurn == 2*players.length) {
            for(Player player : players)
                player.collect();
            updateText();

            for(Player player : players)
                if(player.isBot()) {
                    Bot bot = (Bot)player;
                    bot.stopInit();
                }
        }
        if(nbTurn >= 2*players.length) {
            Random rnd = new Random();

            diceAnim++;

            dice1Value = rnd.nextInt(6)+1;
            dice2Value = rnd.nextInt(6)+1;

            canvas.showDices();
                
            System.out.println("Value of dice : " + dice1Value + "+" + dice2Value + " => " + (dice1Value+dice2Value));
        }

        if(players[turn].isBot()) {
            Bot bot = (Bot)players[turn];
            //new Thread(bot).start();
        }

        return nbTurn < 2*players.length?-1:(dice1Value+dice2Value);
    }

    public void setRobber() {
        if(map != null) map.resetBlocked();
    }

    public boolean canBuildRoad(boolean free, int x1, int y1, int x2, int y2) {
        return (free || players[turn].possesse(roadCost)) && map.canBuildRoad(turn, y1, x1, y2, x2);
    }

    public boolean canBuildVillage(boolean free, int x, int y) {
        return (free && map.canBuildFirstVillage(turn, x, y)) || (players[turn].possesse(villageCost) && map.canBuildVillage(turn, x, y));
    }

    public boolean canBuildTown(int x, int y) {
        return players[turn].possesse(townCost);
    }

    public void addRoad(int x1, int y1, int x2, int y2, boolean pay) {
        if(pay) {
            players[turn].pay(roadCost);
            updateText();
        }
        map.buildRoad(turn, y1, x1, y2, x2);
    }

    public void addVillage(int x, int y, boolean pay) {
        if(pay) {
            players[turn].pay(villageCost);
            if(players[turn].increment(1)) canvas.showWinnerPannel(turn);
            updateText();
        }

        map.buildVillage(turn, x, y);

        players[turn].addColony(map.getColony(x, y));
        if(map.getColony(x, y).getPort() > -1) 
            players[turn].addPort(map.getColony(x, y).getPort());

        System.out.println("Colony (" + x+";"+y+"):\n"+map.getColony(x, y).toString());
    }

    public void addTown(int x, int y) {
        players[turn].pay(townCost);
        map.getColony(x, y).upgrade();
        addPoint();
        updateText();
    }

    public boolean canBuyCard() {
        if(players[turn].possesse(cardCost)) {
            players[turn].pay(cardCost);
            updateText();
            return true;
        }
        return false;
    }

    public void addPoint() {
        if(players[turn].increment(1)) canvas.showWinnerPannel(turn);
    }

    public int getTurn() { return turn; }
    public void setTurn(int t) { this.turn = t; }
    public int getNbTurn() { return this.nbTurn; }

    public Player[] getPlayer() { return players; }
    public Player getCurrentPlayer() { return this.players[turn]; }
    public Map getMap() { return this.map; }
    
    public int getSize() { return this.size; }

    public boolean isPlayingAnim() { return diceAnim > 0; }
}
