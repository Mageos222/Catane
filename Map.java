import GameEngine.Vector2;

import java.util.ArrayList;
import java.util.Random;

public class Map {
    
    private int size;

    private Colony[][] map;

    public Map(int size, Canvas canvas) {
        this.size = size;

        int tileSize = 520/size;
        int xOffset = (int)(0.5f*tileSize);
        int yOffset = (int)(0.74f*tileSize);

        int yShift = (int)(0.1f*tileSize);

        // Map Init
        map = new Colony[size*2][];
        for(int y = 0; y < size * 2; y++) {
            int i = (y < size)?y:2*size-y-1;
            map[y] = new Colony[2*(size+i)+1];
            for(int x = 0; x < 2*(size+i)+1; x++) {
                map[y][x] = new Colony(canvas.addEmptyVillage((x-size-i)*xOffset, (int)((y-size+0.5f)*yOffset+yShift), x, y, size));

                yShift = -yShift;

                /*int nextX = x+(x%2==1?-1:y==size||y==size-1?0:1);
                int nextY = y+(x%2==0&&y>=size||x%2==1&&y<size&&y>0||y>=map.length-1?-1:1);

                if(x != 2*(size+i)) canvas.addEmptyRoad((x-size-i)*xOffset+xOffset/2, (int)((y-size+0.5f)*yOffset), x, y, x+1, y, roadType, size);
                if(roadType == 1 && y != 2*size-1) canvas.addEmptyRoad((x-size-i)*xOffset, (int)((y-size+0.5f)*yOffset)+yOffset/2, x, y, nextX, nextY, 2, size);*/
            }
            yShift = (y == size-1)?yShift:-yShift;
        }

        placeRoad(yShift, xOffset, yOffset, canvas);

        // Adding tiles for each Colony
        Random rnd = new Random();

        // creation d'une liste avec les valeurs des biomes+
        ArrayList<Integer> listType = new ArrayList<>();
                
        for (int i=0; i<3*size*(size-1) ; i++){
            listType.add(i%5);

        }
        listType.add(5);

        ArrayList<Integer> listValue = new ArrayList<>();

        for (int i=0; i<3*size*(size-1); i++){
            if (i%10 == 0) listValue.add(6);
            if (i%10 == 1) listValue.add(8);
            if (i%10 == 2) listValue.add(5);
            if (i%10 == 3) listValue.add(9);
            if (i%10 == 4) listValue.add(4);
            if (i%10 == 5) listValue.add(10);
            if (i%10 == 6) listValue.add(3);
            if (i%10 == 7) listValue.add(11);
            if (i%10 == 8) listValue.add(2);
            if (i%10 == 9) listValue.add(12);

        }

        for(int y = 0; y < 2*size - 1; y++) {
            for(int x = 0; x < 2*size-Math.abs(size-y-1)-1; x++) {

                // TODO : 1 desert, min 2 de chaque type

                /*int value = rnd.nextInt(11)+2; // 2-12 , Pas de 7
                while (value == 7){
                    value = rnd.nextInt(11)+2;
                }*/

                // creation des tiles avec des biomes de la liste 
                int r = rnd.nextInt(listType.size());
                int type = listType.get(r);
                listType.remove(r);

                int value;
                if(type != 5) {
                    int q = rnd.nextInt(listValue.size());
                    value = listValue.get(q);
                    listValue.remove(q);
                }
                else value = 7;

                Colony[] adja = getAdjaToTile(x, y);

                Tiles tile = new Tiles(new Vector2(x, y), type, value); //ICI 
                for(int i = 2*x; i <= 2*x+2; i++) {
                    for(int j = y; j <= y+1; j++){
                        if(y < size - 1 && type != 5) map[j][i+(j-y)].add(tile);
                        else if(y == size - 1 && type != 5) map[j][i].add(tile);
                        else if(type != 5) map[j][i+(j-y+1)%2].add(tile);
                    }
                }    

                canvas.addTile(x, y, tile, type, size, adja);
            }
        }
        placePort(canvas, xOffset, yOffset);
    }

