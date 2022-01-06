import java.util.concurrent.TimeUnit;

import GameEngine.BoxCollider;
import GameEngine.CircleCollider;
import GameEngine.GameObject;
import GameEngine.Renderer;
import GameEngine.Transform;
import GameEngine.UI;

import java.awt.*;

public class Home {
    private UI ui;

    private GameObject newButton;
    private GameObject continueButton;

    private GameObject profil1;
    private GameObject profil2;
    private GameObject profil3;
    private GameObject profil4;

    private GameObject playButton;
    private GameObject backButton;
    private GameObject plusButton;
    private GameObject minusButton;

    private GameObject[] botButtons;

    private int nbPlayer = 3;
    private boolean[] bot;

    private boolean closed = false;

    public Home(Transform run) {
        ui = new UI();
        ui.setDimension(run.getSize().getX(), run.getSize().getY());
        ui.setLocation(run.getPosition().getX(), -run.getPosition().getY());
        ui.setBackground("Images/Buttons/WoodBack.jpg");

        init(run);
    }

    public Home(Transform run, int w, int h, int x, int y) {
        ui = new UI();
        ui.setDimension(w, h);
        ui.setLocation(x, y);
        ui.setBackground("Images/Buttons/WoodBack.jpg");

        init(run);
    }
    
    public void init(Transform run) {
        newButton = new GameObject(new String[] {"Images/Buttons/button1-1.png", "Images/Buttons/button1-2.png"}, 300, 100);
        newButton.transform().setPosition(0, 100);

        newButton.addComponent(new BoxCollider(newButton));
        newButton.collider().setOnHoverEnterAction(() -> {
            newButton.renderer().setImage(1);
            ui.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        });
        newButton.collider().setOnHoverExitAction(() -> {
            newButton.renderer().setImage(0);
            ui.setCursor(Cursor.getDefaultCursor());
        });
        newButton.collider().setOnMouseClickedAction(() -> changeScreen(true));

        continueButton = new GameObject(new String[] {"Images/Buttons/button2-1.png", "Images/Buttons/button2-2.png"}, 300, 100);
        continueButton.transform().setPosition(0, -100);

        profil1 = new GameObject("Images/Profils/Profil1.png", 300, 600);
        profil1.transform().setPosition(-800, 50);
        profil1.renderer().setVisible(false);

        profil2 = new GameObject("Images/Profils/Profil2.png", 300, 600);
        profil2.transform().setPosition(-400, 50);
        profil2.renderer().setVisible(false);

        profil3 = new GameObject("Images/Profils/Profil3.png", 300, 600);
        profil3.transform().setPosition(0, 50);
        profil3.renderer().setVisible(false);

        profil4 = new GameObject("Images/Profils/Profil4.png", 300, 600);
        profil4.transform().setPosition(400, 50);
        profil4.renderer().setVisible(false);

        playButton = new GameObject(new String[] { "Images/Buttons/Play1.png", "Images/Buttons/Play2.png" }, 300, 100);
        playButton.transform().setPosition(800,-400);
        playButton.renderer().setAlign(Renderer.Align.BOTTOM_RIGHT);
        playButton.renderer().setVisible(false);

        playButton.addComponent(new BoxCollider(playButton));
        playButton.collider().setOnHoverEnterAction(() -> {
            playButton.renderer().setImage(1);
            ui.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        });
        playButton.collider().setOnHoverExitAction(() -> {
            playButton.renderer().setImage(0);
            ui.setCursor(Cursor.getDefaultCursor());
        });
        playButton.collider().setOnMouseClickedAction(() -> startNewGame(ui, run));

        backButton = new GameObject(new String[] { "Images/Buttons/backButton2.png", "Images/Buttons/backButton.png" }, 300, 100);
        backButton.transform().setPosition(800, 400);
        backButton.renderer().setAlign(Renderer.Align.TOP_RIGHT);
        backButton.renderer().setVisible(false);

        backButton.addComponent(new BoxCollider(backButton));
        backButton.collider().setOnHoverEnterAction(() -> {
            backButton.renderer().setImage(1);
            ui.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        });
        backButton.collider().setOnHoverExitAction(() -> {
            backButton.renderer().setImage(0);
            ui.setCursor(Cursor.getDefaultCursor());
        });
        backButton.collider().setOnMouseClickedAction(() -> changeScreen(false));

        plusButton = new GameObject("Images/Buttons/button_add.png", 100, 100);
        plusButton.transform().setPosition(400, 50);
        plusButton.renderer().setVisible(false);

        plusButton.addComponent(new CircleCollider(plusButton));
        plusButton.collider().setOnHoverEnterAction(() -> plusButton.transform().scale(1.1));
        plusButton.collider().setOnHoverExitAction(() -> plusButton.transform().scale(0.9));
        plusButton.collider().setOnMouseClickedAction(this::addPlayer);

        minusButton = new GameObject("Images/Buttons/minusButton.png", 100, 100);
        minusButton.transform().setPosition(800, 50);
        minusButton.renderer().setVisible(false);

        minusButton.addComponent(new CircleCollider(minusButton));
        minusButton.collider().setOnHoverEnterAction(() -> minusButton.transform().scale(1.1));
        minusButton.collider().setOnHoverExitAction(() -> minusButton.transform().scale(0.9));
        minusButton.collider().setOnMouseClickedAction(this::removePlayer);

        botButtons = new GameObject[4];
        for(int i = 0; i < 4; i++) {
            botButtons[i] = new GameObject(new String[] {"Images/Buttons/playerButton1.png", "Images/Buttons/PlayerButton2png.png", 
                                                    "Images/Buttons/BotButton1.png", "Images/Buttons/BotButton2.png"}, 300, 100);
            botButtons[i].transform().setPosition(-800+400*i, -300);
            botButtons[i].renderer().setVisible(false);

            botButtons[i].addComponent(new BoxCollider(botButtons[i]));
            GameObject obj = botButtons[i];
            int j = i;
            botButtons[i].collider().setOnHoverEnterAction(() -> obj.renderer().nextImage());
            botButtons[i].collider().setOnHoverExitAction(() -> obj.renderer().precImage());
            botButtons[i].collider().setOnMouseClickedAction(() -> switchBot(j));
            botButtons[i].collider().setActiv(false);

            ui.add(botButtons[i]);
        }

        ui.add(newButton);
        ui.add(continueButton);

        ui.add(profil1);
        ui.add(profil2);
        ui.add(profil3);
        ui.add(profil4);

        ui.add(plusButton);
        ui.add(minusButton);

        ui.add(playButton);
        ui.add(backButton);

        bot = new boolean[4];  
    }

