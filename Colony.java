public class Colony {

    private int connR;
    private int connL;
    private int connSup;
    private int village;

    //private int[] values;
    //private Ressource[] ressources;

    private Tiles[] tiles;

    public Colony() {
        this.tiles = new Tiles[0];
        this.village = -1;

    }

    public void add(Tiles tile){
        Tiles[] n = new Tiles[tiles.length+1];
        for(int i = 0; i < tiles.length; i++)
            n[i] = tiles[i];
        n[tiles.length] = tile;
        tiles = n;
    }

    public int getVillage(){
        return this.village;
    }

    public int getConnL(){
        return this.connL;
    }

    public int getConnR(){
        return this.connR;
    }

    public int getConnSup(){
        return this.connSup;
    }

    public void setVillage(int j){
        this.village=j;
    }

    public void setConnL(int j){
        this.connL = j;
    }

    public void setConnR(int j){
        this.connR = j;
    }

    public void setConnSup(int j){
        this.connSup = j;
    }

    public boolean haveConn(int j){
        return this.connR == j || this.connL == j || this.connSup == j;
    }
}
