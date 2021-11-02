import java.awt.*;
import java.awt.image.BufferedImage;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class GameObject {
    private Image image;
    private File file;

    private int width;
    private int height;

    private int posX;
    private int posY;

    private int z_index = 0;

    private boolean interactable = false;

    private boolean isHover;
    private boolean visible;

    private Action onHoverEnter;
    private Action onHoverExit;
    private Action onMouseClicked;

    private Distance dst;

    public GameObject(String file, int width, int height) {        
        this.file = new File(file);

        this.visible = true;
        scale(width, height);

        init();
    }

    public GameObject(String file) {
        this.file = new File(file);
        try {
            BufferedImage original = ImageIO.read(this.file);
            this.width = original.getWidth();
            this.height = original.getHeight();

            this.image = ImageIO.read(this.file);
        }
        catch(IOException e) {
            System.out.println("Error while opening file : " + e);
        }

        this.visible = true;

        init();
    }

    public GameObject(int width, int height) {
        this.visible = false;
        this.width = width;
        this.height = height;

        init();
    }

    public void init() {
        this.dst = this.circle;

        this.onHoverEnter = ui -> { };
        this.onHoverExit = ui -> { };
        this.onMouseClicked = ui -> { };
    }

    public void setPosition(int x, int y) {
        this.posX = x;
        this.posY = y;
    }

    public void translate(int x, int y) {
        this.posX += x;
        this.posY += y;
    }

    public boolean scale(int width, int height) {
        if(width == this.width && height == this.height) return false;

        BufferedImage originalImage;
        try {
            originalImage = ImageIO.read(this.file);
            this.image = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            this.height = height;
            this.width = width;
        } catch (IOException e) {
            System.out.println("error while resizing image : " + e);
        }

        return true;
    }

    private Distance circle = new Distance() {
        public boolean isOn(Point p) {
            return distanceFrom(p) < width/2;
        }
        public double distanceFrom(Point p) {
            Point pos = new Point();
            pos.setLocation(getCenterX(), getCenterY());
            return pos.distance(p);
        }
    };
    private Distance square = new Distance() {
        public boolean isOn(Point p) {
            return p.getX() < getCenterX()+width/2 && p.getX() > getCenterX()-width/2 &&
                p.getY() < getCenterY()+height/2 && p.getY() > getCenterY()-height/2;
        }
        public double distanceFrom(Point p) {
            if(isOn(p)) return 0;
            Point pos = new Point();
            pos.setLocation(getCenterX(), getCenterY());
            return pos.distance(p);
        }
    };

    public void setDistanceToSquare() { this.dst = square; }
    public void setDistanceToCircle() { this.dst = circle; }

    public boolean isOn(Point p) { return dst.isOn(p); }
    public double distanceFrom(Point p) { return dst.distanceFrom(p); }

    public void setOnHoverEnterAction(Action action) { this.onHoverEnter = action; }
    public void setOnHoverExitAction(Action action) { this.onHoverExit = action; }
    public void setOnMouseClickedAction(Action action) { this.onMouseClicked = action; }

    public void onHoverEnter(UI ui) { onHoverEnter.execute(ui); }
    public void onHoverExit(UI ui) { onHoverExit.execute(ui); }
    public void onMouseClicked(UI ui) { onMouseClicked.execute(ui); }

    public void setHover(boolean v) { this.isHover = v; }
    public boolean isHover() { return isHover; }

    public void setVisible(boolean v) { this.visible = v; }
    public boolean isVisible() { return this.visible; }

    public void setInteractable(boolean v) { interactable = v; }
    public boolean isInteractable() { return interactable; }

    public void setZindex(int z) { z_index = z; }
    public int getZindex() { return z_index; }

    public int getPosX() { return this.posX - width/2; }
    public int getPosY() { return this.posY - height/2; }

    public int getCenterX() { return posX; }
    public int getCenterY() { return posY; }

    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public Image getImage() { return this.image; }
    public void setImage(Image img) { this.image = img; }
}

interface Distance {
    public boolean isOn(Point p);
    public double distanceFrom(Point p);
}