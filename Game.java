import java.util.concurrent.TimeUnit;

public class Game {
    
    private Player[] players;
    private int turn;

    private Map map;
    private UI ui;

    public Game(Player[] players) {
        this.players = players;

        this.turn = 0;
        this.map = new Map();

        ui = new UI();

        /*GameObject center = new GameObject("./Images/Field.png", 100, 100);
        center.setPosition(0, 0);
        ui.add(center);

        GameObject forest = new GameObject("./Images/Forest.png", 100, 100);
        forest.setPosition(-44, -75);
        ui.add(forest);*/

        for(Tiles tile : map.getMap()) {
            GameObject obj = tile.getObject();
            obj.setInteractable(true);
            obj.setZindex(1);

            //obj.setOnHoverEnterAction(i -> i.focus(obj));
            //obj.setOnHoverExitAction(i -> i.unfocus(obj));

            ui.add(tile.getObject());
        }

        for(int x = 0; x < 11; x++) {
            for(int y = 0; y < 6; y++) {
                if((y==0||y==5)&&(x<2||x>8)||(y==1||y==4)&&(x==0||x==10)) continue;
                
                String[] houses = {"Images/villageRed.png", "Images/villageBlue.png", "Images/villageGreen.png", "Images/villageYellow.png"};
                GameObject empty = new GameObject(houses, 40, 40);
                empty.setPosition(-49*(5-x), -75*(3-y)+(2*(y%2)-1)*10*((x%2)-((x+1)%2))+30);
                empty.setZindex(4);
                empty.setVisible(false);

                empty.setInteractable(true);

                empty.setOnHoverEnterAction(i -> i.snap(empty));
                empty.setOnHoverExitAction(i -> i.unsnap(empty));
                empty.setOnMouseClickedAction(i -> i.addNewObject(empty, true));
                ui.add(empty);

                if(x < 8 || ((y == 1 || y == 4) && x < 9) || ((y == 2 || y == 3) && x < 10)) {
                    String[] img = {"Images/RoadRightRed.png", "Images/RoadLeftRed.png" };
                    GameObject emptyRoad = new GameObject(img[Math.abs(x%2-y%2)], 60, 60);
                    emptyRoad.setPosition(-49*(5-x)+25, -75*(3-y)+38);
                    emptyRoad.setVisible(false);
                    emptyRoad.setZindex(3);

                    emptyRoad.setInteractable(true);

                    emptyRoad.setOnHoverEnterAction(i -> i.snap(emptyRoad));
                    emptyRoad.setOnHoverExitAction(i -> i.unsnap(emptyRoad));
                    emptyRoad.setOnMouseClickedAction(i -> i.addNewObject(emptyRoad, false));
                    ui.add(emptyRoad);
                }

                if(x%2-(y+1)%2==0) {
                    GameObject emptyRoad2 = new GameObject("Images/RoadRed.png", 60, 60);
                    emptyRoad2.setPosition(-49*(5-x), -75*(3-y));
                    emptyRoad2.setZindex(3);
                    emptyRoad2.setVisible(false);

                    emptyRoad2.setInteractable(true);

                    emptyRoad2.setOnHoverEnterAction(i -> i.snap(emptyRoad2));
                    emptyRoad2.setOnHoverExitAction(i -> i.unsnap(emptyRoad2));
                    emptyRoad2.setOnMouseClickedAction(i -> i.addNewObject(emptyRoad2, false));
                    ui.add(emptyRoad2);
                }
            }
        }

        GameObject button = new GameObject("Images/button.png", 150, 50);
        button.setInteractable(true);
        button.setPosition(250, 200);
        button.setZindex(2);

        button.setOnHoverEnterAction(i -> i.focus(button, 20));
        button.setOnHoverExitAction(i -> i.unfocus(button, 20));
        button.setOnMouseClickedAction(i -> i.setNewObject(new GameObject("Images/HouseRed.png", 30, 30)));

        button.setDistanceToSquare();
        ui.add(button);

        ui.repaint();

        while(ui.isActive()) {
            ui.analyse();
            
            try {
                TimeUnit.MILLISECONDS.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        Game game = new Game(new Player[] { new Player("Moi", false)});
    }

}