    private void placeRoad(int yShift, int xOffset, int yOffset, Canvas canvas) {
        int roadType = 1;

        for(int y = 0; y < map.length; y++) {
            for(int x = 0; x < map[y].length; x++) {
                yShift = -yShift;
                
                int xPos = (x-size-(y<size?y:2*size-y-1))*xOffset;
                int yPos = (int)((y-size+0.5f)*yOffset);

                Vector2[] adjacent = getAdjacent(x, y);
                if(adjacent[0] != null) 
                    map[y][x].setRoadR(canvas.addEmptyRoad(xPos+xOffset/2, yPos, x, y, adjacent[0].getX(), adjacent[0].getY(), roadType, size));
                if(roadType == 1 && adjacent[2] != null) 
                    map[y][x].setRoadSup(canvas.addEmptyRoad(xPos, yPos+yOffset/2, x, y, adjacent[2].getX(), adjacent[2].getY(), 2, size));
                else if(adjacent[2] != null) map[y][x].setRoadSup(map[adjacent[2].getY()][adjacent[2].getX()].getRoadSup());
                if(adjacent[1] != null) map[y][x].setRoadL(map[adjacent[1].getY()][adjacent[1].getX()].getRoadR());

                roadType = (roadType+1)%2;
            }
            yShift = (y == size-1)?yShift:-yShift;
            roadType = (y > size-2)?0:1;
        }
    }

    private void placePort(Canvas canvas, int xMult, int yMult) {
        PortOperation[] op = {
            new PortOperation() { public int x(int i, int size){return 2*i-size+2;} public int y(int i, int size){return size;} public int v(int state){return state==0?0:1;}},
            new PortOperation() { public int x(int i, int size){return size+1+i;} public int y(int i, int size){return size-i-1;} public int v(int state){return state==0?2:0;}},
            new PortOperation() { public int x(int i, int size){return 2*size-1-i;} public int y(int i, int size){return -(i+1);} public int v(int state){return state==0?4:2;}},
            new PortOperation() { public int x(int i, int size){return -(2*i-size+2);} public int y(int i, int size){return -size;} public int v(int state){return state==0?5:4;}},
            new PortOperation() { public int x(int i, int size){return -(size+i+1);} public int y(int i, int size){return i-size+1;} public int v(int state){return state==0?3:5;}},
            new PortOperation() { public int x(int i, int size){return -(2*size-1-i);} public int y(int i, int size){return i+1;} public int v(int state){return state==0?1:3;}}
        };

        int state = 0;
        /*
        int[] type = new int[2+4*(size-1)]; //nombre de bateau (a test avec grande map)

        for (int i=0; i<type.length; i++){
            if (type.length-i <=type.length%5){
                type[i] = 5;
            } else {
                type[i] = i%5;
            }
        }
        int index = 0;
        */

        Random rnd = new Random();
        ArrayList<Integer> bat = new ArrayList<>();

        for (int i=0; i<2+4*(size-1); i++){
            if ((2+4*(size-1))-i <=(2+4*(size-1))%5){
                bat.add(5);
            } else {
                bat.add(i%5);
            }
        }

        for(int j = 0; j < 6; j++) {
            for(int i = 0; i < size; i++) {
                if(state == 2 || state == 1 && i == size - 1) {
                    state = (state-2)%3;
                    continue;
                }

                int q = rnd.nextInt(bat.size());
                int type = bat.get(q);
                bat.remove(q);
                
                canvas.addPort(op[j].x(i, size)*xMult, op[j].y(i, size)*yMult, size, op[j].v(state), type);

                Vector2 port1 = new Vector2(0, 0);
                Vector2 port2 = new Vector2(0, 0);

                switch(j) {
                    case 0: port1 = new Vector2(2*(i+1), 0);
                        if(state == 0) port2 = new Vector2(1+2*i, 0);
                        else port2 = new Vector2(2*(i+1)+1, 0); break;
                    case 1: port1 = new Vector2(map[i+1].length-(i+1==size?1:2), i+1);
                        if(state == 0) port2 = new Vector2(map[i].length-1, i);
                        else port2 = new Vector2(map[i+1].length-1, i+1); break;
                    case 2: port1 = new Vector2(map[size+i].length-2, size+i);
                        if(state == 0) port2 = new Vector2(map[size+i].length-1, size+i);
                        else port2 = new Vector2(map[size+i+1].length-1, size+i+1); break;
                    case 3: port1 = new Vector2(2*(size-i-1), 2*size-1);
                        if(state == 0) port2 = new Vector2(2*(size-i-1)+1, 2*size-1);
                        else port2 = new Vector2(2*(size-i-1)-1, 2*size-1); break;
                    case 4: port1 = new Vector2(i+1==size?0:1, 2*size-i-2);
                        if(state == 0) port2 = new Vector2(0, 2*size-i-1);
                        else port2 = new Vector2(0, 2*size-i-2); break;
                    default: port1 = new Vector2(1, size-i);
                        if(state == 0) port2 = new Vector2(0, size-i);
                        else port2 = new Vector2(0, size-i+1); break;
                }

                map[port1.getY()][port1.getX()].addPort(type);
                map[port2.getY()][port2.getX()].addPort(type);

                //System.out.println("Adding port at ("+port1.getX()+";"+port1.getY()+") to ("+port2.getX()+";"+port2.getY()+")");*/

                state++;
            }

            state++;
        }

        System.out.println("All port placed");
    }

