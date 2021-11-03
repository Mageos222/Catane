import java.util.Random;

public class Map {
    
    private Tiles[] map;

    public Map() {
        map = new Tiles[19];

        Random rnd = new Random();

        int discount = 0;

        for(int x = 0; x < 5; x++) {
            for(int y = 0; y < 5; y++) {
                if((y == 0 || y == 4) && (x == 0 || x == 4) || (y%2==1 && x == 4)) {
                    discount++;
                    continue;
                }

                int type = rnd.nextInt(6);
                int value = rnd.nextInt(11)+2;

                map[x*5+y - discount] = new Tiles(new Vector2(-98*(2-x)+49*(y%2), -75*(2-y)), type, value);
            }
        }
    }

    public Tiles[] getMap() { return map; }

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
