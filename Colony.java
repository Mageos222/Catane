import GameEngine.GameObject;

public class Colony {
    private static String[] files = {
        "Images/HouseRed.png",
        "Images/HouseBlue.png",
        "Images/HouseGreen.png",
        "Images/HousePink.png"
    };

    private GameObject obj;
    private final int color;

    public Colony(int color) {
        this.color = color;
        this.obj = new GameObject(files[color], 50, 50);
    }
}
