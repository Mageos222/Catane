import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class GameObject {
    static enum Align { TopLeft(0), Top(1), TopRight(2), CenterLeft(3), Center(4), 
        CenterRight(5), BottomLeft(6), Bottom(7), BottomRight(8);
    
        private final int value;
        private Align(int value) { this.value = value; }
        public int getValue() {
            return value;
        }
    };

    private BufferedImage[] original;

    private int width;
    private int height;

    private int relativWidth;
    private int relativHeight;

    private int posX;
    private int posY;

    private int relativPosX;
    private int relativPosY;

    private double ratio = 1;

    private int renderIndex = 0;
    private Align align;

    private int zIndex = 0;
    private boolean visible;

    public GameObject(String file, int width, int height) {    
        this.original = new BufferedImage[1];    
        try {
            this.original[0] = ImageIO.read(new File(file));
        } catch (IOException e) {
            System.out.println("Error while opening file : " + e);
        }

        this.visible = true;
        this.align = Align.Center;
        setScale(width, height);
    }

    public GameObject(String file) {
        this.original = new BufferedImage[1];    

        try {
            original[0] = ImageIO.read(new File(file));
            this.width = original[0].getWidth();
            this.height = original[0].getHeight();
        }
        catch(IOException e) {
            System.out.println("Error while opening file : " + e);
        }

        this.align = Align.Center;
        this.visible = true;
    }

    public GameObject(String[] files, int width, int height) {
        this.original = new BufferedImage[files.length];            
        for(int i = 0; i < files.length; i++)
            try {
                this.original[i] = ImageIO.read(new File(files[i]));
            } catch (IOException e) {
                System.out.println("Error while opening file " + i + " : " + e);
            }

        setScale(width, height);
        this.align = Align.Center;
        this.visible = true;
    }

    public GameObject(GameObject copy) {
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
        this.relativPosX += (posX-x)*ratio;
        this.relativPosY += (posY-y)*ratio;

        this.posX = x;
        this.posY = y;
    }

    public void setRelativPosition(int x, int y) {
        this.relativPosX = x;
        this.relativPosY = y;
    }

    public void translate(int x, int y) {
        this.posX += x;
        this.posY += y;
        
        this.relativPosX += x * ratio;
        this.relativPosY += y * ratio;
    }

    public void setScale(int width, int height) {
        this.height = height;
        this.width = width;

        this.relativWidth = (int)(this.width * ratio) + 1;
        this.relativHeight = (int)(this.height * ratio) + 1;
    }

    public void scale(int width, int height) {
        int w = this.width + width;
        int h = this.height + height;

        this.height = h;
        this.width = w;

        this.relativWidth = (int)(this.width * ratio) + 1;
        this.relativHeight = (int)(this.height * ratio) + 1;
    }

    public void scale(double ratio) {
        int w = (int)(this.width * ratio) + 1;
        int h = (int)(this.height * ratio) + 1;

        this.relativWidth = w;
        this.relativHeight = h;
        this.ratio = ratio;
    }

    public void mix(Image img) {
        BufferedImage res = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D dis = res.createGraphics();

        dis.drawImage(original[0].getScaledInstance(width, height, Image.SCALE_SMOOTH), 0, 0, null);
        dis.drawImage(img, (int)(width/2f-img.getWidth(null)/2f), (int)(height/2f-img.getHeight(null)/2f), null);
        dis.dispose();

        original[0] = res;
    }

    public void setVisible(boolean v) { this.visible = v; }
    public boolean isVisible() { return this.visible; }

    public boolean isInteractable() { return false; }

    public void setZindex(int z) { zIndex = z; }
    public int getZindex() { return zIndex; }

    public int getPosX() { return (int)(this.posX - this.width/2f); }
    public int getPosY() { return (int)(this.posY - this.height/2f); }

    public int getRelativCenterX() { return (int)(this.relativPosX + this.relativWidth/2f); }
    public int getRelativCenterY() { return (int)(this.relativPosY + this.relativHeight/2f); }

    public int getRelativPosX() { return this.relativPosX; }
    public int getRelativPosY() { return this.relativPosY; }

    public int getCenterX() { return posX; }
    public int getCenterY() { return posY; }

    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public int getRelativWidth() { return this.relativWidth; }
    public int getRelativHeight() { return this.relativHeight; }

    public void nextImage() { renderIndex = (renderIndex > original.length-2)?0:renderIndex+1; }
    public void setImage(int i) { renderIndex = i; }
    public int getRenderIndex() { return renderIndex; }

    public BufferedImage[] getFiles() { return this.original; }
 
    public void setImage(BufferedImage[] originals) { this.original = originals.clone(); }
    public BufferedImage getImage() { return original[renderIndex]; }

    public void setAlign(Align align) { this.align = align; }
    public int getAlign() { return align.getValue(); }
}