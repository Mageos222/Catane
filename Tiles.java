import GameEngine.GameObject;
import GameEngine.Vector2;

import java.io.File;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Tiles {

    public enum Env { FIELD, FOREST, PASTURE, ROCK, CLAY, DESERT }
    private static String[] files = {
        "./Images/wheat.png",
        "./Images/lumber.png",
        "./Images/sheep.png",
        "./Images/ore.png",
        "./Images/brick.png",
        "./Images/Desert.png"
    };

    private GameObject obj;
    private Env type;
    private int value;

    public Tiles(Vector2 position, int type, int value)
    {
        this.obj = new GameObject(files[type], 100, 100);
        this.obj.transform().setPosition(position.getX(), position.getY());

        this.type = Env.values()[type];
        this.value = value;

        File file = new File("Images/Jeton"+value+".png");
        try {
            BufferedImage originalImage = ImageIO.read(file);
            Image image = originalImage.getScaledInstance(40, 40, Image.SCALE_SMOOTH);
            obj.renderer().mix(image);
        }
        catch(IOException e) {
            System.out.println("Error while opening Jeton file " + value + " : " + e);
        }
    }

    public GameObject getObject() { return obj; }

    @Override
    public String toString() {
        return "Tiles of type " + type.toString() + " and value " + value;
    }
}
