public class Tiles {

    public enum Env { FIELD, FOREST, PASTURE, ROCK, CLAY, DESERT }
    private static String[] files = {
        "./Images/Field.png",
        "./Images/Forest.png",
        "./Images/Pasture.png",
        "./Images/Rock.png",
        "./Images/Clay.png",
        "./Images/Desert.png"
    };

    private GameObject obj;
    private Env type;
    private int value;

    public Tiles(Vector2 position, int type)
    {
        this.obj = new GameObject(files[type], 100, 100);
        this.obj.setPosition(position.x, position.y);

        this.type = Env.values()[type];
    }

    public GameObject getObject() { return obj; }

    @Override
    public String toString() {
        return "Tiles of type " + type.toString() + " and value " + value;
    }
}
