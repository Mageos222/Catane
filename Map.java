import java.util.Random;

public class Map {
    
    private Tiles[] map;

    public Map() {
        map = new Tiles[25];

        Random rnd = new Random();

        for(int x = 0; x < 5; x++) {
            for(int y = 0; y < 5; y++) {
                int value = rnd.nextInt(6);

                map[x*5+y] = new Tiles(new Vector2(x, y), value);
            }
        }
    }

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
