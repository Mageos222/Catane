package GameEngine;

import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class Renderer implements Component {
    public enum Align { TOP_LEFT(0), TOP(1), TOP_RIGHT(2), CENTER_LEFT(3), CENTER(4), 
        CENTER_RIGHT(5), BOTTOM_LEFT(6), BOTTOM(7), BOTTOM_RIGHT(8);
    
        private final int value;
        private Align(int value) { this.value = value; }
        public int getValue() {
            return value;
        }
    }

    GameObject parent;

    private BufferedImage[] images;

    private int renderIndex = 0;
    private Align align;

    private int zIndex = 0;
    private boolean visible;

    private boolean play = false;
    private int animSpeed = 1;
    private int nextFrame;

    protected Renderer(GameObject parent) {
        this.parent = parent;

        this.visible = true;
        this.align = Align.CENTER;
    }

    public void mix(Image img) {
        BufferedImage res = new BufferedImage(parent.transform().getSize().getX(), parent.transform().getSize().getY(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D dis = res.createGraphics();

        dis.drawImage(images[0].getScaledInstance(parent.transform().getSize().getX(), parent.transform().getSize().getY(), Image.SCALE_SMOOTH), 0, 0, null);
        dis.drawImage(img, (int)(parent.transform().getSize().getX()/2f-img.getWidth(null)/2f), (int)(parent.transform().getSize().getY()/2f-img.getHeight(null)/2f), null);
        dis.dispose();

        images[0] = res;
    }

    public void setVisible(boolean v) { 
        this.visible = v; 
        for(GameObject child : parent.getChildren()) 
            child.renderer().setVisible(v); 
    }
    public boolean isVisible() { return this.visible; }

    public void setZindex(int z) { zIndex = z; }
    public int getZindex() { return zIndex; }

    public void nextImage() { renderIndex = (renderIndex + 1) % images.length; }
    public void precImage() { renderIndex = (renderIndex - 1) % images.length; }
    public void setImage(int i) { renderIndex = i; }
    public int getRenderIndex() { return renderIndex; }

    public BufferedImage[] getImages() { return this.images; }
 
    public void setImages(BufferedImage[] originals) { this.images = originals.clone(); }
    public abstract void setImages(String originals);
    public BufferedImage getImage() { return images[renderIndex]; }

    public void setAlign(Align align) { 
        this.align = align; 
        parent.transform().setAlign(align.getValue()); 
    }
    public int getAlign() { return align.getValue(); }

    public int getWidth() { return images[0].getWidth(); }
    public int getHeight() { return images[0].getHeight(); }

    public void startAnim() { this.play = true; }
    public boolean isPlaying() { return this.play; }
    public void setAnimSpeed(int speed) { this.animSpeed = speed; } 
    public int getNextFrame() { return this.nextFrame; }
    public void playFrame(int frame) {
        nextImage();
        nextFrame = frame + animSpeed;
    }

    public abstract void addImage(String image);

    public int getId() { return 0; }
}
