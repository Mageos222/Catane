public class Colony {

    private int connR;
    private int connL;
    private int connSup;

    //private int[] values;
    //private Ressource[] ressources;

    private Tiles[] tiles;

    public Colony() {
        tiles = new Tiles[3];
    }

    public void add(Tiles tile){
        Tiles[] n = new Tiles[tiles.length+1];
        for(int i = 0; i < tiles.length; i++)
            n[i] = tiles[i];
        n[tiles.length] = tile;
        tiles = n;
    }
}
