import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Bot extends Player {

    Controller controller;

    public Bot(String name) {
        super(name);
    }

    public void setController(Controller controller) { this.controller = controller; }

    public void play(Colony[][] map, boolean init) {
        System.out.println("My turn");
        if(init) playInit(map);
    }

    private void playInit(Colony[][] map) {
        int n = map.length/2;
        ArrayList<Classification> point = new ArrayList<>();

        Ressource ressource = new Ressource();
        Ressource res = new Ressource(1, 2, 1, 1, 2);
        res.remove(ressource);

        for(Colony colony : colonies) 
            for(Tiles tile : colony.getTiles())
                ressource.add(tile.getType(), 2);

        for(int y = 0; y < map.length; y++) 
            for(int x = 0; x < map[y].length; x++) 
                if(map[y][x].getVillage() == -1)
                    point.add(new Classification(x, y, map[y][x].getTiles(), res));

        controller.setAddObject(true);

        Classification[] tab = sort(point);
        for(Classification col : tab) {
            controller.snap(map[col.getY()][col.getX()].getObject());
            if(controller.build(map[col.getY()][col.getX()].getObject(), col.getX(), col.getY(), 0, 0, true, false)) {
                System.out.println("Building village at position ("+col.getX()+";"+col.getY()+")");

                if(map[col.getY()][col.getX()].getConnL() == -1) {
                    controller.build(map[col.getY()][col.getX()].getObject(), col.getX(), col.getY(), col.getX()-1, col.getY(), false, false);
                    System.out.println("Building a road");
                }
                else if(map[col.getY()][col.getX()].getConnR() == -1) {
                    controller.build(map[col.getY()][col.getX()].getObject(), col.getX(), col.getY(), col.getX()+1, col.getY(), false, false);
                    System.out.println("Building a road");
                }
                break;
            }
            controller.unsnap(map[col.getY()][col.getX()].getObject());
        }
        
        controller.nextTurn();
    }

    private Classification[] sort(ArrayList<Classification> point) {
        int counter = 0;
        Classification[] res = new Classification[point.size()];

        for(int i = 6; i >= 0; i--) 
            for(Classification c : point) 
                if(c.getValue() == i) {
                    res[counter] = c;
                    counter++;
                }

        return res;
    }
    
    @Override
    public boolean isBot() { return true; }

    class Classification {
        int x; 
        int y;
        int value;

        public Classification(int x, int y, Tiles[] tiles, Ressource res) {
            this.x = x; 
            this.y = y;
            this.value = 0;

            for(Tiles tile : tiles) 
                value += res.getRessource(tile.getType());
        }

        public int getValue() { return this.value; }
        public int getX() { return this.x; }
        public int getY() { return this.y; }
    }
}   

