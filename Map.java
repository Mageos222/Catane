import GameEngine.Vector2;

import java.rmi.server.Operation;
import java.util.ArrayList;
import java.util.Random;

import GameEngine.CircleCollider;
import GameEngine.GameObject;
import GameEngine.UI;

public class Map {
    
    private int size = 3;

    private Colony[][] map;

    public Map(int size, UI ui, Canvas canvas, Controller controller) {
        String[] files = {
            "Images/GamePage/wheat.png",
            "Images/GamePage/lumber.png",
            "Images/GamePage/sheep.png",
            "Images/GamePage/ore.png",
            "Images/GamePage/brick.png",
            "Images/GamePage/Desert.png"
        };

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
                map[y][x] = new Colony();

                canvas.addEmptyVillage((x-size-i)*xOffset, (int)((y-size+0.5f)*yOffset+yShift), x, y, size);
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
        ArrayList<Integer> listType = new ArrayList<Integer>();
                
        for (int i=0; i<5; i++){
            listType.add(i);
            listType.add(i);
        }
        listType.add(5);

        for(int i=0; i<3*size*(size-1)-10; i++){
            listType.add(rnd.nextInt(5));
        }

        for(int y = 0; y < 2*size - 1; y++) {
            for(int x = 0; x < 2*size-Math.abs(size-y-1)-1; x++) {

                // TODO : 1 desert, min 2 de chaque type

                int value = rnd.nextInt(11)+2; // 2-12 , Pas de 7
                while (value == 7){
                    value = rnd.nextInt(11)+2;
                }

                // creation des tiles avec des biomes de la liste 
                int r = rnd.nextInt(listType.size());
                int type = listType.get(r);
                listType.remove(r);

                Colony[] adja = new Colony[6];
                int counter = 0;

                Tiles tile = new Tiles(new Vector2(x, y), type, value);
                for(int i = 2*x; i <= 2*x+2; i++) {
                    for(int j = y; j <= y+1; j++){
                        if(y < size - 1) {
                            map[j][i+(j-y)].add(tile);
                            adja[counter] = map[j][i+(j-y)];
                        }
                        else if(y == size - 1) {
                            map[j][i].add(tile);
                            adja[counter] = map[j][i];
                        }
                        else {
                            map[j][i+(j-y+1)%2].add(tile);
                            adja[counter] = map[j][i+(j-y+1)%2];
                        }
                        counter++;
                    }
                }    

                Vector2 pos = Vector2.multiply(new Vector2(2*(x+Math.max(0, y-size+1))-(size-1+y), size-y-1), new Vector2(xOffset, yOffset));

                GameObject obj = new GameObject(files[type], tileSize, tileSize);       // creation d'un objet
                obj.renderer().setZindex(1);
                obj.transform().setPosition(pos);
                obj.renderer().mix(tile.getImage());
                
                obj.addComponent(new CircleCollider(obj));
                obj.collider().setOnHoverEnterAction(() -> controller.moveVoleur(pos.getX(), pos.getY()));
                obj.collider().setOnHoverExitAction(() -> { });
                obj.collider().setOnMouseClickedAction(() -> controller.putVoleur(adja));

                ui.add(obj);

                if(type == 5) { 
                    controller.moveVoleur(pos.getX(), pos.getY());
                    controller.putVoleur(adja);
                }
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
        int[] type = new int[(3*size*(size-1))+1];

        for (int i=0; i<type.length; i++){
            if (type.length-i <5){
                type[i] = 5;
            } else {
                type[i] = i%5;
            }
        }
        int index = 0;

        for(int j = 0; j < 6; j++) {
            for(int i = 0; i < size; i++) {
                if(state == 2 || state == 1 && i == size - 1) {
                    state = (state-2)%3;
                    continue;
                }
                canvas.addPort(op[j].x(i, size)*xMult, op[j].y(i, size)*yMult, size, op[j].v(state), type[index]);

                if(j==0) {
                    map[0][i].addPort(type[index]);
                    map[0][i+1].addPort(type[index]);
                }
                else if(j==3) {
                    map[2*size-1][size-i].addPort(type[index]);
                    map[2*size-1][size-i-1].addPort(type[index]);
                }

                state++;
                index++;
            }

            state++;
        }

    }

    public Colony getColony(int x, int y) { return map[y][x]; }

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
        //if (map[y1][x1].getVillage() == j || map[y2][x2].getVillage() == j) return true;
        //if (map[y1][x1].haveConn(j) && (map[y1][x1].getVillage() == -1 || map[y1][x1].getVillage() == j)) return true ;
        //if (map[y2][x2].haveConn(j) && (map[y2][x2].getVillage() == -1 || map[y2][x2].getVillage() == j)) return true ;
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
