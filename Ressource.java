public class Ressource {

    private int[] ressource;

    public Ressource() {
        ressource = new int[5];
    }

    public Ressource(int t, int val) {
        this();
        ressource[t] = val;
    }

    public Ressource(int wheat, int wood, int sheep, int rock, int clay) {
        this();
        ressource[0] = wheat;
        ressource[1] = wood;
        ressource[2] = sheep;
        ressource[3] = rock;
        ressource[4] = clay;
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
    public void remove(Ressource r) {
        for(int i = 0; i < 5; i++) 
            ressource[i] -= r.getRessource(i);
    }

    public int sum() {
        int res = 0;
        for(int r : ressource)
            res += r;
        return res;
    }

    @Override
    public boolean equals(Object o) {
        Ressource r;
        try {
            r = (Ressource)o;
        }
        catch(ClassCastException e) {
            System.out.println("o must be a Ressource");
            return false;
        }
        for(int i = 0; i < 5; i++)
            if(ressource[i] != r.getRessource(i))
                return false;
        return true;
    }

    // 5 getters 
    // 1 setter (type t, int val)
}