    public Player[] run() {
        while(!closed) {
            ui.nextFrame();
            
            try {
                TimeUnit.MILLISECONDS.sleep(50);
            } catch (InterruptedException e) {
                break;
            }
        }
        Player[] players = new Player[nbPlayer];
        for(int i = 0; i < nbPlayer; i++) 
            players[i] = bot[i]?new Bot(i):new Player(i);

        System.out.println("Home closed");
        return players;
    }

    public void changeScreen(boolean state) {
        MusicPlayer button = new MusicPlayer("Music/button.wav");
        button.play();

        removePlayer();

        this.newButton.renderer().setVisible(!state);
        this.newButton.collider().setActiv(!state);
        this.continueButton.renderer().setVisible(!state);

        this.profil1.renderer().setVisible(state);
        this.profil2.renderer().setVisible(state);
        this.profil3.renderer().setVisible(state);

        this.plusButton.renderer().setVisible(state);
        this.plusButton.collider().setActiv(state);

        this.playButton.renderer().setVisible(state);
        this.playButton.collider().setActiv(state);
        this.backButton.renderer().setVisible(state);
        this.backButton.collider().setActiv(state);

        for(int i = 0; i < 3; i++) {
            botButtons[i].renderer().setVisible(state);
            botButtons[i].collider().setActiv(state);
        }
    }

    public void addPlayer() {
        plusButton.renderer().setVisible(false);
        plusButton.collider().setActiv(false);

        minusButton.renderer().setVisible(true);
        minusButton.collider().setActiv(true);

        profil4.renderer().setVisible(true);
        botButtons[3].renderer().setVisible(true);
        botButtons[3].collider().setActiv(true);

        nbPlayer = 4;
    }

    public void removePlayer() {
        plusButton.renderer().setVisible(true);
        plusButton.collider().setActiv(true);

        minusButton.renderer().setVisible(false);
        minusButton.collider().setActiv(false);

        profil4.renderer().setVisible(false);
        botButtons[3].renderer().setVisible(false);
        botButtons[3].collider().setActiv(false);

        nbPlayer = 3;
    }

    public void switchBot(int i) {
        MusicPlayer button = new MusicPlayer("Music/button.wav");
        button.play();
        
        botButtons[i].renderer().setImage((botButtons[i].renderer().getRenderIndex()+2)%4);
        bot[i] = !bot[i];
    }

    public void startNewGame(UI ui, Transform run) {
        MusicPlayer button = new MusicPlayer("Music/button.wav");
        button.play();

        int width = ui.getWidth();
        int height = ui.getHeight();

        int posX = ui.getPosX();
        int posY = ui.getPosY();

        closed = true;

        run.setSize(width, height);
        run.setPosition(posX, posY);
    }

    public void setVisible(boolean v) { 
        ui.setVisible(v);
        ui.nextFrame();
    }
}
