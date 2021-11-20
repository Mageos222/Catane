import GameEngine.Vector2;

import java.util.ArrayList;
import java.util.Random;

import GameEngine.CircleCollider;
import GameEngine.GameObject;
import GameEngine.UI;

public class Map {
    
    private int size = 3;

    private Colony[][] map;

    public Map(int size, UI ui, Game game) {
        String[] files = {
            "./Images/wheat.png",
            "./Images/lumber.png",
            "./Images/sheep.png",
            "./Images/ore.png",
            "./Images/brick.png",
            "./Images/Desert.png"
        };

        int tileSize = 175;
        int xOffset = (int)(0.5f*tileSize);
        int yOffset = (int)(0.74f*tileSize);

        this.size = size;
        int yShift = (int)(0.1f*tileSize);

        // Map Init
        map = new Colony[size*2][];
        for(int y = 0; y < size * 2; y++) {
            int i = (y < size)?y:2*size-y-1;
            map[y] = new Colony[2*(size+i)+1];
            for(int x = 0; x < 2*(size+i)+1; x++) {
                map[y][x] = new Colony();

                game.addEmptyVillage((x-size-i)*xOffset, (int)((y-size+0.5f)*yOffset+yShift));
                yShift = -yShift;
            }
            yShift = (y == size-1)?yShift:-yShift;
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

                int value = rnd.nextInt(11)+2;
                while (value == 7){
                    value = rnd.nextInt(11)+2;
                }

                // creation des tiles avec des biomes de la liste 
                int r = rnd.nextInt(listType.size());
                int type = listType.get(r);
                listType.remove(r);

                Tiles tile = new Tiles(new Vector2(x, y), type, value);
                for(int i = 2*x; i < 2*x+2; i++) {
                    for(int j = y; j <= y+1; j++){
 
                        if(y < size - 1) map[j][i+j].add(tile);
                        else map[j][i+(j+1)%2].add(tile);
                    }
                }    

                GameObject obj = new GameObject(files[type], tileSize, tileSize);       // creation d'un objet
                obj.renderer().setZindex(1);
                obj.transform().setPosition(Vector2.multiply(new Vector2((size-1+y)-2*(x+Math.max(0, y-size+1)), size-y-1), new Vector2(xOffset, yOffset)));
                obj.renderer().mix(tile.getImage());

                ui.add(obj);
            }
        }
    }

    public Colony[][] getMap() { return map; }

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

}