    public Colony[] getAdjaToTile(int x, int y) {
        Colony[] adja = new Colony[6];
        int counter = 0;

        for(int i = 2*x; i <= 2*x+2; i++) {
            for(int j = y; j <= y+1; j++){
                if(y < size - 1) 
                    adja[counter] = map[j][i+(j-y)];
                else if(y == size - 1) 
                    adja[counter] = map[j][i];
                else 
                    adja[counter] = map[j][i+(j-y+1)%2];
                counter++;
            }
        }    

        return adja;
    }

    public Colony getColony(int x, int y) { return map[y][x]; }
    public Colony[][] getMap() { return this.map; }
    public int getSize() { return this.size; }

    @Override
    public String toString() {
        StringBuilder bld = new StringBuilder();

        for(int x = 0; x < 5; x++) { 
            bld.append("\n");
            for(int y = 0; y < 5; y++) {
                bld.append(map[x*5+y].toString());
            } 
        }

        return bld.toString();
    }

    public boolean canBuildRoad(int j, int y1, int x1, int y2, int x2){
        return (map[y1][x1].getVillage() == j || map[y2][x2].getVillage() == j) ||
            (map[y1][x1].haveConn(j) || map[y2][x2].haveConn(j)) &&
            (map[y1][x1].getVillage() < 0 || map[y1][x1].getVillage() == j || 
            map[y2][x2].getVillage() < 0 || map[y2][x2].getVillage() == j);
    }

    public void buildRoad(int j, int y1, int x1, int y2, int x2){
        if (y1 != y2){
            map[y1][x1].setConnSup(j);
            map[y2][x2].setConnSup(j);
        }

        else if(x1 < x2) {
            map[y1][x1].setConnR(j);
            map[y2][x2].setConnL(j);
        }
        else {
            map[y1][x1].setConnL(j);
            map[y2][x2].setConnR(j);
        }
    }

    public boolean canBuildFirstVillage(int j, int x, int y){
        return map[y][x].getVillage() != -2;
    }

    public boolean canBuildVillage(int j, int x, int y){
        if (this.canBuildFirstVillage(j,x,y)) return map[y][x].haveConn(j);
        return false;
    }

    public void buildVillage(int j, int x, int y){
        map[y][x].setVillage(j);
        if (x>0) map[y][x-1].setVillage(-2);
        if (x<map[y].length-1) map[y][x+1].setVillage(-2);  

        /*if (x%2 == 0 && y > this.size) map[y-1][x+1].setVillage(-2);
        if (x%2 == 0 && y < this.size-1) map[y+1][x+1].setVillage(-2);
        if (x%2 == 0 && y == this.size) map[y-1][x].setVillage(-2);
        if (x%2 == 0 && y == this.size-1) map[y+1][x].setVillage(-2);

        if (x%2 == 1 && y > this.size && y<map.length-1) map[y+1][x-1].setVillage(-2);
        if (x%2 == 1 && y < this.size-1 && y>0) map[y-1][x-1].setVillage(-2);
        if (x%2 == 1 && y == this.size) map[y+1][x-1].setVillage(-2);
        if (x%2 == 1 && y == this.size-1) map[y-1][x-1].setVillage(-2);*/

        map[y+(x%2==0&&y>=size||x%2==1&&y<size&&y>0||y>=map.length-1?-1:1)][x+(x%2==1?-1:y==size||y==size-1?0:1)].setVillage(-2);
                                      
    }

    public void resetBlocked() {
        for(Colony[] colonies : map)  
            for(Colony colony : colonies)
                colony.setBlocked(-1);
    }

