import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class GameObject {
    private Image[] image;
    private File[] file;

    private int width;
    private int height;

    private int posX;
    private int posY;

    private int z_index = 0;
    private int renderIndex = 0;

    private boolean interactable = false;

    private boolean isHover;
    private boolean visible;

    private Action onHoverEnter;
    private Action onHoverExit;
    private Action onMouseClicked;

    private Distance dst;

    public GameObject(String file, int width, int height) {    
        this.file = new File[1];    
        this.image = new Image[1];
        this.file[0] = new File(file);

        this.visible = true;
        scale(width, height);

        init();
    }

    public GameObject(String file) {
        this.file = new File[1];    
        this.image = new Image[1];
        this.file[0] = new File(file);
        try {
            BufferedImage original = ImageIO.read(this.file[0]);
            this.width = original.getWidth();
            this.height = original.getHeight();

            this.image[0] = ImageIO.read(this.file[0]);
        }
        catch(IOException e) {
            System.out.println("Error while opening file : " + e);
        }

        this.visible = true;

        init();
    }

    public GameObject(String[] files, int width, int height) {
        this.file = new File[files.length];    
        this.image = new Image[files.length];
        
        for(int i = 0; i < files.length; i++) 
            this.file[i] = new File(files[i]);

        scale(width, height);
        this.visible = true;

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

    public void scale(int width, int height) {
        for(int i = 0; i < this.file.length; i++) {
            try {
                BufferedImage originalImage = ImageIO.read(this.file[i]);
                this.image[i] = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            } catch (IOException e) {
                System.out.println("error while resizing image at " + i + " : " + e);
            }
        }

        this.height = height;
        this.width = width;
    }

    public void mix(Image img) {
        BufferedImage res = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D dis = res.createGraphics();

        dis.drawImage(image[0], 0, 0, null);
        dis.drawImage(img, width/2-img.getWidth(null)/2, height/2-img.getHeight(null)/2, null);
        dis.dispose();

        image[0] = res.getScaledInstance(width, height, Image.SCALE_SMOOTH);
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

    public void nextImage() { renderIndex = (renderIndex > image.length-2)?0:renderIndex+1; }
    public void setImage(int i) { renderIndex = i; }
    public int getRenderIndex() { return renderIndex; }

    public File[] getFiles() { return this.file; }
 
    public Image getImage() { return this.image[renderIndex]; }
    public void setImage(Image img) { this.image[renderIndex] = img; }
    public void setImage(Image[] img, File[] files) { this.image = img.clone(); this.file = files.clone(); }
    public Image getImage(int i) { 
        if(i > image.length - 1) return null;
        return this.image[i]; 
    }
    public void setImage(Image img, int i) { 
        if(i < image.length - 1)
            this.image[i] = img; 
    }
}

interface Distance {
    public boolean isOn(Point p);
    public double distanceFrom(Point p);
}