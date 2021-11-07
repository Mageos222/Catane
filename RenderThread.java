import java.util.ArrayList;
import java.util.List;

import java.awt.*;
import java.awt.image.BufferedImage;

public class RenderThread extends Thread {
    private BufferedImage frame;

    private UI ui;

    private List<GameObject> renderQueu;
    private boolean isRescale;

    private double r;
    
    private int[] shiftX;
    private int[] shiftY;

    private int width;
    private int height;

    private boolean run = false;

    public RenderThread(UI ui) {
        this.ui = ui;
        this.renderQueu = new ArrayList<>();
    }

    public void update(boolean isRescale, double r, int[] shiftX, int[] shiftY, int width, int height) {
        this.isRescale = isRescale;
        this.r = r;
        this.shiftX = shiftX;
        this.shiftY = shiftY;
        this.width = width;
        this.height = height;
    }

    private void execute() {
        frame = new BufferedImage(width,height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D display = frame.createGraphics();

        while(run) {
            if(renderQueu.isEmpty()) {
                try {
                    sleep(50);
                    continue;
                } catch (InterruptedException e) {
                    break;
                }
            }

            GameObject gameObject = renderQueu.get(0);

            if(isRescale) {
                Point pos = ui.getCenterPosition(gameObject.getPosX(), gameObject.getPosY());

                gameObject.scale(r);
                gameObject.setRelativPosition((int)(pos.getX()*r+shiftX[gameObject.getAlign()%3]), 
                                            (int)(pos.getY()*r+shiftY[gameObject.getAlign()/3]));
            }
            if(gameObject.isVisible()) {
                Image img = gameObject.getImage().getScaledInstance(gameObject.getRelativWidth(), 
                                gameObject.getRelativHeight(), Image.SCALE_SMOOTH);
                display.drawImage(img, gameObject.getRelativPosX(), gameObject.getRelativPosY(), null);
            }

            renderQueu.remove(0);
        }

        display.dispose();
    }

    public void startComputing() {
        this.run = true;
        execute();
    }
    public void stopComputing() { this.run = false; }

    public void add(GameObject obj) { this.renderQueu.add(obj); }
    public boolean end() { return this.renderQueu.isEmpty(); }
    public boolean isClosed() { return !this.run; }
    public BufferedImage getImage() { return frame; }
}
