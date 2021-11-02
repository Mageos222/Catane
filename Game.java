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
                
                GameObject empty = new GameObject(30, 30);
                empty.setPosition(-44*(5-x), -75*(3-y)+(2*(y%2)-1)*14*((x%2)-((x+1)%2))+38);
                empty.setZindex(3);

                empty.setInteractable(true);

                empty.setOnHoverEnterAction(i -> i.snap(empty));
                empty.setOnHoverExitAction(i -> i.exit(empty));
                empty.setOnMouseClickedAction(i -> i.addNewObject(empty));
                ui.add(empty);
            }
        }

        GameObject button = new GameObject("Images/button.png", 150, 50);
        button.setInteractable(true);
        button.setPosition(250, 200);
        button.setZindex(2);

        button.setOnHoverEnterAction(i -> i.focus(button));
        button.setOnHoverExitAction(i -> i.unfocus(button));
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
