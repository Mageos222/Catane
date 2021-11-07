import GameEngine.GameObject;
import GameEngine.Vector2;
import GameEngine.UI;
import GameEngine.BoxCollider;
import GameEngine.CircleCollider;
import GameEngine.Renderer;
import GameEngine.FPSCounter;
import GameEngine.SpriteRenderer;

import java.util.List;
import java.util.concurrent.TimeUnit;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Game {
    
    private Player[] players;
    private int turn;

    private Map map;
    private int size;
    private UI ui;

    private static GameObject tower;
    
    private boolean addObject = false;

    private FPSCounter fps;

    public Game(Player[] players, int size) {
        this.players = players;

        this.turn = 0;
        this.size = size;
        this.map = new Map(size);

        ui = new UI(1920, 1080);
        ui.setDimension(720, 480);

        String[] towers = {"Images/townRed.png","Images/townBlue.png", "Images/townGreen.png", "Images/townYellow.png"};
        tower = new GameObject(towers, 40, 40);

        /*GameObject center = new GameObject("./Images/Field.png", 100, 100);
        center.setPosition(0, 0);
        ui.add(center);

        GameObject forest = new GameObject("./Images/Forest.png", 100, 100);
        forest.setPosition(-44, -75);
        ui.add(forest);*/

        int tileSize = 175;
        int xOffset = (int)(0.5f*tileSize);
        int yOffset = (int)(0.74f*tileSize);

        for(Tiles tile : map.getMap()) {
            GameObject obj = tile.getObject();
            obj.renderer().setZindex(1);

            obj.transform().setSize(tileSize, tileSize);
            obj.transform().setPosition(Vector2.multiply(obj.transform().getPosition(), new Vector2(xOffset, yOffset)));

            //obj.setOnHoverEnterAction(i -> i.focus(obj));
            //obj.setOnHoverExitAction(i -> i.unfocus(obj));

            ui.add(tile.getObject());
        }

        /*GameObject test = new GameObject("Images/Clay.png", 300, 300);
        test.setZindex(10);
        test.setPosition(100, 100);
        test.addComponent(new CircleCollider(test));
        test.getCollider().setOnHoverEnterAction(() -> System.out.println("hover"));
        test.getCollider().setOnHoverExitAction(() -> System.out.println("exit"));
        ui.add(test);*/

        int yShift = (int)(0.1f*tileSize);
        int roadType = 1;

        for(int y = 1; y <= size; y++) {
            for(int x = -2*size+y; x <= 2*size-y; x++) {
                addEmptyVillage(x*xOffset, (int)((y-0.5f)*yOffset-yShift));
                addEmptyVillage(x*xOffset, -(int)((y-0.5f)*yOffset-yShift));
                yShift = -yShift;

                if(x != 2*size-y) {
                    addEmptyRoad(x*xOffset+xOffset/2, (int)((y-0.5f)*yOffset), roadType);
                    addEmptyRoad(x*xOffset+xOffset/2, -(int)((y-0.5f)*yOffset), (roadType+1)%2);
                }
                if(roadType == 0 && y != size) addEmptyRoad(x*xOffset, (int)((y-0.5f)*yOffset)+yOffset/2, 2);
                else if(roadType == 1) addEmptyRoad(x*xOffset, -(int)((y-0.5f)*yOffset)+yOffset/2, 2);
                roadType = (roadType+1)%2;
            }
            yShift = -yShift;
            roadType = 1;
        }

        GameObject button = new GameObject("Images/button.png", 300, 100);
        button.transform().setPosition(700, 400);
        button.renderer().setZindex(2);
        button.addComponent(new BoxCollider(button));

        button.collider().setOnHoverEnterAction(() -> focus(button, 20));
        button.collider().setOnHoverExitAction(() -> unfocus(button, 20));
        button.collider().setOnMouseClickedAction(this::setNewObject);

        button.renderer().setAlign(Renderer.Align.BOTTOM_RIGHT);
        ui.add(button);

        ui.setBackground("Images/Water.png");
    }

    public void startGame() {
        fps = new FPSCounter(ui);
        fps.start();

        while(ui.isActive()) {
            List<UI.Event> events = ui.nextFrame();

            for(UI.Event event : events) {
                if(event == UI.Event.MOUSE_RIGHT_CLICK) {
                    addObject = false;
                    ui.setCursor(Cursor.getDefaultCursor());
                }
            }
            
            try {
                TimeUnit.MILLISECONDS.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        fps.interrupt();
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
        String[] img = {"Images/RoadRightRed.png", "Images/RoadLeftRed.png", "Images/RoadRed.png" };
        
        GameObject emptyRoad = new GameObject(img[i], 90, 90);
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
        Game game = new Game(new Player[] { new Player("Moi", false)}, 3);
        game.startGame();
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

            object.collider().setOnHoverEnterAction(() -> snapUpdate(object));
            object.collider().setOnHoverExitAction(() -> unsnapUpdate(object));
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

        object.renderer().setVisible(true);
    }

    public void unsnap(GameObject object) {
        object.renderer().setVisible(false);
        object.collider().setHover(false);
    }

    public void snapUpdate(GameObject object) {
        if(!addObject) {
            focus(object, 10);
            return; 
        }
        object.collider().setHover(true);
        object.renderer().nextImage();
    }

    public void unsnapUpdate(GameObject object) {
        if(!addObject) unfocus(object, 10);

        object.collider().setHover(false);
        object.renderer().setImage(0);
    }

}
