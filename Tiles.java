import GameEngine.GameObject;
import GameEngine.Vector2;

import java.io.File;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Tiles {

    public enum Env { FIELD, FOREST, PASTURE, ROCK, CLAY, DESERT }

    private int type;
    private int value;

    private int level;

    public Tiles(Vector2 position, int type, int value)
    {
        this.type = type;
        this.value = value;

        this.level = 1;
    }

    public int getValue() { return this.value; }
    public int getType() { return this.type; }
    public int getLevel() { return this.level; }

    public Image getImage() {
        File file = new File("Images/Jeton/Jeton"+value+".png");
        try {
            BufferedImage originalImage = ImageIO.read(file);
            return originalImage.getScaledInstance(60, 60, Image.SCALE_SMOOTH);
        }
        catch(IOException e) {
            System.out.println("Error while opening Jeton file " + value + " : " + e);
            return null;
        }
    }

    @Override
    public String toString() {
        return "Tiles of type " + /*type.toString() +*/ " and value " + value;
    }
}
