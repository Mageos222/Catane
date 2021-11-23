import GameEngine.GameObject;
import GameEngine.Vector2;
import GameEngine.UI;
import GameEngine.BoxCollider;
import GameEngine.CircleCollider;
import GameEngine.Renderer;
import GameEngine.FPSCounter;
import GameEngine.SpriteRenderer;
import GameEngine.TextRenderer;

import java.util.List;
import java.util.Random;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.beans.Visibility;

public class Game extends Thread {
    
    private Player[] players;
    private int turn;
    private boolean turnAction;

    private Map map;
    private int size;
    private UI ui;

    private GameObject tower;
    
    private boolean addObject = false;

    private FPSCounter fps;
    private LoadingPage loading;

    private GameObject[] profils;
    private GameObject[][] ressourceText;

    private int nbTurn;
    private boolean canBuildVillage;
    private boolean canBuildRoad;
    private boolean ressourceDistrib;

    private final static Ressource roadCost = new Ressource(0, 1, 0, 0, 1);
    private final static Ressource villageCost = new Ressource(1, 1, 1, 0, 1);
    private final static Ressource townCost = new Ressource(2, 0, 0, 3, 0);

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

        ui = new UI(1920, 1080);
        ui.setDimension(width, height);
        ui.setLocation(posX, posY);
        ui.setVisible(false);

        loading = new LoadingPage(posX, posY, width, height);
        loading.start();

