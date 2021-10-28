public class Tiles {

    public enum Env { FIELD, FOREST, PASTURE, ROCK, CLAY, DESERT }

    private final Vector2 position;
    private Env type;
    private int value;

    public Tiles(Vector2 position, int type) 
    {
        this.position = position;
        this.type = Env.values()[type];
    }

    @Override
    public String toString() {
        return "Tiles of type " + type.toString() + " and value " + value + " at (" + position.toString();
    }
}
