import GameEngine.Vector2;
import java.util.Random;

import GameEngine.GameObject;
import GameEngine.UI;

public class Map {
    
    private Tiles[][] map;
    private int size = 3;

    private Colony[][] map2;

    public Map(int size, UI ui) {
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
        //map = new Tiles[3*size*(size-1)+1];

        Random rnd = new Random();

        this.map = new Tiles[size*2-1][];
        for(int y = 0; y < 2*size - 1; y++) {
            this.map[y] = new Tiles[2*size-Math.abs(size-y-1)-1];
            for(int x = 0; x < 2*size-Math.abs(size-y-1)-1; x++) {
                int type = rnd.nextInt(6);
                int value = rnd.nextInt(11)+2;
                this.map[y][x] = new Tiles(new Vector2(x, y), type, value);

                GameObject obj = new GameObject(files[type], tileSize, tileSize);       // creation d'un objet
                obj.renderer().setZindex(1);
                obj.transform().setPosition(Vector2.multiply(new Vector2((size-1+y)-2*(x+Math.max(0, y-size+1)), size-y-1), new Vector2(xOffset, yOffset)));

                ui.add(obj);
            }
        }

        // creation des colony
        int y = 6 + (2*(size -5));
        map2 = new Colony[y][];
        for (int i=0; i<y/2; i++){
            map2[i] = new Colony[7 + 2*i];
            map2[y-i-1] = new Colony[7 + 2*i];
        }
        if (y%2 == 1) map2[y/2+1] = new Colony[7 + y - 1];  // au cas ou il y a un nombre impair de case [y]

        // creation du Colony vides + ajout des Tiles
        for (int a=0; a<y; a++){
            for (int b=0; b<map2[a].length; b++){
                map2[a][b] = new Colony();
                map2[a][b].add();       // ajouter Tiles
            }
        }




        /*this.map2 = new Colony[2*size][];
        for(int y = 0; y <= size; y++) {
            this.map2[y] = new Colony[4*size-2*(size-y-1)-1];
            this.map2[2*size-y-1] = new Colony[4*size-2*(size-y-1)-1];
            for(int x = 0; x < 4*size-2*(size-y-1)-1; x++) {
                this.map2[y][x] = new Colony();
                this.map2[2*size-y-1][x] = new Colony();
            }
        }*/
    }

    public Tiles[][] getMap() { return map; }

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
