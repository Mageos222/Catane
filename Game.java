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
            obj.setZindex(1);

            obj.setScale(tileSize, tileSize);
            obj.setPosition(obj.getCenterX()*xOffset, obj.getCenterY()*yOffset);

            //obj.setOnHoverEnterAction(i -> i.focus(obj));
            //obj.setOnHoverExitAction(i -> i.unfocus(obj));

            ui.add(tile.getObject());
        }

        /*Interactable test = new Interactable("Images/Clay.png", 70, 70);
        test.setZindex(10);
        test.setPosition(100, 100);
        test.setOnHoverEnterAction(() -> snap(test));
        test.setOnHoverExitAction(() -> unsnap(test));
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

        Interactable button = new Interactable("Images/button.png", 300, 100);
        button.setPosition(700, 400);
        button.setZindex(2);

        button.setOnHoverEnterAction(() -> focus(button, 20));
        button.setOnHoverExitAction(() -> unfocus(button, 20));
        button.setOnMouseClickedAction(this::setNewObject);

        button.setDistanceToSquare();
        button.setAlign(GameObject.Align.BottomRight);
        ui.add(button);

        ui.setBackground("Images/Water.png");
    }

    public void startGame() {
        while(ui.isActive()) {
            List<UI.Event> events = ui.nextFrame();

            for(UI.Event event : events) {
                if(event == UI.Event.MOUSE_RIGHT_CLICK) {
                    addObject = false;
                    ui.setCursor(Cursor.DEFAULT_CURSOR);
                }
            }
            
            try {
                TimeUnit.MILLISECONDS.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void addEmptyVillage(int x, int y) {
        String[] houses = {"Images/villageRed.png", "Images/villageBlue.png", "Images/villageGreen.png", "Images/villageYellow.png"};
        Interactable empty = new Interactable(houses, 70, 70);
        empty.setPosition(x, y);
        empty.setZindex(4);
        empty.setVisible(false);

        empty.setOnHoverEnterAction(() -> snap(empty));
        empty.setOnHoverExitAction(() -> unsnap(empty));
        empty.setOnMouseClickedAction(() -> addNewObject(empty, true, true));
        ui.add(empty);
    }

    public void addEmptyRoad(int x, int y, int i) {
        String[] img = {"Images/RoadRightRed.png", "Images/RoadLeftRed.png", "Images/RoadRed.png" };
        
        Interactable emptyRoad = new Interactable(img[i], 90, 90);
        emptyRoad.setPosition(x, y);
        emptyRoad.setVisible(false);
        emptyRoad.setZindex(3);

        emptyRoad.setOnHoverEnterAction(() -> snap(emptyRoad));
        emptyRoad.setOnHoverExitAction(() -> unsnap(emptyRoad));
        emptyRoad.setOnMouseClickedAction(() -> addNewObject(emptyRoad, false, false));
        ui.add(emptyRoad);
    }

    public static void main(String[] args) {
        Game game = new Game(new Player[] { new Player("Moi", false)}, 3);
        game.startGame();
    }

    public void setNewObject() {
        addObject = true;
        ui.setCursor(Cursor.HAND_CURSOR);
    }

    public void addNewObject(Interactable object, boolean isUpdatable, boolean isFocusable) {
        if(!object.isHover() || !addObject) return;
        object.setHover(false);
        addObject = false;

        if(isUpdatable) {
            BufferedImage[] images = {object.getFiles()[object.getRenderIndex()], tower.getFiles()[object.getRenderIndex()]};
            object.setImage(images);

            object.setOnHoverEnterAction(() -> snapUpdate(object));
            object.setOnHoverExitAction(() -> unsnapUpdate(object));
            object.setOnMouseClickedAction(() -> addNewObject(object, false, true));
        }
        else {
            if(isFocusable) {
                object.setOnHoverEnterAction(() -> focus(object, 10));
                object.setOnHoverExitAction(() -> unfocus(object, 10));
            }
            else {
                object.setOnHoverEnterAction(() -> { });
                object.setOnHoverExitAction(() -> { });
            }
            object.setOnMouseClickedAction(() -> { });
        }

        ui.setCursor(Cursor.DEFAULT_CURSOR);
    }

    public void focus(Interactable object, int size) {
        object.scale(size, size);
        object.setHover(true);
        object.setZindex(object.getZindex()+5);
    }

    public void unfocus(Interactable object, int size) {
        object.scale(-size, -size);
        object.setHover(false);
        object.setZindex(object.getZindex()-5);
    } 

    public void snap(Interactable object) {
        if(!addObject) return;
        object.setHover(true);

        object.setVisible(true);
    }

    public void unsnap(Interactable object) {
        object.setVisible(false);
        object.setHover(false);
    }

    public void snapUpdate(Interactable object) {
        if(!addObject) {
            focus(object, 10);
            return; 
        }
        object.setHover(true);
        object.nextImage();
    }

    public void unsnapUpdate(Interactable object) {
        if(!addObject) unfocus(object, 10);

        object.setHover(false);
        object.setImage(0);
    }

}
