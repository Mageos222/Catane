import GameEngine.GameObject;
import GameEngine.Vector2;

import java.util.ArrayList;
import java.util.Random;

public class Map {
    
    private int size = 3;

    private Colony[][] map;

    public Map(int size, Canvas canvas) {

        int tileSize = 520/size;
        int xOffset = (int)(0.5f*tileSize);
        int yOffset = (int)(0.74f*tileSize);

        this.size = size;
        int yShift = (int)(0.1f*tileSize);

        // Map Init
        map = new Colony[size*2][];
        int roadType = 1;

        for(int y = 0; y < size * 2; y++) {
            int i = (y < size)?y:2*size-y-1;
            map[y] = new Colony[2*(size+i)+1];
            for(int x = 0; x < 2*(size+i)+1; x++) {
                GameObject object = canvas.addEmptyVillage((x-size-i)*xOffset, (int)((y-size+0.5f)*yOffset+yShift), x, y, size);
                map[y][x] = new Colony(object);

                yShift = -yShift;

                int nextX = x+(x%2==1?-1:y==size||y==size-1?0:1);
                int nextY = y+(x%2==0&&y>=size||x%2==1&&y<size&&y>0||y>=map.length-1?-1:1);

                if(x != 2*(size+i)) canvas.addEmptyRoad((x-size-i)*xOffset+xOffset/2, (int)((y-size+0.5f)*yOffset), x, y, x+1, y, roadType, size);
                if(roadType == 1 && y != 2*size-1) canvas.addEmptyRoad((x-size-i)*xOffset, (int)((y-size+0.5f)*yOffset)+yOffset/2, x, y, nextX, nextY, 2, size);
                
                roadType = (roadType+1)%2;
            }
            yShift = (y == size-1)?yShift:-yShift;
            roadType = (y > size-2)?0:1;
        }

        // Adding tiles for each Colony
        Random rnd = new Random();

        // creation d'une liste avec les valeurs des biomes+
        ArrayList<Integer> listType = new ArrayList<>();
                
        for (int i=0; i<((2*size)-1)*((2*size)-1)-7 ; i++){
            listType.add(i%5);

        }
        listType.add(5);

        ArrayList<Integer> listValue = new ArrayList<>();

        for (int i=0; i<((2*size)-1)*((2*size)-1)-6 ; i++){
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

                int q = rnd.nextInt(listValue.size());
                int value = listValue.get(q);
                listValue.remove(q);



                Colony[] adja = new Colony[6];
                int counter = 0;

                Tiles tile = new Tiles(new Vector2(x, y), type, value); //ICI 
                for(int i = 2*x; i <= 2*x+2; i++) {
                    for(int j = y; j <= y+1; j++){
                        if(y < size - 1) {
                            if(type != 5) map[j][i+(j-y)].add(tile);
                            adja[counter] = map[j][i+(j-y)];
                        }
                        else if(y == size - 1) {
                            if(type != 5) map[j][i].add(tile);
                            adja[counter] = map[j][i];
                        }
                        else {
                            if(type != 5) map[j][i+(j-y+1)%2].add(tile);
                            adja[counter] = map[j][i+(j-y+1)%2];
                        }
                        counter++;
                    }
                }    

                canvas.addTile(x, y, tile, type, size, adja);
            }
        }
        placePort(canvas, xOffset, yOffset);
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

    public Colony getColony(int x, int y) { return map[y][x]; }
    public Colony[][] getMap() { return this.map; }

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
        if (y1>y2 || y2>y1){
            map[y1][x1].setConnSup(j);
            map[y2][x2].setConnSup(j);
        }

        if (x1<x2){
            map[y1][x1].setConnL(j);
            map[y2][x2].setConnR(j);  
        }

        if(x2>x1){
            map[y1][x1].setConnR(j);
            map[y2][x2].setConnL(j);
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
                colony.setBlocked(false);
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
