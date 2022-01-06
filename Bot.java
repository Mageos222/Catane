import java.util.ArrayList;
import java.util.Random;

import GameEngine.GameObject;
import GameEngine.Vector2;

public class Bot extends Player implements Runnable {

    private Controller controller;

    private Map m;
    private boolean init = true;

    private Random rnd = new Random();

    private static final Ressource roadCost = new Ressource(0, 1, 0, 0, 1);
    private static final Ressource villageCost = new Ressource(1, 1, 1, 0, 1);
    private static final Ressource townCost = new Ressource(2, 0, 0, 3, 0);

    private ArrayList<Vector2> path;
    private int maxPath = 4;

    public Bot(int number) {
        super(number);
        this.path = new ArrayList<>();
    }

    public void setController(Controller controller) { this.controller = controller; }

    @Override
    public void run() {
        controller.setBotPlaying(true);
        System.out.println(getName() + " : My turn");
        if(init) playInit();
        else play();
    }

    private void play() {

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        Colony[][] map = m.getMap();
        Ressource cost = new Ressource(5, 5, 5, 5, 5);
        Boolean haveAction = true;

        if(controller.getRobber()) moveRobber();

        Classification target = null;

        do {
            target = findTarget(cost);
            if(target == null) maxPath++;
        }
        while(target == null && maxPath < 20);

        if(target == null)  System.out.println(getName() + " : I don't know what to do...");
        else {
            boolean canUpgradeVillage = true;

            while(haveAction) {
                if(possesse(townCost) && canUpgradeVillage) canUpgradeVillage = upgradeVillage(cost); 
                else if(possesse(villageCost) && m.canBuildVillage(number, target.x, target.y) && map[target.y][target.x].getVillage()==-1) 
                    buildVillage(target);
                else if(possesse(roadCost) && path.size() >= 2) makePath(path);
                else haveAction = false;

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        path.clear();
        controller.setBotPlaying(false);
        System.out.println(getName() + " : End of my turn");
        controller.botNextTurn();
    }

    private void playInit() {
        Colony[][] map = m.getMap();

        Ressource cost = new Ressource(2, 3, 2, 1, 3);
        Classification[] tab = makePointList(cost);
        for(Classification col : tab) {
            //System.out.println(getName() + " : Searching");
            controller.botSnap(map[col.getY()][col.getX()].getObject());
            if(controller.build(map[col.getY()][col.getX()].getObject(), col.getX(), col.getY(), 0, 0, true, true)) {
                System.out.println(getName() + " : Building village at position ("+col.getX()+";"+col.getY()+"), score : " + col.getValue());

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                while(!buildRoad(col))
                    System.out.println(getName() + " : Searching road");

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
        
        controller.setBotPlaying(false);
        System.out.println(getName() + " : End of my turn");
        controller.botNextTurn();
    }

    private boolean calculatePath(Classification target) {
        Colony[][] map = m.getMap();
        System.out.println(getName() + " : Calculating path...");
        Vector2 starting = getClosest(new Vector2(target.getX(), target.getY()), 1);
        path = AStar(new Vector2(target.getX(), target.getY()), starting);

        boolean remove = false;
        for(int i = 1; i < path.size(); i++) {
            if(remove) {
                path.remove(i);
                continue;
            }

            Vector2 start = path.get(i);
            Vector2 next = path.get(i-1);

            if(start.getY() != next.getY() && map[start.getY()][start.getX()].getConnSup() == number ||
                start.getX() > next.getX() && map[start.getY()][start.getX()].getConnL() == number || 
                start.getX() < next.getX() && map[start.getY()][start.getX()].getConnR() == number) 
                remove = true;
        }

        if(path.size() > maxPath) {
            path.clear();
            return false;
        }

        System.out.println("Path : ");
        //for(Vector2 v : path) System.out.println(v.toString());

        return true;
    }

    private Classification findTarget(Ressource cost) {
        Colony[][] map = m.getMap();

        Classification[] tab = makePointList(cost);

        for(Classification col : tab) {
            if(m.canBuildFirstVillage(number, col.getX(), col.getY()) && map[col.getY()][col.getX()].getVillage() == -1 && (!path.isEmpty() || calculatePath(col))) {
                System.out.println(getName() + " : Targeted point : (" + col.getX() + ";" + col.getY()+")");
                return col;
            }
        }

        return null;
    }

    private void buildVillage(Classification col) {
        Colony[][] map = m.getMap();

        controller.botSnap(map[col.getY()][col.getX()].getObject());
        controller.build(map[col.getY()][col.getX()].getObject(), col.getX(), col.getY(), 0, 0, true, true);

        System.out.println(getName() + " : Building village at position ("+col.getX()+";"+col.getY()+"), score : " + col.getValue());
    }

    private boolean upgradeVillage(Ressource cost) {
        Colony[][] map = m.getMap();
        Classification[] list = makePointList(cost, true);
        if(list.length > 0) {
            Classification town = list[0];
            controller.botSnap(map[town.y][town.x].getObject());
            controller.build(map[town.y][town.x].getObject(), town.x, town.y, 0, 0, false, true);
            System.out.println(getName() + " : Vilage (" + town.x + ";" + town.y + ") upgraded");
        }
        return list.length > 1;
    }

    private boolean buildRoad(Classification col) {
        Colony[][] map = m.getMap();
        switch(rnd.nextInt(3)) {
            case 0: if(map[col.getY()][col.getX()].getRoadR() != null && map[col.getY()][col.getX()].getConnR() == -1) {
                controller.botSnap(map[col.getY()][col.getX()].getRoadR());
                controller.build(map[col.getY()][col.getX()].getRoadR(), col.getX(), col.getY(), col.getX()+1, col.getY(), false, false);    
                return true;               
            }
            break;
            case 1 : if(map[col.getY()][col.getX()].getRoadL() != null && map[col.getY()][col.getX()].getConnL() == -1) {
                controller.botSnap(map[col.getY()][col.getX()].getRoadL());
                controller.build(map[col.getY()][col.getX()].getRoadL(), col.getX(), col.getY(), col.getX()-1, col.getY(), false, false);
                return true;
            } 
            break;
            default: if(map[col.getY()][col.getX()].getRoadSup() != null && map[col.getY()][col.getX()].getConnSup() == -1) {
                controller.botSnap(map[col.getY()][col.getX()].getRoadSup());
                Vector2 pos = m.getAdjacent(col.getX(), col.getY())[2];
                controller.build(map[col.getY()][col.getX()].getRoadSup(), col.getX(), col.getY(), pos.getX(), pos.getY(), false, false);
                return true;
            }
            break;
        }

        return false;
    }

    private Classification[] makePointList(Ressource cost) { return makePointList(cost, false); }

    private Classification[] makePointList(Ressource cost, boolean posseded) {
        Colony[][] map = m.getMap();
        ArrayList<Classification> point = new ArrayList<>();
        
        Ressource ressource = new Ressource();

        for(Colony colony : colonies) 
            for(Tiles tile : colony.getTiles())
                ressource.add(tile.getType(), 1);

        cost.remove(ressource);

        //System.out.println(getName() + " : Ressources value : " + cost.toString());
        
        for(int y = 0; y < map.length; y++) 
            for(int x = 0; x < map[y].length; x++) 
                if(!posseded && map[y][x].getVillage() == -1 || posseded && map[y][x].getVillage() == number && !map[y][x].isTown()) 
                    point.add(new Classification(x, y, map[y][x].getTiles(), cost));

        return sort(point);
    }

    private Classification[] sort(ArrayList<Classification> point) {
        int counter = 0;
        Classification[] res = new Classification[point.size()];

        for(int i = 20; i >= 0; i--) 
            for(Classification c : point) 
                if(c.getValue() == i) {
                    res[counter] = c;
                    counter++;
                }

        return res;
    }

    private Vector2 getClosest(Vector2 pos, int size) {
        Vector2 res = new Vector2(0, 0);
        double dst = Double.MAX_VALUE;

        Colony[][] map = m.getMap();

        for(int y = 0; y < map.length; y++) {
            for(int x = 0; x < map[y].length; x++) {
                if(map[y][x].haveConn(number)) {
                    int posX =x+Math.min(y, m.getSize()-y)-(pos.getX()+Math.min(pos.getY(), m.getSize()-pos.getY()));
                    int posY = y-pos.getY();

                    double newDst = Math.sqrt(Math.pow(posX, 2)+Math.pow(posY, 2));

                    if(res == null || newDst < dst) {
                        res = new Vector2(x, y);
                        dst = newDst;
                    }
                }
            }
        }

        return res;
    }

    private void makePath(ArrayList<Vector2> list) {  
        if(list.size() < 2) return;
        Colony[][] map = m.getMap();

        Vector2 start = list.get(list.size()-1);
        Vector2 target = list.get(list.size()-2);

        GameObject obj;
        if(start.getY() != target.getY()) 
            obj = map[start.getY()][start.getX()].getRoadSup();
        else if(start.getX() > target.getX()) 
            obj = map[start.getY()][start.getX()].getRoadL();
        else 
            obj = map[start.getY()][start.getX()].getRoadR();
        
        if(m.canBuildRoad(number, start.getY(), start.getX(), target.getY(), target.getX())) {
            System.out.println(getName() + " : Building road from " + start.toString() + " to " + target.toString());
            controller.botSnap(obj);
            controller.build(obj, start.getX(), start.getY(), target.getX(), target.getY(), false, false);
        }

        list.remove(list.size()-1);
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
                System.out.println(getName() + " : Building road from " + startingPoint.toString() + " to " + target.toString());
                controller.botSnap(col.getRoadSup());
                controller.build(col.getRoadSup(), startingPoint.getX(), startingPoint.getY(), 
                                    sup.getX(), sup.getY(), false, false);
                return true;
            }
        }
        if(Math.abs(horizontal) >= Math.abs(vertical) || h) {
            System.out.println(getName() + " : Building road from " + startingPoint.toString() + " to " + target.toString());

            GameObject road = horizontal>0?col.getRoadR():col.getRoadL();

            int add = horizontal>0?1:-1;
            if((horizontal>0?col.getConnR():col.getConnL()) != -1) return false;
            if(m.canBuildRoad(number, startingPoint.getY(), startingPoint.getX(), startingPoint.getY(), startingPoint.getX()+add)) {
                controller.botSnap(road);
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
        controller.botMoveRobber(pos.getX(), pos.getY());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        int[] players = controller.putRobber(m.getAdjaToTile(x, y), sup.object.getType());

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        int choice = rnd.nextInt(players.length);
        controller.botSelectPlayer(players[choice], controller.getTemp().get(choice));

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        controller.confirmPlayer(players[choice]);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private ArrayList<Vector2> AStar(Vector2 target, Vector2 startingPoint) {
        Colony[][] map = m.getMap();

        ArrayList<Node> open = new ArrayList<>();
        ArrayList<Node> close = new ArrayList<>();

        open.add(new Node(0, startingPoint.dst(target), null, startingPoint));

        int i = 0;
        while(i < 1000) {
            if(open.isEmpty()) return new ArrayList<>();
            Node current = open.get(0);
            for(Node node : open) 
                if(node.fCost < current.fCost) current = node;
            open.remove(current);
            close.add(current);

            if(map[current.position.getY()][current.position.getX()].getVillage() != number && 
                    map[current.position.getY()][current.position.getX()].getVillage() >= 0) {
                i++;
                continue;
            }

            if(current.position.equals(target)) {
                ArrayList<Vector2> res = new ArrayList<>();
                while(true) {
                    res.add(current.position);
                    if(current.parent == null) return res;
                    current = current.parent;
                }
            }

            Vector2[] adja = m.getAdjacent(current.position.getX(), current.position.getY());

            double[] stDst = new double[3];
            double[] tDst = new double[3];

            for(int a = 0; a < 3; a++) {
                if(adja[a] == null) continue;
                stDst[a] = Math.sqrt(Math.pow(adja[a].getX()+Math.min(adja[a].getY(), m.getSize()-adja[a].getY())-
                            (double)(startingPoint.getX()+Math.min(startingPoint.getY(), m.getSize()-startingPoint.getY())), 2)
                            + Math.pow(adja[a].getY(), startingPoint.getY()));
                tDst[a] = Math.sqrt(Math.pow(adja[a].getX()+Math.min(adja[a].getY(), m.getSize()-adja[a].getY())-
                            (double)(target.getX()+Math.min(target.getY(), m.getSize()-target.getY())),2)
                            + Math.pow(adja[a].getY(), target.getY()));
            }

            if(adja[0] != null && ((map[adja[0].getY()][adja[0].getX()].getConnL() == -1 || 
                        map[adja[0].getY()][adja[0].getX()].getConnL() == number) && !exist(close, adja[0]))) {
                Node exist = getNode(open, adja[0]);
                if(exist != null) {
                    if(stDst[0] + tDst[0] < exist.fCost)
                        exist.fCost = stDst[0] + tDst[0];
                }
                else open.add(new Node(stDst[0], tDst[0], current, adja[0]));
            }
            if(adja[1] != null && ((map[adja[1].getY()][adja[1].getX()].getConnR() == -1 || 
                        map[adja[1].getY()][adja[1].getX()].getConnR() == number) && !exist(close, adja[1]))) {
                Node exist = getNode(open, adja[1]);
                if(exist != null) {
                    if(stDst[1] + tDst[1] < exist.fCost)
                        exist.fCost = stDst[1] + tDst[1];
                }
                else open.add(new Node(stDst[1], tDst[1], current, adja[1]));
            }
            if(adja[2] != null && ((map[adja[2].getY()][adja[2].getX()].getConnSup() == -1 || 
                        map[adja[2].getY()][adja[2].getX()].getConnSup() == number) && !exist(close, adja[2]))) {
                Node exist = getNode(open, adja[2]);
                if(exist != null) {
                    if(stDst[2] + tDst[2] < exist.fCost)
                        exist.fCost = stDst[2] + tDst[2];
                }
                else open.add(new Node(stDst[2], tDst[2], current, adja[2]));
            }

            /*for(int i = 0; i < 3; i++)
                if(adja[i] != null && (map[adja[i].getY()][adja[i].getX()].getVillage() == -1 || 
                    map[adja[i].getY()][adja[i].getX()].getVillage() == number) && !exist(close, adja[i])) {
                    open.add(new Node(adja[i].dst(startingPoint), adja[i].dst(target), current, adja[i]));
                }*/

            i++;
        }

        System.out.println("X_X");
        return new ArrayList<>();
    }

    private boolean exist(ArrayList<Node> nodes, Vector2 v) {
        return getNode(nodes, v) != null;
    }

    public Node getNode(ArrayList<Node> nodes, Vector2 v) {
        for(Node node : nodes)
            if(node.position.equals(v))
                return node;

        return null;
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

            if(value < 0) value = 0;
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

