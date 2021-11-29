package GameEngine;

import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.awt.event.*; 

public class UI extends Canvas {
    public enum Event { MOUSE_LEFT_CLICK, MOUSE_RIGHT_CLICK } 

    private Frame f;

    private static final int WIDTH = 1920;
    private static final int HEIGHT = 1080;
    private final double RATIO;

    private int screenWidth;
    private int screenHeight;

    private transient List<GameObject> gameObjects;

    private boolean mouseClicked = false;
    private boolean rescale = false;
    private boolean isRescaled = true;

    private List<Event> events;

    private transient BufferedImage backgroundImg;

    private int nbFrame = 0;
    private int clikFrameTime = 0;

    public UI () {
        this.RATIO = (double)WIDTH/HEIGHT;

        this.screenWidth = WIDTH;
        this.screenHeight = HEIGHT;

        setBackground (Color.CYAN);    
        setSize(WIDTH, HEIGHT); 

        f = new Frame("Catane");   
        f.add(this);    
    
        f.setLayout(null);    
        f.setSize(WIDTH, HEIGHT);    
        f.setVisible(true); 
        
        gameObjects = new ArrayList<>();

        this.events = new ArrayList<>();
        
        f.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {                
                f.dispose();
            }
        });

        f.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent componentEvent) {
                rescale = true;
            }
        });
        
        addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {  
                if(e.getButton() == 1 && clikFrameTime < nbFrame) {
                    clikFrameTime = nbFrame + 3;
                    mouseClicked = true;
                    if(!events.contains(Event.MOUSE_LEFT_CLICK))
                        events.add(Event.MOUSE_LEFT_CLICK);
                }
                else if(e.getButton() == 3 && !events.contains(Event.MOUSE_RIGHT_CLICK)) 
                    events.add(Event.MOUSE_RIGHT_CLICK);
            }  
            public void mouseEntered(MouseEvent e) { /* not used */ }  
            public void mouseExited(MouseEvent e) { /* not used */ }  
            public void mousePressed(MouseEvent e) { 
                if(e.getButton() == 1 && clikFrameTime < nbFrame) {
                    clikFrameTime = nbFrame + 3;
                    mouseClicked = true;
                    if(!events.contains(Event.MOUSE_LEFT_CLICK))
                        events.add(Event.MOUSE_LEFT_CLICK);
                }
                else if(e.getButton() == 3 && !events.contains(Event.MOUSE_RIGHT_CLICK))
                    events.add(Event.MOUSE_RIGHT_CLICK);
            }  
            public void mouseReleased(MouseEvent e) { /* not used */ } 
        });  
    }
    
    public void setBackground(String file) {
        try {
            this.backgroundImg = ImageIO.read(new File(file));
        } catch (IOException e) {
            System.out.println("Error while opening file : " + e);
        }
    }

    public List<Event> nextFrame() {
        if(rescale) rescale();
        checkCollision();

        mouseClicked = false;
        nbFrame++;

        draw(getGraphics());

        List<Event> ret = new ArrayList<>(events);
        events.clear();
        
        return ret;
    }

    public void checkCollision() {
        Collider res = null;

        Point mousePos = getRelativPosition(MouseInfo.getPointerInfo().getLocation());

        for(GameObject object : gameObjects) {
            if(object.collider() == null || !object.collider().isActiv()) continue;
            Collider obj = object.collider();

            if(obj.isOn(mousePos) && (res == null || obj.distanceFrom(mousePos) < res.distanceFrom(mousePos))) {
                if(res != null && res.isHover()) {
                    res.onHoverExit();
                    res.setHover(false);
                }
                res = obj;
            }
            else if(obj.isHover()) {
                obj.onHoverExit();
                obj.setHover(false);
            }
        }

        if(res != null) {
            if(!res.isHover()) {
                res.onHoverEnter();
                res.setHover(true);
            }
            if(mouseClicked) res.onMouseClicked();
        } 
    }

    public void rescale() {
        setSize(f.getWidth(), f.getHeight()); 

        screenWidth = f.getWidth();
        screenHeight = f.getHeight();

        rescale = false;
        isRescaled = true;
    }
 
    public Point getRelativPosition(Point p) {
        Point r = p;
        r.setLocation((p.getX() - f.getX()), (p.getY() - f.getY()));
        return r;
    }

    public static Vector2 getCenterPosition(int x, int y) {
        return new Vector2((int)(x+WIDTH/2f), (int)(y+HEIGHT/2f));
    }
    
    public boolean isActive() {
        return f.isActive();
    }

    @Override
    public void setVisible(boolean b) {
        f.setVisible(b);
        super.setVisible(b);
    }

    public void add(GameObject gameObject) {
        this.gameObjects.add(gameObject);
    }

    public void draw(Graphics g) {
        if(isRescaled) rescaleObjects();

        BufferedImage frame = new BufferedImage(screenWidth,screenHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D display = frame.createGraphics();

        if(backgroundImg != null) 
            display.drawImage(backgroundImg, 0, 0, screenWidth, screenHeight, this);

        List<GameObject> renderQueu = new ArrayList<>(gameObjects);       
        int index = 0;
        while(!renderQueu.isEmpty()) {
            int i = 0;
            while(i < renderQueu.size()) {
                GameObject gameObject = renderQueu.get(i);
                if(!gameObject.renderer().isVisible()) {
                    renderQueu.remove(i);
                    continue;
                }
                if(renderQueu.get(i).renderer().getZindex() == index) {
                    display.drawImage(gameObject.renderer().getImage(), gameObject.transform().getRelativCenterPosition().getX(), 
                                                             gameObject.transform().getRelativCenterPosition().getY(), 
                                                             gameObject.transform().getRelativSize().getX(), 
                                                             gameObject.transform().getRelativSize().getY(),this);

                    renderQueu.remove(i);
                }
                else i++;

                if(gameObject.renderer().isPlaying() && gameObject.renderer().getNextFrame() <= nbFrame) 
                    gameObject.renderer().playFrame(nbFrame);
            }
            index++;
        }

        if(isActive())
            g.drawImage(frame, 0, 0, screenWidth, screenHeight, this);
        display.dispose();
    }

    private void rescaleObjects() {
        double ratio = (double)screenWidth/screenHeight;
        int scaledWidth = screenWidth;
        int scaledHeight = screenHeight;

        double r = 1;
        if(ratio > RATIO) {
            scaledWidth = (int)(WIDTH*(double)screenHeight/HEIGHT);
            r = (double)screenHeight/HEIGHT;
        }
        else if(ratio < RATIO) {
            scaledHeight = (int)(HEIGHT*(double)screenWidth/WIDTH);
            r = (double)screenWidth/WIDTH;
        }

        int sWidth = screenWidth-scaledWidth;
        int sHeight = screenHeight-scaledHeight;

        int[] shiftX = { 0, (int)(sWidth/2f), sWidth};
        int[] shiftY = { 0, (int)(sHeight/2f), sHeight};

        for(GameObject gameObject : gameObjects) {
            Vector2 pos = getCenterPosition(gameObject.transform().getPosition().getX(), gameObject.transform().getPosition().getY());

            gameObject.transform().setRelativSize(r);
            gameObject.transform().setRelativPosition((int)(pos.getX()*r+shiftX[gameObject.renderer().getAlign()%3]), 
                                                      (int)(pos.getY()*r+shiftY[gameObject.renderer().getAlign()/3]));
            gameObject.transform().setRelativ(shiftX, shiftY, r);
        }

        isRescaled = false;
    }

    public void windowClosing (WindowEvent e) {  
        f.dispose();    
    }   
    public void close() {
        f.dispose();
    }

    @Override
    public void setCursor(Cursor cursor) { f.setCursor(cursor); }
    public void setDimension(int width, int height) { f.setSize(width, height);}
    
    @Override
    public void setLocation(int x, int y) {
        f.setLocation(x, y);
    }

    @Override
    public int getWidth() { return this.screenWidth; }
    @Override
    public int getHeight() { return this.screenHeight; }
    public int getPosX() { return f.getX(); }
    public int getPosY() { return f.getY(); }

    public List<GameObject> getObjects() { return gameObjects; }
    public BufferedImage getBackgroundImage() { return backgroundImg; }

    public boolean isRescaled() { return isRescaled; }
    public void setIsRescale(boolean v) { isRescaled = v; }

    public int getNbFrame() { return nbFrame; }
}