        canBuildRoad = true;
        canBuildVillage = true;
    }

    @Override
    public void run() {
        this.map = new Map(size, ui, this);

        String[] towers = {"Images/Colonies/townRed.png","Images/Colonies/townBlue.png", 
            "Images/Colonies/townGreen.png", "Images/Colonies/townYellow.png"};
        tower = new GameObject(towers, 40, 40);

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

            if(nbTurn == 2*players.length && !ressourceDistrib) {
                ressourceDistrib = true;
                for(Player player : players)
                    player.collect();
                updateText();
            }
            Random rnd = new Random();
            if(turn == 0 && !turnAction && nbTurn >= 2*players.length) {
                turnAction = true;
                diceAnim++;

                dice1Value = rnd.nextInt(6)+1;
                dice2Value = rnd.nextInt(6)+1;

                dice1.renderer().setVisible(true);
                dice2.renderer().setVisible(true);
                
                System.out.println("Value of dice : " + dice1Value + "+" + dice2Value + " => " + (dice1Value+dice2Value));
            }

            if(diceAnim > 0 && diceAnim < 20) {
                dice1.renderer().setImage(rnd.nextInt(6));
                dice2.renderer().setImage(rnd.nextInt(6));
                diceAnim++;
            }
            else if(diceAnim == 20) {
                dice1.renderer().setImage(dice1Value-1);
                dice2.renderer().setImage(dice2Value-1);
                diceAnim++;
            }
            else if(diceAnim > 0 && diceAnim < 50) diceAnim++;
            else if(diceAnim > 0) {
                diceAnim = 0;
                dice1.renderer().setVisible(false);
                dice2.renderer().setVisible(false);

                dice1Small.renderer().setImage(dice1Value-1);
                dice2Small.renderer().setImage(dice2Value-1);
                dice1Small.renderer().setVisible(true);
                dice2Small.renderer().setVisible(true);

                for(Player player : players)
                    player.collect(dice1Value+dice2Value);
                updateText();
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

    private void updateText() {
        for(int j = 0; j < players.length; j++)
            for(int i = 0; i < 5; i++) 
                ressourceText[j][i].renderer().setImages(String.valueOf(players[j].getRessource(i)));
    }

    public void addEmptyVillage(int posX, int posY, int x, int y) {
        String[] houses = {"Images/Colonies/villageRed.png", "Images/Colonies/villageBlue.png", 
            "Images/Colonies/villageGreen.png", "Images/Colonies/villageYellow.png"};
        GameObject empty = new GameObject(houses, 70, 70);
        empty.transform().setPosition(posX, posY);
        empty.renderer().setZindex(4);
        empty.renderer().setVisible(false);

        empty.addComponent(new CircleCollider(empty));
        empty.collider().setOnHoverEnterAction(() -> snap(empty));
        empty.collider().setOnHoverExitAction(() -> unsnap(empty));
        empty.collider().setOnMouseClickedAction(() -> build(empty, x, y, true, true));
        ui.add(empty);
    }

    public void addEmptyRoad(int posX, int posY, int x, int y, int i) {
        String[][] imgFiles = { {"Images/Colonies/RoadRightRed.png", "Images/Colonies/RoadRightBlue.png", 
            "Images/Colonies/RoadRightGreen.png", "Images/Colonies/RoadRightYellow.png"}, 
        {"Images/Colonies/RoadLeftRed.png", "Images/Colonies/RoadLeftBlue.png", 
            "Images/Colonies/RoadLeftGreen.png", "Images/Colonies/RoadLeftYellow.png" }, 
        {"Images/Colonies/RoadRed.png", "Images/Colonies/RoadBlue.png", 
            "Images/Colonies/RoadGreen.png", "Images/Colonies/RoadYellow.png" }};

        GameObject emptyRoad = new GameObject(imgFiles[i], 90, 90);
        emptyRoad.transform().setPosition(posX, posY);
        emptyRoad.renderer().setVisible(false);
        emptyRoad.renderer().setZindex(3);

        emptyRoad.addComponent(new CircleCollider(emptyRoad));

        emptyRoad.collider().setOnHoverEnterAction(() -> snap(emptyRoad));
        emptyRoad.collider().setOnHoverExitAction(() -> unsnap(emptyRoad));
        emptyRoad.collider().setOnMouseClickedAction(() -> build(emptyRoad, x, y, false, false));
        ui.add(emptyRoad);
    }

    public void setNewObject() {
        addObject = true;
        ui.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public void build(GameObject object, int x, int y, boolean isVillage, boolean isTown) {
        if(!object.collider().isHover() || !addObject) return;

        if(!isTown && (canBuildRoad || players[turn].possesse(roadCost))) {
            if(!canBuildRoad) {
                players[turn].pay(roadCost);
                updateText();
            }
            canBuildRoad = false;
        }
        else if(isVillage && (canBuildVillage || players[turn].possesse(villageCost))) {
            if(!canBuildVillage) {
                players[turn].pay(villageCost);
                updateText();
            }
            canBuildVillage = false;
            players[turn].addColony(map.getColony(x, y));
            System.out.println("Colony (" + x+";"+y+"):\n"+map.getColony(x, y).toString());
        }
        else if(isTown && players[turn].possesse(townCost)) {
            players[turn].pay(townCost);
            map.getColony(x, y).upgrade();
            updateText();
        }
        else return;

        object.collider().setHover(false);
        addObject = false;

        if(isVillage) {
            BufferedImage[] images = {object.renderer().getImages()[object.renderer().getRenderIndex()],
                tower.renderer().getImages()[object.renderer().getRenderIndex()]};
            object.renderer().setImages(images);
            object.renderer().setImage(0);

            int actualTurn = turn;
            object.collider().setOnHoverEnterAction(() -> snapUpdate(object, actualTurn));
            object.collider().setOnHoverExitAction(() -> unsnapUpdate(object, actualTurn));
            object.collider().setOnMouseClickedAction(() -> build(object, x, y, false, true));
        }
        else {
            if(isTown) {
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
        if(canBuildRoad || canBuildVillage) return;

        this.profils[turn].transform().scale(0.8);

        this.turn = (turn+1) % players.length;
        this.nbTurn++;
        if(nbTurn < 2*players.length) {
            canBuildVillage = true;
            canBuildRoad = true;
        }

        this.profils[turn].transform().scale(1.2);
        this.turnAction = false;
    }

    private void drawCanvas() {
        this.profils = new GameObject[players.length];
        this.ressourceText = new GameObject[players.length][];

        this.profils[0] = new GameObject("Images/Profils/playerProfil1.png", 200, 200);
        this.profils[0].transform().setPosition(-825, 375);
        this.profils[0].renderer().setAlign(Renderer.Align.TOP_LEFT);
        ui.add(this.profils[0]);
        addRessourceText(0, -675, 375, Renderer.Align.TOP_LEFT);

        this.profils[1] = new GameObject("Images/Profils/playerProfil2.png", 200, 200);
        this.profils[1].transform().setPosition(825, 375);
        this.profils[1].renderer().setAlign(Renderer.Align.TOP_RIGHT);
        ui.add(this.profils[1]);
        addRessourceText(1, 675, 375, Renderer.Align.TOP_RIGHT);

        this.profils[2] = new GameObject("Images/Profils/playerProfil3.png", 200, 200);
        this.profils[2].transform().setPosition(-825, -375);
        this.profils[2].renderer().setAlign(Renderer.Align.BOTTOM_LEFT);
        ui.add(this.profils[2]);
        addRessourceText(2, -675, -375, Renderer.Align.BOTTOM_LEFT);

        if(players.length == 4) {
            this.profils[3] = new GameObject("Images/Profils/playerProfil4.png", 200, 200);
            this.profils[3].transform().setPosition(825, -375);
            this.profils[3].renderer().setAlign(Renderer.Align.BOTTOM_RIGHT);
            ui.add(this.profils[3]);
            addRessourceText(3, 675, -375, Renderer.Align.BOTTOM_RIGHT);
        }
        this.profils[0].transform().scale(1.2);

        GameObject build = new GameObject("Images/GamePage/Hammer.png", 75, 75);
        build.transform().setPosition(0, -450);
        build.renderer().setZindex(2);
        build.renderer().setAlign(Renderer.Align.BOTTOM);
        build.addComponent(new BoxCollider(build));

        build.collider().setOnHoverEnterAction(() -> focus(build, 20));
        build.collider().setOnHoverExitAction(() -> unfocus(build, 20));
        build.collider().setOnMouseClickedAction(this::setNewObject);

        ui.add(build);

        GameObject next = new GameObject("Images/GamePage/nextButton.png", 75, 75);
        next.transform().setPosition(150, -450);
        next.renderer().setZindex(2);
        next.renderer().setAlign(Renderer.Align.BOTTOM);
        next.addComponent(new BoxCollider(next));

        next.collider().setOnHoverEnterAction(() -> focus(next, 20));
        next.collider().setOnHoverExitAction(() -> unfocus(next, 20));
        next.collider().setOnMouseClickedAction(this::nextTurn);

        ui.add(next);

        GameObject pause = new GameObject("Images/GamePage/Pause.png", 75, 75);
        pause.transform().setPosition(0, 450);
        pause.renderer().setZindex(2);
        pause.renderer().setAlign(Renderer.Align.TOP);
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

        String[] dice = new String[6];
        for(int i = 1; i <= 6; i++) dice[i-1] = "Images/GamePage/dice"+i+".png";

        dice1 = new GameObject(dice, 200, 200);
        dice1.transform().setPosition(-150, 0);
        dice1.renderer().setZindex(9);
        dice2 = new GameObject(dice, 200, 200);
        dice2.transform().setPosition(150, 0);
        dice2.renderer().setZindex(9);

        dice1Small = new GameObject(dice, 75, 75);
        dice1Small.transform().setPosition(-750, 0);
        dice1Small.renderer().setZindex(9);
        dice1Small.renderer().setAlign(Renderer.Align.CENTER_LEFT);
        dice2Small = new GameObject(dice, 75, 75);
        dice2Small.transform().setPosition(-650, 0);
        dice2Small.renderer().setZindex(9);
        dice2Small.renderer().setAlign(Renderer.Align.CENTER_LEFT);

        dice1.renderer().setVisible(false);
        dice2.renderer().setVisible(false);
        dice1Small.renderer().setVisible(false);
        dice2Small.renderer().setVisible(false);

        ui.add(dice1);
        ui.add(dice2);
        ui.add(dice1Small);
        ui.add(dice2Small);

        GameObject costCard = new GameObject("Images/GamePage/costCard.png", 240, 310);
        costCard.transform().setPosition(750, 0);
        costCard.renderer().setAlign(Renderer.Align.CENTER_LEFT);
        costCard.renderer().setZindex(9);
        costCard.addComponent(new BoxCollider(costCard));
        costCard.collider().setOnHoverEnterAction(() -> focus(costCard, 100));
        costCard.collider().setOnHoverExitAction(() -> unfocus(costCard, 100));
        ui.add(costCard);

        ui.setBackground("Images/GamePage/Water.png");

    }

    private void addRessourceText(int index, int x, int y, Renderer.Align align) {
        String[] file = new String[] { "wheatIco.png", "wood.png", "sheepIco.png", "rock.png", "clay.png" };

        ressourceText[index] = new GameObject[5];
        for(int i = 0; i < 5; i++) {
            ressourceText[index][i] = new GameObject(50, 75);
            ressourceText[index][i].transform().setPosition(x-(x/Math.abs(x))*i*60, y-30);
            ressourceText[index][i].addComponent(new TextRenderer(ressourceText[0][i], "0"));
            ressourceText[index][i].renderer().setZindex(1);
            ressourceText[index][i].renderer().setAlign(align);
            ui.add(ressourceText[index][i]);

            GameObject icon = new GameObject("Images/GamePage/" + file[i], 50, 50);
            icon.transform().setPosition(x-(x/Math.abs(x))*i*60, y+30);
            icon.renderer().setZindex(1);
            icon.renderer().setAlign(align);
            ui.add(icon);
        }
    }

    public int getTurn() { return turn; }

    public static void main(String[] args) {
        MusicPlayer music = new MusicPlayer("Music/Music.wav");
        //music.loop();

        Home home = new Home(new Game(3));
        home.start();
    }
}
