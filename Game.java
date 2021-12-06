import GameEngine.*;
import GameEngine.RessourceManager;

import java.util.List;
import java.util.Random;
import java.awt.*;

public class Game extends Thread {
    
    private Player[] players;
    private int turn;
    private boolean turnAction;

    private Map map;
    private int size;
    private UI ui;

    private Controller controller;
    private Canvas canvas;

    private LoadingPage loading;

    private int nbTurn;
    private boolean ressourceDistrib;

    private GameObject dice1;
    private GameObject dice2;
    private GameObject dice1Small;
    private GameObject dice2Small;

    private int dice1Value;
    private int dice2Value;
    private int diceAnim = 0;

    public Game(int size) {
        this.size = size;

        this.turn = 0;
    }

    public void init(Player[] players, int posX, int posY, int width, int height) {
        this.players = players;

        ui = new UI();
        ui.setDimension(width, height);
        ui.setLocation(posX, posY);
        ui.setVisible(false);

        controller = new Controller(this, ui);
        canvas = new Canvas(this, controller, ui);
        controller.setCanvas(canvas);

        loading = new LoadingPage(posX, posY, width, height);
        loading.start();
    }

    @Override
    public void run() {
        this.map = new Map(size, ui, canvas, controller);

        String[] dice = new String[6];
        for(int i = 1; i <= 6; i++) dice[i-1] = "Images/GamePage/dice"+i+".png";

        dice1 = new GameObject(dice, 200, 200);
        dice2 = new GameObject(dice, 200, 200);
        dice1Small = new GameObject(dice, 75, 75);
        dice2Small = new GameObject(dice, 75, 75);
        
        canvas.drawCanvas();

        FPSCounter fps;
        fps = new FPSCounter(ui);
        fps.start();

        Random rnd = new Random();

        while(ui.isActive() || loading != null) {
            if(loading != null && fps.getFPS() > 0) {
                ui.setLocation(loading.getX(), loading.getY());

                loading.close();
                loading.interrupt();
                loading = null;

                ui.setVisible(true);
            }

            List<UI.Event> events = ui.nextFrame();

            for(UI.Event event : events) {
                if(event == UI.Event.MOUSE_RIGHT_CLICK) {
                    controller.setAddObject(false);
                    ui.setCursor(Cursor.getDefaultCursor());
                }
            }

            if(nbTurn == 2*players.length && !ressourceDistrib) {
                ressourceDistrib = true;
                for(Player player : players)
                    player.collect();
                controller.updateText();
            }

            if(!turnAction && nbTurn >= 2*players.length) {
                turnAction = true;
                diceAnim++;

                dice1Value = rnd.nextInt(6)+1;
                dice2Value = rnd.nextInt(6)+1;

                dice1.renderer().setVisible(true);
                dice2.renderer().setVisible(true);
                    
                System.out.println("Value of dice : " + dice1Value + "+" + dice2Value + " => " + (dice1Value+dice2Value));
                if(dice1Value+dice2Value == 7) {
                    controller.setVoleur(true);
                }
            }

            playDiceAnim();
            
            try {
                sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        fps.interrupt();
        System.out.println("Game closed");
    }

    private void playDiceAnim() {
        Random rnd = new Random();
        if(diceAnim > 0) {
            if(diceAnim < 15) {
                dice1.renderer().setImage(rnd.nextInt(6));
                dice2.renderer().setImage(rnd.nextInt(6));
            }
            else if(diceAnim == 15) {
                dice1.renderer().setImage(dice1Value-1);
                dice2.renderer().setImage(dice2Value-1);
            }
            else if(diceAnim > 30) {
                diceAnim = -1;
                dice1.renderer().setVisible(false);
                dice2.renderer().setVisible(false);

                dice1Small.renderer().setImage(dice1Value-1);
                dice2Small.renderer().setImage(dice2Value-1);
                dice1Small.renderer().setVisible(true);
                dice2Small.renderer().setVisible(true);

                for(Player player : players)
                    player.collect(dice1Value+dice2Value);
                controller.updateText();
            }
            diceAnim++;
        }
    }

    public int getTurn() { return turn; }
    public void setTurn(int t) { this.turn = t; }
    public int getNbTurn() { return this.nbTurn; }
    public void increment() { this.nbTurn++; }

    public Player[] getPlayer() { return players; }
    public Map getMap() { return this.map; }
    
    public void setTurnAction(boolean v) {this.turnAction = v;}

    public int getSize() { return this.size; }

    public GameObject getDice1() { return this.dice1; }
    public GameObject getDice2() { return this.dice2; }
    public GameObject getDice1Small() { return this.dice1Small; }
    public GameObject getDice2Small() { return this.dice2Small; }

    public static void main(String[] args) {
        MusicPlayer music = new MusicPlayer("Music/Music.wav");
        //music.loop();

        RessourceManager.load("Images");

        Home home = new Home(new Game(3));
        home.start();
    }
}
