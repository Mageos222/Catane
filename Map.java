import java.util.Random;

public class Map {
    
    private Tiles[] map;
    private int size = 3;

    public Map(int size) {
        this.size = size;
        map = new Tiles[3*size*(size-1)+1];

        Random rnd = new Random();

        /*int discount = 0;

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
        }*/

        int counter = 0;
        for(int i = 1; i <= size; i++) {
            for(int x = 2*(i-1); x >= -2*(i-1); x--) {
                for(int y = i-1; y >= -i+1; y--) {
                    if(Math.abs(x)+Math.abs(y)==2*(i-1) || (Math.abs(x) == Math.abs(y)-2 && Math.abs(x)+Math.abs(y)>=i-1)) {
                        int type = rnd.nextInt(6);
                        int value = rnd.nextInt(11)+2;

                        map[counter] = new Tiles(new Vector2(x, y), type, value);
                        counter++;
                    }
                }
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
