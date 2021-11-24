import GameEngine.Vector2;

import java.util.ArrayList;
import java.util.Random;

import GameEngine.GameObject;
import GameEngine.UI;

public class Map {
    
    private int size = 3;

    private Colony[][] map;

    public Map(int size, UI ui, Game game) {
        String[] files = {
            "./Images/GamePage/wheat.png",
            "./Images/GamePage/lumber.png",
            "./Images/GamePage/sheep.png",
            "./Images/GamePage/ore.png",
            "./Images/GamePage/brick.png",
            "./Images/GamePage/Desert.png"
        };

        int tileSize = 175;
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

                game.addEmptyVillage((x-size-i)*xOffset, (int)((y-size+0.5f)*yOffset+yShift), x, y);
                yShift = -yShift;

                int nextX = x+(x%2==1?-1:y==size||y==size-1?0:1);
                int nextY = y+(x%2==0&&y>=size||x%2==1&&y<size&&y>0||y>=map.length-1?-1:1);

                if(x != 2*(size+i)) game.addEmptyRoad((x-size-i)*xOffset+xOffset/2, (int)((y-size+0.5f)*yOffset), x, y, x+1, y, roadType);
                if(roadType == 1 && y != 2*size-1) game.addEmptyRoad((x-size-i)*xOffset, (int)((y-size+0.5f)*yOffset)+yOffset/2, x, y, nextX, nextY, 2);
                
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

                Tiles tile = new Tiles(new Vector2(x, y), type, value);
                for(int i = 2*x; i <= 2*x+2; i++) {
                    for(int j = y; j <= y+1; j++){
                        if(y < size - 1) map[j][i+(j-y)].add(tile);
                        else if(y == size - 1) map[j][i].add(tile);
                        else map[j][i+(j-y+1)%2].add(tile);
                    }
                }    

                GameObject obj = new GameObject(files[type], tileSize, tileSize);       // creation d'un objet
                obj.renderer().setZindex(1);
                obj.transform().setPosition(Vector2.multiply(new Vector2(2*(x+Math.max(0, y-size+1))-(size-1+y), size-y-1), new Vector2(xOffset, yOffset)));
                obj.renderer().mix(tile.getImage());

                ui.add(obj);
            }
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

        if (x%2 == 0 && y > this.size) map[y-1][x+1].setVillage(-2);
        if (x%2 == 0 && y < this.size-1) map[y+1][x+1].setVillage(-2);
        if (x%2 == 0 && y == this.size) map[y-1][x].setVillage(-2);
        if (x%2 == 0 && y == this.size-1) map[y+1][x].setVillage(-2);

        if (x%2 == 1 && y > this.size && y<map.length-1) map[y+1][x-1].setVillage(-2);
        if (x%2 == 1 && y < this.size-1 && y>0) map[y-1][x-1].setVillage(-2);
        if (x%2 == 1 && y == this.size) map[y+1][x-1].setVillage(-2);
        if (x%2 == 1 && y == this.size-1) map[y-1][x-1].setVillage(-2);

        map[y+(x%2==0&&y>=size||x%2==1&&y<size&&y>0||y>=map.length-1?-1:1)][x+(x%2==1?-1:y==size||y==size-1?0:1)].setVillage(-2);
                                      
    }
}
