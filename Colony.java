public class Colony {

    private int connR;
    private int connL;
    private int connSup;

    private Tiles[] tiles;
    private int level;

    public Colony() {
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
