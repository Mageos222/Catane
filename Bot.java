import java.util.ArrayList;
import java.util.Random;

import GameEngine.Vector2;

public class Bot extends Player implements Runnable {

    private Controller controller;

    private Map m;
    private boolean init = true;

    private Random rnd = new Random();

    private static final Ressource roadCost = new Ressource(0, 1, 0, 0, 1);
    private static final Ressource villageCost = new Ressource(1, 1, 1, 0, 1);
    private static final Ressource townCost = new Ressource(2, 0, 0, 3, 0);

    public Bot(int number) {
        super(number);
    }

    public void setController(Controller controller) { this.controller = controller; }

    @Override
    public void run() {
        System.out.println("Bot "+ number + " : My turn");
        if(init) playInit();
        else play();
    }

    private void play() {
        Colony[][] map = m.getMap();

        Ressource cost = new Ressource(3, 3, 3, 3, 3);
        Classification[] tab = makePointList(cost);

        for(Classification col : tab) {
            if(m.canBuildFirstVillage(number, col.getX(), col.getY()) && map[col.getY()][col.getX()].getVillage() == -1) {

                if(possesse(villageCost) && m.canBuildVillage(number, col.getX(), col.getY())) {
                    controller.build(map[col.getY()][col.getX()].getObject(), col.getX(), col.getY(), 0, 0, true, false);
                    System.out.println("Bot "+ number + " : Building village at position ("+col.getX()+";"+col.getY()+"), score : " + col.getValue());
                    break;
                }
                else if(possesse(roadCost)) {
                    System.out.println("Bot "+ number + " : I can build a road");
                    makePath(new Vector2(col.getX(), col.getY()));
                    break;
                }
            }
        }

        controller.nextTurn();
        System.out.println("Bot "+ number + " : End of my turn");
    }

