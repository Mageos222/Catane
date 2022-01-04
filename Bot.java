import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import GameEngine.GameObject;
import GameEngine.Vector2;

public class Bot extends Player implements Runnable {

    private Controller controller;

    private Map m;
    private boolean init = true;

    private Random rnd = new Random();

    private ArrayList<Vector2> classi;

    private static final Ressource roadCost = new Ressource(0, 1, 0, 0, 1);
    private static final Ressource villageCost = new Ressource(1, 1, 1, 0, 1);
    private static final Ressource townCost = new Ressource(2, 0, 0, 3, 0);

    private ArrayList<Vector2> path;

    public Bot(int number) {
        super(number);
        this.classi = new ArrayList<>();
        this.path = new ArrayList<>();
    }

    public void setController(Controller controller) { this.controller = controller; }

    @Override
    public void run() {
        controller.setBotPlaying(true);
        System.out.println("Bot "+ number + " : My turn");
        if(init) playInit();
        else play();
        controller.setBotPlaying(false);
    }

    private void play() {
        Colony[][] map = m.getMap();
        Boolean haveAction = false;

        Ressource cost = new Ressource(3, 3, 3, 3, 3);
        Classification[] tab = makePointList(cost);

        if(controller.getRobber()) moveRobber();

        for(Classification col : tab) {
            if(m.canBuildFirstVillage(number, col.getX(), col.getY()) && map[col.getY()][col.getX()].getVillage() == -1) {

                if(possesse(villageCost) && m.canBuildVillage(number, col.getX(), col.getY())) {
                    controller.snap(map[col.getY()][col.getX()].getObject());
                    if(controller.build(map[col.getY()][col.getX()].getObject(), col.getX(), col.getY(), 0, 0, true, true)) {
                        classi.add(new Vector2(col.getX(), col.getY()));
                        System.out.println("Bot "+ number + " : Building village at position ("+col.getX()+";"+col.getY()+"), score : " + col.getValue());
                        haveAction = true;
                        break;
                    }
                }
                if(!m.canBuildVillage(number, col.getX(), col.getY()) && possesse(roadCost)) {
                    System.out.println("Bot "+ number + " : I can build a road");                   
                    if(path.isEmpty()) {
                        Vector2[] starting = getClosest(new Vector2(col.getX(), col.getY()), 1);
                        if(starting[0] != null) {
                            path = AStar(new Vector2(col.getX(), col.getY()), starting[0]);
                            System.out.println("Path : ");
                            for(Vector2 v : path) System.out.println(v.toString());
                        }
                    }
                    if(path.size() >= 2) {
                        test(path);
                        haveAction = true;
                    }

                    /*for(int i = 0; i < 25; i++) {
                        if(starting[i] == null || makePath(starting[i], new Vector2(col.getX(), col.getY()))) {
                            if(starting[i] != null) haveAction = true;
                            break;
                        }
                    }
                    break;*/
                }
                break;
            }
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        if(haveAction) play();
        else {
            path.clear();
            controller.nextTurn();
            System.out.println("Bot "+ number + " : End of my turn");
        }
    }

    private void playInit() {
        Colony[][] map = m.getMap();

        controller.setAddObject(true);

        Ressource cost = new Ressource(2, 3, 2, 1, 3);
        Classification[] tab = makePointList(cost);
        for(Classification col : tab) {
            System.out.println("Bot "+ number + " : Searching");
            controller.snap(map[col.getY()][col.getX()].getObject());
            if(controller.build(map[col.getY()][col.getX()].getObject(), col.getX(), col.getY(), 0, 0, true, true)) {
                classi.add(new Vector2(col.getX(), col.getY()));
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
        switch(rnd.nextInt(3)) {
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

    private Vector2[] getClosest(Vector2 pos, int size) {
        Vector2[] res = new Vector2[size];
        double[] dst = new double[size];

        Colony[][] map = m.getMap();

        /*for(Vector2 col : classi) {
            if(res == null) res = col;

            int posX = (col.getX()+Math.min(col.getY(), m.getSize()-col.getY()))-(pos.getX()+Math.min(pos.getY(), m.getSize()-pos.getY()));
            int posY = col.getY()-pos.getY();
            
            double newDst = Math.sqrt(Math.pow(posX, 2)+Math.pow(posY, 2));

            if(newDst < dst) {
                res = col;
                dst = newDst;
            }
        }*/

        for(int y = 0; y < map.length; y++) {
            for(int x = 0; x < map[y].length; x++) {
                if(map[y][x].haveConn(number)) {
                    int posX =x+Math.min(y, m.getSize()-y)-(pos.getX()+Math.min(pos.getY(), m.getSize()-pos.getY()));
                    int posY = y-pos.getY();
                    
                    double newDst = Math.sqrt(Math.pow(posX, 2)+Math.pow(posY, 2));

                    for(int i = 0; i < size; i++) {
                        if(res[i] == null || newDst < dst[i]){
                            for(int j = size-1; j > i; j--)
                                res[j] = res[j-1];
                            res[i] = new Vector2(x, y);
                            dst[i] = newDst;
                            
                            break;                      
                        }
                    }
                }
            }
        }

        return res;
    }

    private void test(ArrayList<Vector2> list) {  
        if(list.size() < 2) return;
        Colony[][] map = m.getMap();

        Vector2 start = list.get(list.size()-1);
        Vector2 target = list.get(list.size()-2);
        Colony col = map[start.getY()][start.getX()];
        if(m.canBuildRoad(number, start.getY(), start.getX(), target.getY(), target.getX())) {
            System.out.println("Building from " + start.toString() + " to " + target.toString());
            controller.setAddObject(true);
            controller.snap(col.getRoadSup());
            controller.build(col.getRoadSup(), start.getX(), start.getY(), target.getX(), target.getY(), false, false);

            list.remove(list.size()-1);
        }
    }

    private boolean makePath(Vector2 startingPoint, Vector2 target) {
        Colony[][] map = m.getMap();
        boolean h = false;

        int horizontal = target.getX()-startingPoint.getX();
        int vertical = target.getY()-startingPoint.getY();

        Colony col = map[startingPoint.getY()][startingPoint.getX()];

        if(Math.abs(horizontal) < Math.abs(vertical)) {
            int add = vertical>0?1:-1;
            
            Vector2 sup = m.getAdjacent(startingPoint.getX(), startingPoint.getY())[2];

            if(sup == null || sup.getY() > startingPoint.getY() && vertical < 0 || sup.getY() < startingPoint.getY() && 
                    vertical > 0 || col.getConnSup() != -1) h = true;
            else if(m.canBuildRoad(number, startingPoint.getY(), startingPoint.getX(), sup.getY(), sup.getX())) {
                System.out.println("Building from " + startingPoint.toString() + " to " + target.toString() + " vertical");
                controller.setAddObject(true);
                controller.snap(col.getRoadSup());
                controller.build(col.getRoadSup(), startingPoint.getX(), startingPoint.getY(), 
                                    sup.getX(), sup.getY(), false, false);
                return true;
            }
        }
        if(Math.abs(horizontal) >= Math.abs(vertical) || h) {
            System.out.println("Building from " + startingPoint.toString() + " to " + target.toString() + (horizontal>0?" Right":" Left"));

            GameObject road = horizontal>0?col.getRoadR():col.getRoadL();

            int add = horizontal>0?1:-1;
            if((horizontal>0?col.getConnR():col.getConnL()) != -1) return false;
            if(m.canBuildRoad(number, startingPoint.getY(), startingPoint.getX(), startingPoint.getY(), startingPoint.getX()+add)) {
                controller.setAddObject(true);
                controller.snap(road);
                controller.build(road, startingPoint.getX(), startingPoint.getY(), startingPoint.getX()+add, startingPoint.getY(), false, false);
                return true;
            }
        }

        return false;
    }

    private void moveRobber() {
        ArrayList<Compare<Tiles, Integer>> list = new ArrayList<>();
        Colony[][] map = m.getMap();
        for(Colony[] colonies : map) {
            for(Colony colony : colonies) {
                if(colony.getVillage() != -1 && colony.getVillage() != number) {
                    for(Tiles tile : colony.getTiles()) {
                        boolean find = false;
                        for(Compare<Tiles, Integer> comp : list) {
                            if(comp.object == tile) {
                                comp.value += 1;
                                find = true;
                                break;
                            }
                        }
                        if(!find) list.add(new Compare<>(tile, 1));
                    }
                }
            }
        }

        Compare<Tiles, Integer> sup = list.get(0);
        for(Compare<Tiles, Integer> comp : list) 
            if(comp.value > sup.value) 
                sup = comp;

        int tileSize = 520/m.getSize();
        int xOffset = (int)(0.5f*tileSize);
        int yOffset = (int)(0.74f*tileSize);

        int x = sup.object.getPosition().getX();
        int y = sup.object.getPosition().getY();
        
        Vector2 pos = Vector2.multiply(new Vector2(2*(x+Math.max(0, y-m.getSize()+1))-(m.getSize()-1+y), m.getSize()-y-1), new Vector2(xOffset, yOffset));
        controller.moveRobber(pos.getX(), pos.getY());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        int[] players = controller.putRobber(m.getAdjaToTile(x, y));
        int choice = rnd.nextInt(players.length);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        controller.selectPlayer(players[choice], controller.getTemp().get(choice));

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        controller.confirmPlayer(players[choice]);
    }

    private ArrayList<Vector2> AStar(Vector2 target, Vector2 startingPoint) {
        Colony[][] map = m.getMap();

        ArrayList<Node> open = new ArrayList<>();
        ArrayList<Node> close = new ArrayList<>();

        open.add(new Node(0, startingPoint.dst(target), null, startingPoint));

        while(true) {
            if(open.isEmpty()) return new ArrayList<>();
            Node current = open.get(0);
            for(Node node : open) 
                if(node.fCost < current.fCost) current = node;

            open.remove(current);
            close.add(current);

            if(current.position.equals(target)) {
                ArrayList<Vector2> res = new ArrayList<>();
                while(true) {
                    res.add(current.position);
                    if(current.parent == null) return res;
                    current = current.parent;
                }
            }

            Vector2[] adja = m.getAdjacent(current.position.getX(), current.position.getY());
            if(adja[0] != null && map[adja[0].getY()][adja[0].getX()].getConnL() == -1 || 
                map[adja[0].getY()][adja[0].getX()].getConnL() == number && !exist(close, adja[0])) 
                    open.add(new Node(adja[0].dst(startingPoint), adja[0].dst(target), current, adja[0]));
            if(adja[1] != null && map[adja[1].getY()][adja[1].getX()].getConnL() == -1 || 
                map[adja[1].getY()][adja[1].getX()].getConnL() == number && !exist(close, adja[1])) 
                    open.add(new Node(adja[1].dst(startingPoint), adja[1].dst(target), current, adja[1]));
            if(adja[2] != null && map[adja[2].getY()][adja[2].getX()].getConnL() == -1 || 
                map[adja[2].getY()][adja[2].getX()].getConnL() == number && !exist(close, adja[2])) 
                    open.add(new Node(adja[2].dst(startingPoint), adja[2].dst(target), current, adja[2]));


            /*for(int i = 0; i < 3; i++)
                if(adja[i] != null && (map[adja[i].getY()][adja[i].getX()].getVillage() == -1 || 
                    map[adja[i].getY()][adja[i].getX()].getVillage() == number) && !exist(close, adja[i])) {
                    open.add(new Node(adja[i].dst(startingPoint), adja[i].dst(target), current, adja[i]));
                }*/
        }
    }

    private boolean exist(ArrayList<Node> nodes, Vector2 v) {
        for(Node node : nodes)
            if(node.position.equals(v))
                return true;

        return false;
    }
    
    @Override
    public boolean isBot() { return true; }

    public void stopInit() { this.init = false; }
    public void setMap(Map map) { this.m = map; }

    private class Classification {
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

    private class Compare<O, V extends Number> {
        private O object;
        private V value;

        public Compare(O obj, V val) {
            this.object = obj;
            this.value = val;
        }
    }

    private class Node {
        private Node parent;
        private double gCost;
        private double hCost;
        private double fCost;

        private Vector2 position;

        public Node(double g, double h, Node parent, Vector2 position) {
            this.gCost = g;
            this.hCost = h;
            this.fCost = gCost + hCost;

            this.parent = parent;
            this.position = position;
        }
    }
}   

