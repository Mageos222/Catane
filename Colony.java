public class Colony {

    private int connR;
    private int connL;
    private int connSup;
    private int village;

    private Tiles[] tiles;
    private int level;

    public Colony() {
        this.tiles = new Tiles[0];
        this.village = -1;

        tiles = new Tiles[0];
        level = 1;
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
        
    public Ressource collect(int value) { return collect(value, false); }
    public Ressource collect() { return collect(0, true); }

    private Ressource collect(int value, boolean total) {
        Ressource res = new Ressource();
        for(Tiles tile : tiles) 
            if((tile.getValue() == value || total) && tile.getType() < 5)
                res.add(tile.getType(), level);

        return res;
    }

    public void upgrade() { this.level = 2; }

    @Override
    public String toString() {
        String res = "";
        for(Tiles tile : tiles) {
            String t;
            switch(tile.getType()) {
                case 0: t = "Field"; break;
                case 1: t = "Forest"; break;
                case 2: t = "Pasture"; break;
                case 3: t = "Rock"; break;
                case 4: t = "Clay"; break;
                default: t = "Desert"; break;
            }
            res += "- type : " + t + "\n";
        }
        return res;
    }
}
