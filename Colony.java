import java.util.Vector;

import GameEngine.GameObject;
import GameEngine.Vector2;

public class Colony {

    private GameObject object;

    private int connR;
    private GameObject roadR;
    private int connL;
    private GameObject roadL;
    private int connSup;
    private GameObject roadSup;
    private int village;
    private Vector2 position;

    private Tiles[] tiles;
    private int level;

    private int isBlocked;
    private int port = -1;

    public Colony(GameObject object, int x, int y) {
        this.object = object;

        this.tiles = new Tiles[0];
        this.village = -1;

        this.connR = -1;
        this.connL = -1;
        this.connSup = -1;

        this.isBlocked = -1;
        this.position = new Vector2(x, y);

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

    public void addPort(int p) { this.port = p; }
    public int getPort() { return this.port; }

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
        boolean hasBeenBlocked = false;

        for(Tiles tile : tiles) {
            if((tile.getValue() == value || total) && tile.getType() < 5 && (isBlocked != tile.getType() || hasBeenBlocked)) 
                res.add(tile.getType(), level);
            if(isBlocked == tile.getType()) hasBeenBlocked = true;
        }


        return res;
    }

    public void cutConnection() {
        if(village < 0) return;
        if(connR != village) connR = -1;
        if(connL != village) connL = -1;
        if(connSup != village) connSup = -1;
    }

    public void upgrade() { this.level = 2; }
    public boolean isTown() { return this.level == 2; }

    public void setBlocked(int v) { this.isBlocked = v; }
    public int isBlocked() { return this.isBlocked; }

    public Tiles[] getTiles() { return this.tiles; }
    public GameObject getObject() { return this.object; }

    public void setRoadR(GameObject road) { this.roadR = road; }
    public void setRoadL(GameObject road) { this.roadL = road; }
    public void setRoadSup(GameObject road) { this.roadSup = road; }
    public GameObject getRoadR() { return this.roadR; }
    public GameObject getRoadL() { return this.roadL; }
    public GameObject getRoadSup() { return this.roadSup; }
    public Vector2 getPosition() { return this.position; }

    @Override
    public String toString() {
        String res = "";
        for(Tiles tile : tiles) {
            String t;
            res += "- type : " + getType(tile.getType()) + "\n";
        }
        if(port != -1) res += "- port : " + getType(port) + "\n";
        return res;
    }

    private String getType(int t) {
        switch(t) {
            case 0: return "Field"; 
            case 1: return "Forest"; 
            case 2: return "Pasture"; 
            case 3: return "Rock";
            case 4: return "Clay";
            default: return "Desert";
        }
    }
}
