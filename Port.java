public class Port extends Player {

    private int type;
    private int cost;

    public Port(int type, int cost) {
        super("Port " + type, true);
        this.type = type;
        this.cost = cost;

        receive(new Ressource(type, 3));
    }

    @Override
    public void collect(int value) {
        // Do nothing
    }
    @Override
    public void collect() {
        //  DO nothing
    }

    public int getCost() { return this.cost; }
    public int getType() { return this.type; }

}