    public Vector2[] getAdjacent(int x, int y) {
        Vector2[] res = new Vector2[3];

        if(x != map[y].length - 1) res[0] = new Vector2(x+1, y);
        if(x != 0) res[1] = new Vector2(x-1, y);
        if(y < size - 1) {
            if(x%2 == 0) res[2] = new Vector2(x+1, y+1);
            else if(y != 0) res[2] = new Vector2(x-1, y-1);
        }
        else if(y == size - 1) {
            if(x%2 == 0) res[2] = new Vector2(x, y+1);
            else res[2] = new Vector2(x-1, y-1);
        }
        else if(y == size) {
            if(x%2 == 0) res[2] = new Vector2(x, y-1);
            else res[2] = new Vector2(x-1, y+1);
        }
        else {
            if(x%2 == 0) res[2] = new Vector2(x+1, y-1);
            else if(y != 2*size-1) res[2] = new Vector2(x-1, y+1);
        }

        return res;
    }

    public void printRoadConn() {
        for(int y = map.length-1; y >= 0; y--) {
            String res = "";
            for(int i = 0; i < Math.max(0, Math.abs(size-1-y))-(y>=size?1:0); i++) res += "           ";
            for(int x = 0; x < map[y].length; x++) {
                String conL = map[y][x].getConnL()==-1?"-1":" "+map[y][x].getConnL();
                String conS = map[y][x].getConnSup()==-1?"-1":" "+map[y][x].getConnSup();
                String conR = map[y][x].getConnR()==-1?"-1":" "+map[y][x].getConnR();

                res += "("+conL+";"+conS+";"+conR+") ";
            }
            System.out.println(res);
        }
    }

    public int computeLongestRoad(int player, Vector2 pos1, Vector2 pos2) {
        ArrayList<Vector2> closed = new ArrayList<>();
        int res1 = computeFirstPath(player, pos1, closed);

        closed.clear();
        int res2 = computeFirstPath(player, pos2, closed);

        return Math.max(res1, res2);
    }

    private int computeFirstPath(int p, Vector2 pos, ArrayList<Vector2> closed) {
        int res = 0;

        Vector2[] adja = getAdjacent(pos.getX(), pos.getY());
        if(map[pos.getY()][pos.getX()].getConnR() == p) {
            if(contain(closed, adja[0])) res += 1;
            else res += computeDirection(p, adja[0], closed, pos);
        }
        if(map[pos.getY()][pos.getX()].getConnL() == p && !contain(closed, adja[1])) {
            if(contain(closed, adja[1])) res += 1;
            else res += computeDirection(p, adja[1], closed, pos);
        }
        if(map[pos.getY()][pos.getX()].getConnSup() == p && !contain(closed, adja[2])) {
            if(contain(closed, adja[2])) res += 1;
            else res += computeDirection(p, adja[2], closed, pos);
        }

        return res;
    }

    private int computeDirection(int p, Vector2 pos, ArrayList<Vector2> closed, Vector2 prec) {
        closed.add(pos);
        int[] res = new int[3];

        Vector2[] adja = getAdjacent(pos.getX(), pos.getY());
        if(adja[0] != null && !adja[0].equals(prec) && map[pos.getY()][pos.getX()].getConnR() == p) {
            if(contain(closed, adja[0])) res[0] = 1;
            else res[0] = computeDirection(p, adja[0], closed, pos);
        }
        if(adja[1] != null && !adja[1].equals(prec) && map[pos.getY()][pos.getX()].getConnL() == p && !contain(closed, adja[1])) {
            if(contain(closed, adja[1])) res[1] = 1;
            else res[1] = computeDirection(p, adja[1], closed, pos);
        }
        if(adja[2] != null && !adja[2].equals(prec) && map[pos.getY()][pos.getX()].getConnSup() == p && !contain(closed, adja[2])) {
            if(contain(closed, adja[2])) res[2] = 1;
            else res[2] = computeDirection(p, adja[2], closed, pos);
        }

        int max = 0;
        for(int i = 0; i < 3; i++) if(res[i] > max) max = res[i];

        return 1+max;
    }

    private boolean contain(ArrayList<Vector2> list, Vector2 v) {
        for(Vector2 vector : list) 
            if(vector.equals(v))
                return true;
        return false;
    }

    public interface PortOperation {
        public int x(int i, int size);
        public int y(int i, int size);
        public int v(int state);

        static int s(int state, int x, int y) {
            return state==0?x:y;
        }
    }
}