    private void playInit() {
        Colony[][] map = m.getMap();

        controller.setAddObject(true);

        Ressource cost = new Ressource(2, 3, 2, 1, 3);
        Classification[] tab = makePointList(cost);
        for(Classification col : tab) {
            System.out.println("Bot "+ number + " : Searching");
            controller.snap(map[col.getY()][col.getX()].getObject());
            if(controller.build(map[col.getY()][col.getX()].getObject(), col.getX(), col.getY(), 0, 0, true, false)) {
                System.out.println("Bot "+ number + " : Building village at position ("+col.getX()+";"+col.getY()+"), score : " + col.getValue());

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                controller.setAddObject(true);
                while(!buildRoad(col))
                    System.out.println("Bot "+ number + " : Searching road");

                break;
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            controller.unsnap(map[col.getY()][col.getX()].getObject());
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        controller.nextTurn();
        System.out.println("Bot "+ number + " : End of my turn");
    }

    private boolean buildRoad(Classification col) {
        Colony[][] map = m.getMap();
        switch(rnd.nextInt(2)) {
            case 0: if(map[col.getY()][col.getX()].getRoadR() != null && map[col.getY()][col.getX()].getConnR() == -1) {
                controller.snap(map[col.getY()][col.getX()].getRoadR());
                controller.build(map[col.getY()][col.getX()].getRoadR(), col.getX(), col.getY(), col.getX()+1, col.getY(), false, false);    
                return true;               
            }
            break;
            case 1 : if(map[col.getY()][col.getX()].getRoadL() != null && map[col.getY()][col.getX()].getConnL() == -1) {
                controller.snap(map[col.getY()][col.getX()].getRoadL());
                controller.build(map[col.getY()][col.getX()].getRoadL(), col.getX(), col.getY(), col.getX()-1, col.getY(), false, false);
                return true;
            } 
            break;
            default: if(map[col.getY()][col.getX()].getRoadSup() != null && map[col.getY()][col.getX()].getConnSup() == -1) {
                controller.snap(map[col.getY()][col.getX()].getRoadSup());
                Vector2 pos = m.getAdjacent(col.getX(), col.getY())[2];
                controller.build(map[col.getY()][col.getX()].getRoadSup(), col.getX(), col.getY(), pos.getX(), pos.getY(), false, false);
                return true;
            }
            break;
        }

        return false;
    }

    private Classification[] makePointList(Ressource cost) {
        Colony[][] map = m.getMap();
        ArrayList<Classification> point = new ArrayList<>();
        
        Ressource ressource = new Ressource();

        for(Colony colony : colonies) 
            for(Tiles tile : colony.getTiles())
                ressource.add(tile.getType(), 1);

        cost.remove(ressource);

        System.out.println("Bot "+ number + " : Ressources value : " + cost.toString());
        
        for(int y = 0; y < map.length; y++) 
            for(int x = 0; x < map[y].length; x++) 
                if(map[y][x].getVillage() == -1) 
                    point.add(new Classification(x, y, map[y][x].getTiles(), cost));

        return sort(point);
    }

    private Classification[] sort(ArrayList<Classification> point) {
        int counter = 0;
        Classification[] res = new Classification[point.size()];

        for(int i = 11; i >= 0; i--) 
            for(Classification c : point) 
                if(c.getValue() == i) {
                    res[counter] = c;
                    counter++;
                }

        return res;
    }

    private Colony getClosest(Vector2 pos) {
        Colony res = null;
        double dst = Double.MAX_VALUE;

        for(Colony col : colonies) {
            if(res == null) res = col;

            int posX = col.getObject().transform().getCenterPosition().getX()-pos.getX();
            int posY = col.getObject().transform().getCenterPosition().getY()-pos.getY();
            
            double newDst = Math.sqrt(Math.pow(posX, 2)+Math.pow(posY, 2));

            if(newDst < dst) {
                res = col;
                dst = newDst;
            }
        }

        return res;
    }

    private void makePath(Vector2 col) {
        Colony[][] map = m.getMap();

        int tileSize = 520/m.getSize();
        int xOffset = (int)(0.5f*tileSize);
        int yOffset = (int)(0.74f*tileSize);
        int yShift = (int)(0.1f*tileSize);
        
        int i = (col.getY() < m.getSize())?col.getY():2*m.getSize()-col.getY()-1;
        Vector2 pos = new Vector2((col.getX()-m.getSize()-i)*xOffset, (int)((col.getY()-m.getSize()+0.5f)*yOffset+yShift));
        Colony startingPoint = getClosest(pos);

        int horizontal = startingPoint.getObject().transform().getPosition().getX()-pos.getX();
        int vertical = startingPoint.getObject().transform().getPosition().getY()-pos.getY();

        if(Math.abs(horizontal) < Math.abs(vertical)) {
            System.out.print("Bot "+ number + " : Searching horizontal from " + 
                startingPoint.getObject().transform().getPosition().toString() + " to " + pos.toString());
            if(horizontal < 0 && map[col.getY()][col.getX()].getRoadR() != null) {
                System.out.println(" right");
                if(map[col.getY()][col.getX()].getConnR() == number) makePath(Vector2.add(col, new Vector2(1,0)));
                else if(m.canBuildRoad(number, col.getY(), col.getY(), col.getY(), col.getX()+1)) {
                    controller.setAddObject(true);
                    controller.snap(map[col.getY()][col.getX()].getRoadR());
                    controller.build(map[col.getY()][col.getX()].getRoadR(), col.getY(), col.getY(), col.getX()+1, col.getY(), false, false);
                }
            }
            else if(horizontal >= 0 && map[col.getY()][col.getX()].getRoadL() != null) {
                System.out.println(" left");
                if(map[col.getY()][col.getX()].getConnL() == number) makePath(Vector2.add(col, new Vector2(-1,0)));
                else if(m.canBuildRoad(number, col.getY(), col.getY(), col.getY(), col.getX()+1)) {
                    controller.setAddObject(true);
                    controller.snap(map[col.getY()][col.getX()].getRoadL());
                    controller.build(map[col.getY()][col.getX()].getRoadL(), col.getX(), col.getY(), col.getX()-1, col.getY(), false, false);
                }
            }
        }
        else {
            System.out.println("Bot "+ number + " : Searching vertical");
        }
    }
    
    @Override
    public boolean isBot() { return true; }

    public void stopInit() { this.init = false; }
    public void setMap(Map map) { this.m = map; }

    class Classification {
        int x; 
        int y;
        int value;

        public Classification(int x, int y, Tiles[] tiles, Ressource res) {
            this.x = x; 
            this.y = y;
            this.value = 0;

            Ressource val = new Ressource(res);

            for(Tiles tile : tiles) {
                value += val.getRessource(tile.getType());
                val.remove(new Ressource(tile.getType(), 1));
            }
        }

        public int getValue() { return this.value; }
        public int getX() { return this.x; }
        public int getY() { return this.y; }
    }
}   

