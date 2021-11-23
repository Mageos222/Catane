public class Ressource {

    enum Res  { FIELD, FOREST, PASTURE, ROCK, CLAY, DESERT }

    private int[] ressource;

    public Ressource() {
        ressource = new int[5];
    }

    public Ressource(int t, int val) {
        this();
        ressource[t] = val;
    }

    public int getRessource(int t) { return ressource[t]; }

    public boolean contain(Ressource r) {
        for(int i = 0; i < 5; i++) 
            if(ressource[i] < r.getRessource(i))
                return false;
        return true;
    }

    public void add(int t, int v) { this.ressource[t] += v; }
    public void add(Ressource r) {
        for(int i = 0; i < 5; i++) 
            ressource[i] += r.getRessource(i);
    }

    // 5 getters 
    // 1 setter (type t, int val)
}
