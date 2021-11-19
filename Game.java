import GameEngine.GameObject;
import GameEngine.Vector2;
import GameEngine.UI;
import GameEngine.BoxCollider;
import GameEngine.CircleCollider;
import GameEngine.Renderer;
import GameEngine.FPSCounter;
import GameEngine.SpriteRenderer;

import java.util.List;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Game extends Thread {
    
    private Player[] players;
    private int turn;

    private Map map;
    private int size;
    private UI ui;

    private static GameObject tower;
    
    private boolean addObject = false;

    private FPSCounter fps;
    private LoadingPage loading;

    private GameObject[] profils;

    public Game(int size) {
        this.size = size;

        this.turn = 0;
    }

    public void init(Player[] players, int posX, int posY, int width, int height) {
        this.players = players;

        ui = new UI(1920, 1080);
        ui.setDimension(width, height);
        ui.setLocation(posX, posY);
        ui.setVisible(false);

        loading = new LoadingPage(posX, posY, width, height);
        loading.start();
    }

    @Override
    public void run() {
        this.map = new Map(size, ui);

        String[] towers = {"Images/townRed.png","Images/townBlue.png", "Images/townGreen.png", "Images/townYellow.png"};
        tower = new GameObject(towers, 40, 40);

        int tileSize = 175;
        int xOffset = (int)(0.5f*tileSize);
        int yOffset = (int)(0.74f*tileSize);

        int yShift = (int)(0.1f*tileSize);
        int roadType = 1;
        
        for(int y = 1; y <= size; y++) {
            for(int x = -2*size+y; x <= 2*size-y; x++) {
                addEmptyVillage(x*xOffset, (int)((y-0.5f)*yOffset-yShift));
                addEmptyVillage(x*xOffset, -(int)((y-0.5f)*yOffset-yShift));
                yShift = -yShift;

                if(x != 2*size-y) {
                    addEmptyRoad(x*xOffset+xOffset/2, (int)((y-0.5f)*yOffset), (roadType+1)%2);
                    addEmptyRoad(x*xOffset+xOffset/2, -(int)((y-0.5f)*yOffset), roadType);
                }
                if(roadType == 0 && y != size) addEmptyRoad(x*xOffset, (int)((y-0.5f)*yOffset)+yOffset/2, 2);
                else if(roadType == 1) addEmptyRoad(x*xOffset, -(int)((y-0.5f)*yOffset)+yOffset/2, 2);
                roadType = (roadType+1)%2;
            }
            yShift = -yShift;
            roadType = 1;
        }

        drawCanvas();

        fps = new FPSCounter(ui);
        fps.start();

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
                    addObject = false;
                    ui.setCursor(Cursor.getDefaultCursor());
                }
            }
            
            try {
                sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        fps.interrupt();
        System.out.println("Game closed");
    }

    public void addEmptyVillage(int x, int y) {
        String[] houses = {"Images/villageRed.png", "Images/villageBlue.png", "Images/villageGreen.png", "Images/villageYellow.png"};
        GameObject empty = new GameObject(houses, 70, 70);
        empty.transform().setPosition(x, y);
        empty.renderer().setZindex(4);
        empty.renderer().setVisible(false);

        empty.addComponent(new CircleCollider(empty));
        empty.collider().setOnHoverEnterAction(() -> snap(empty));
        empty.collider().setOnHoverExitAction(() -> unsnap(empty));
        empty.collider().setOnMouseClickedAction(() -> addNewObject(empty, true, true));
        ui.add(empty);
    }

    public void addEmptyRoad(int x, int y, int i) {
        String[][] imgFiles = { {"Images/RoadRightRed.png", "Images/RoadRightBlue.png", "Images/RoadRightGreen.png", "Images/RoadRightYellow.png"}, 
        {"Images/RoadLeftRed.png", "Images/RoadLeftBlue.png", "Images/RoadLeftGreen.png", "Images/RoadLeftYellow.png" }, 
        {"Images/RoadRed.png", "Images/RoadBlue.png", "Images/RoadGreen.png", "Images/RoadYellow.png" }};

        GameObject emptyRoad = new GameObject(imgFiles[i], 90, 90);
        emptyRoad.transform().setPosition(x, y);
        emptyRoad.renderer().setVisible(false);
        emptyRoad.renderer().setZindex(3);

        emptyRoad.addComponent(new CircleCollider(emptyRoad));

        emptyRoad.collider().setOnHoverEnterAction(() -> snap(emptyRoad));
        emptyRoad.collider().setOnHoverExitAction(() -> unsnap(emptyRoad));
        emptyRoad.collider().setOnMouseClickedAction(() -> addNewObject(emptyRoad, false, false));
        ui.add(emptyRoad);
    }

    public static void main(String[] args) {
        //Game game = new Game(3);

        MusicPlayer music = new MusicPlayer("Music/Music.wav");
        music.loop();

        Home home = new Home(new Game(3));
        home.start();
    }

    public void setNewObject() {
        addObject = true;
        ui.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public void addNewObject(GameObject object, boolean isUpdatable, boolean isFocusable) {
        if(!object.collider().isHover() || !addObject) return;
        object.collider().setHover(false);
        addObject = false;

        if(isUpdatable) {
            BufferedImage[] images = {object.renderer().getImages()[object.renderer().getRenderIndex()],
                tower.renderer().getImages()[object.renderer().getRenderIndex()]};
            object.renderer().setImages(images);
            object.renderer().setImage(0);

            int actualTurn = turn;
            object.collider().setOnHoverEnterAction(() -> snapUpdate(object, actualTurn));
            object.collider().setOnHoverExitAction(() -> unsnapUpdate(object, actualTurn));
            object.collider().setOnMouseClickedAction(() -> addNewObject(object, false, true));
        }
        else {
            if(isFocusable) {
                object.collider().setOnHoverEnterAction(() -> focus(object, 10));
                object.collider().setOnHoverExitAction(() -> unfocus(object, 10));
            }
            else {
                object.collider().setOnHoverEnterAction(() -> { });
                object.collider().setOnHoverExitAction(() -> { });
            }
            object.collider().setOnMouseClickedAction(() -> { });
        }

        ui.setCursor(Cursor.getDefaultCursor());
    }

    public void focus(GameObject object, int size) {
        object.transform().scale(size, size);
        object.collider().setHover(true);
        object.renderer().setZindex(object.renderer().getZindex()+5);
    }

    public void unfocus(GameObject object, int size) {
        object.transform().scale(-size, -size);
        object.collider().setHover(false);
        object.renderer().setZindex(object.renderer().getZindex()-5);
    } 

    public void snap(GameObject object) {
        if(!addObject) return;
        object.collider().setHover(true);
        object.renderer().setImage(this.getTurn());

        object.renderer().setVisible(true);
    }

    public void unsnap(GameObject object) {
        object.renderer().setVisible(false);
        object.collider().setHover(false);
    }

    public void snapUpdate(GameObject object, int i) {
        if(!addObject || i != this.getTurn()) {
            focus(object, 10);
            return; 
        }
        object.collider().setHover(true);
        object.renderer().nextImage();
    }

    public void unsnapUpdate(GameObject object, int i) {
        if(!addObject || i != this.getTurn()) unfocus(object, 10);

        object.collider().setHover(false);
        object.renderer().setImage(0);
    }

    public void nextTurn() {
        this.profils[turn].transform().scale(0.8);
        turn = (turn+1) % players.length;
        this.profils[turn].transform().scale(1.2);
    }

    private void drawCanvas() {
        this.profils = new GameObject[players.length];

        this.profils[0] = new GameObject("Images/Profils/playerProfil1.png", 200, 200);
        this.profils[0].transform().setPosition(-825, 375);
        this.profils[0].renderer().setAlign(Renderer.Align.TOP_LEFT);
        ui.add(this.profils[0]);

        this.profils[1] = new GameObject("Images/Profils/playerProfil2.png", 200, 200);
        this.profils[1].transform().setPosition(825, 375);
        this.profils[1].renderer().setAlign(Renderer.Align.TOP_RIGHT);
        ui.add(this.profils[1]);

        this.profils[2] = new GameObject("Images/Profils/playerProfil3.png", 200, 200);
        this.profils[2].transform().setPosition(-825, -375);
        this.profils[2].renderer().setAlign(Renderer.Align.BOTTOM_LEFT);
        ui.add(this.profils[2]);

        if(players.length == 4) {
            this.profils[3] = new GameObject("Images/Profils/playerProfil4.png", 200, 200);
            this.profils[3].transform().setPosition(825, -375);
            this.profils[3].renderer().setAlign(Renderer.Align.BOTTOM_RIGHT);
            ui.add(this.profils[3]);
        }
        this.profils[0].transform().scale(1.2);

        GameObject build = new GameObject("Images/GamePage/Hammer.png", 75, 75);
        build.transform().setPosition(0, -450);
        build.renderer().setZindex(2);
        build.addComponent(new BoxCollider(build));

        build.collider().setOnHoverEnterAction(() -> focus(build, 20));
        build.collider().setOnHoverExitAction(() -> unfocus(build, 20));
        build.collider().setOnMouseClickedAction(this::setNewObject);

        ui.add(build);

        GameObject next = new GameObject("Images/GamePage/nextButton.png", 75, 75);
        next.transform().setPosition(150, -450);
        next.renderer().setZindex(2);
        next.addComponent(new BoxCollider(next));

        next.collider().setOnHoverEnterAction(() -> focus(next, 20));
        next.collider().setOnHoverExitAction(() -> unfocus(next, 20));
        next.collider().setOnMouseClickedAction(this::nextTurn);

        ui.add(next);

        GameObject pause = new GameObject("Images/GamePage/Pause.png", 75, 75);
        pause.transform().setPosition(0, 450);
        pause.renderer().setZindex(2);
        pause.addComponent(new CircleCollider(pause));

        pause.collider().setOnHoverEnterAction(() -> focus(pause, 20));
        pause.collider().setOnHoverExitAction(() -> unfocus(pause, 20));
        pause.collider().setOnMouseClickedAction(() -> {
            Home home = new Home(new Game(size), ui.getWidth(), ui.getHeight(), ui.getPosX(), ui.getPosY());
            home.start();
            ui.close();
            this.interrupt();
        });

        ui.add(pause);

        ui.setBackground("Images/Water.png");

    }

    public int getTurn() { return turn; }
}
