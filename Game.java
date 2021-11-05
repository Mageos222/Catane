import java.util.List;
import java.util.concurrent.TimeUnit;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Game {
    
    private Player[] players;
    private int turn;

    private Map map;
    private UI ui;

    private static GameObject tower;
    
    private boolean addObject = false;

    public Game(Player[] players) {
        this.players = players;

        this.turn = 0;
        this.map = new Map();

        ui = new UI();

        String[] towers = {"Images/townRed.png","Images/townBlue.png", "Images/townGreen.png", "Images/townYellow.png"};
        tower = new GameObject(towers, 40, 40);

        /*GameObject center = new GameObject("./Images/Field.png", 100, 100);
        center.setPosition(0, 0);
        ui.add(center);

        GameObject forest = new GameObject("./Images/Forest.png", 100, 100);
        forest.setPosition(-44, -75);
        ui.add(forest);*/

        for(Tiles tile : map.getMap()) {
            GameObject obj = tile.getObject();
            obj.setZindex(1);

            //obj.setOnHoverEnterAction(i -> i.focus(obj));
            //obj.setOnHoverExitAction(i -> i.unfocus(obj));

            ui.add(tile.getObject());
        }

        for(int x = 0; x < 11; x++) {
            for(int y = 0; y < 6; y++) {
                if((y==0||y==5)&&(x<2||x>8)||(y==1||y==4)&&(x==0||x==10)) continue;
                
                String[] houses = {"Images/villageRed.png", "Images/villageBlue.png", "Images/villageGreen.png", "Images/villageYellow.png"};
                Interactable empty = new Interactable(houses, 40, 40);
                empty.setPosition(-49*(5-x), -75*(3-y)+(2*(y%2)-1)*10*((x%2)-((x+1)%2))+30);
                empty.setZindex(4);
                empty.setVisible(false);

                empty.setOnHoverEnterAction(() -> snap(empty));
                empty.setOnHoverExitAction(() -> unsnap(empty));
                empty.setOnMouseClickedAction(() -> addNewObject(empty, true, true));
                ui.add(empty);

                if(x < 8 || ((y == 1 || y == 4) && x < 9) || ((y == 2 || y == 3) && x < 10)) {
                    String[] img = {"Images/RoadRightRed.png", "Images/RoadLeftRed.png" };
                    Interactable emptyRoad = new Interactable(img[Math.abs(x%2-y%2)], 60, 60);
                    emptyRoad.setPosition(-49*(5-x)+25, -75*(3-y)+38);
                    emptyRoad.setVisible(false);
                    emptyRoad.setZindex(3);

                    emptyRoad.setOnHoverEnterAction(() -> snap(emptyRoad));
                    emptyRoad.setOnHoverExitAction(() -> unsnap(emptyRoad));
                    emptyRoad.setOnMouseClickedAction(() -> addNewObject(emptyRoad, false, false));
                    ui.add(emptyRoad);
                }

                if(x%2-(y+1)%2==0) {
                    Interactable emptyRoad2 = new Interactable("Images/RoadRed.png", 60, 60);
                    emptyRoad2.setPosition(-49*(5-x), -75*(3-y));
                    emptyRoad2.setZindex(3);
                    emptyRoad2.setVisible(false);

                    emptyRoad2.setOnHoverEnterAction(() -> snap(emptyRoad2));
                    emptyRoad2.setOnHoverExitAction(() -> unsnap(emptyRoad2));
                    emptyRoad2.setOnMouseClickedAction(() -> addNewObject(emptyRoad2, false, false));
                    ui.add(emptyRoad2);
                }
            }
        }

        Interactable button = new Interactable("Images/button.png", 150, 50);
        button.setPosition(250, 200);
        button.setZindex(2);

        button.setOnHoverEnterAction(() -> focus(button, 20));
        button.setOnHoverExitAction(() -> unfocus(button, 20));
        button.setOnMouseClickedAction(this::setNewObject);

        button.setDistanceToSquare();
        ui.add(button);

        ui.setBackground("Images/Water.png");

        ui.repaint();
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

    public static void main(String[] args) {
        Game game = new Game(new Player[] { new Player("Moi", false)});
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
            Image[] newSprite = {object.getImage(), tower.getImage(object.getRenderIndex())};
            BufferedImage[] files = {object.getFiles()[object.getRenderIndex()], tower.getFiles()[object.getRenderIndex()]};
            object.setImage(newSprite, files);

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
