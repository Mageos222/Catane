import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class GameObject {
    private Image[] image;
    private BufferedImage[] original;

    private int width;
    private int height;

    private int posX;
    private int posY;

    private int renderIndex = 0;

    private int zIndex = 0;
    private boolean visible;

    public GameObject(String file, int width, int height) {    
        this.original = new BufferedImage[1];    
        this.image = new Image[1];
        try {
            this.original[0] = ImageIO.read(new File(file));
        } catch (IOException e) {
            System.out.println("Error while opening file : " + e);
        }

        this.visible = true;
        setScale(width, height);
    }

    public GameObject(String file) {
        this.original = new BufferedImage[1];    
        this.image = new Image[1];

        try {
            original[0] = ImageIO.read(new File(file));
            this.width = original[0].getWidth();
            this.height = original[0].getHeight();

            this.image[0] = ImageIO.read(new File(file));
        }
        catch(IOException e) {
            System.out.println("Error while opening file : " + e);
        }

        this.visible = true;
    }

    public GameObject(String[] files, int width, int height) {
        this.original = new BufferedImage[files.length];    
        this.image = new Image[files.length];
        
        for(int i = 0; i < files.length; i++)
            try {
                this.original[i] = ImageIO.read(new File(files[i]));
            } catch (IOException e) {
                System.out.println("Error while opening file " + i + " : " + e);
            }

        setScale(width, height);
        this.visible = true;
    }

    public GameObject(GameObject copy) {
        this.image = copy.image;
        this.original = copy.original;

        this.width = copy.width;
        this.height = copy.height;

        this.posX = copy.posX;
        this.posY = copy.posY;

        this.zIndex = copy.zIndex;
        this.renderIndex = copy.renderIndex;
        this.visible = copy.visible;
    }

    public void setPosition(int x, int y) {
        this.posX = x;
        this.posY = y;
    }

    public void translate(int x, int y) {
        this.posX += x;
        this.posY += y;
    }

    public void setScale(int width, int height) {
        this.height = height;
        this.width = width;

        for(int i = 0; i < this.original.length; i++) {
            this.image[i] = this.original[i].getScaledInstance(width, height, Image.SCALE_SMOOTH);
        }
    }

    public void scale(int width, int height) {
        width = this.width + width;
        height = this.height + height;
        for(int i = 0; i < this.original.length; i++) {
            this.image[i] = this.original[i].getScaledInstance(width, height, Image.SCALE_SMOOTH);
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

        image[0] = (Image)res;
        original[0] = res;
    }

    public void setVisible(boolean v) { this.visible = v; }
    public boolean isVisible() { return this.visible; }

    public boolean isInteractable() { return false; }

    public void setZindex(int z) { zIndex = z; }
    public int getZindex() { return zIndex; }

    public int getPosX() { return this.posX - this.width/2; }
    public int getPosY() { return this.posY - this.height/2; }

    public int getCenterX() { return posX; }
    public int getCenterY() { return posY; }

    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public void nextImage() { renderIndex = (renderIndex > image.length-2)?0:renderIndex+1; }
    public void setImage(int i) { renderIndex = i; }
    public int getRenderIndex() { return renderIndex; }

    public BufferedImage[] getFiles() { return this.original; }
 
    public Image getImage() { return this.image[renderIndex]; }
    public void setImage(Image img) { this.image[renderIndex] = img; }
    public void setImage(Image[] img, BufferedImage[] originals) { this.image = img.clone(); this.original = originals.clone(); }
    public Image getImage(int i) { 
        if(i > image.length - 1) return null;
        return this.image[i]; 
    }
    public void setImage(Image img, int i) { 
        if(i < image.length - 1)
            this.image[i] = img; 
    }
}