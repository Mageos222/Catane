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

    private final int WIDTH;
    private final int HEIGHT;
    private final double RATIO;

    private int width;
    private int height;

    private transient List<GameObject> gameObjects;

    private boolean mouseClicked = false;
    private boolean rescale = false;
    private boolean isRescale = true;

    private List<Event> events;

    private BufferedImage background;

    private GameObject test;

    private FPSCounter fpsCounter;
    private int nbFrame = 0;

    public UI (int width, int height) {
        this.WIDTH = width;
        this.HEIGHT = height;
        this.RATIO = (double)WIDTH/HEIGHT;

        this.width = width;
        this.height = height;

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
                stopThreads();
                
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
                if(e.getButton() == 1) {
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
                if(e.getButton() == 1) {
                    mouseClicked = true;
                    if(!events.contains(Event.MOUSE_LEFT_CLICK))
                        events.add(Event.MOUSE_LEFT_CLICK);
                }
                else if(e.getButton() == 3 && !events.contains(Event.MOUSE_RIGHT_CLICK))
                    events.add(Event.MOUSE_RIGHT_CLICK);
            }  
            public void mouseReleased(MouseEvent e) { /* not used */ } 
        });  

        fpsCounter = new FPSCounter(this);
        fpsCounter.start();

        /*test = new GameObject("Images/villageBlue.png", 100, 100);
        test.setZindex(10);
        add(test);*/

        System.out.println("Ready");
    }
    
    public void setBackground(String file) {
        try {
            this.background = ImageIO.read(new File(file));
        } catch (IOException e) {
            System.out.println("Error while opening file : " + e);
        }
    }

    public List<Event> nextFrame() {
        if(rescale) rescale();
        checkCollision();

        mouseClicked = false;
        nbFrame++;

        //Point mousePos = getRelativPosition(MouseInfo.getPointerInfo().getLocation());
        //test.setPosition((int)mousePos.getX(), (int)mousePos.getY());

        draw(getGraphics());

        List<Event> ret = new ArrayList<>(events);
        events.clear();
        
        return ret;
    }

    public void checkCollision() {
        Interactable res = null;

        Point mousePos = getRelativPosition(MouseInfo.getPointerInfo().getLocation());
        //System.out.println(mousePos.toString());

        for(GameObject object : gameObjects) {
            if(!object.isInteractable()) continue;
            Interactable obj = (Interactable)object;

            if(obj.isOn(mousePos) && (res == null || obj.distanceFrom(mousePos) < res.distanceFrom(mousePos))) 
                res = obj;
            if(obj.isHover()) obj.onHoverExit();
        }

        if(res != null) {
            if(!res.isHover()) res.onHoverEnter();
            if(mouseClicked) res.onMouseClicked();
        } 
    }

    public void rescale() {
        setSize(f.getWidth(), f.getHeight()); 

        width = f.getWidth();
        height = f.getHeight();

        rescale = false;
        isRescale = true;
    }
 
    public Point getRelativPosition(Point p) {
        Point r = p;
        r.setLocation((p.getX() - f.getX()), (p.getY() - f.getY()));
        return r;
    }

    public Point getCenterPosition(int x, int y) {
        Point p = new Point();
        p.setLocation(x+WIDTH/2f, y+HEIGHT/2f);
        return p;
    }
    
    public boolean isActive() {
        return f.isActive();
    }

    public void add(GameObject gameObject) {
        this.gameObjects.add(gameObject);
    }

    public void draw(Graphics g) {
        if(isRescale) rescaleObjects();

        BufferedImage frame = new BufferedImage(width,height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D display = frame.createGraphics();

        if(background != null) 
            display.drawImage(background, 0, 0, width, height, this);

        List<GameObject> renderQueu = new ArrayList<>(gameObjects);       
        int index = 0;
        while(!renderQueu.isEmpty()) {
            int i = 0;
            while(i < renderQueu.size()) {
                GameObject gameObject = renderQueu.get(i);
                if(!gameObject.isVisible()) {
                    renderQueu.remove(i);
                    continue;
                }
                if(renderQueu.get(i).getZindex() == index) {
                    display.drawImage(gameObject.getImage(), gameObject.getRelativPosX(), gameObject.getRelativPosY(), 
                                            gameObject.getRelativWidth(), gameObject.getRelativHeight(),this);

                    renderQueu.remove(i);
                }
                else i++;
            }
            index++;
        }

        g.drawImage(frame, 0, 0, width, height, this);
        display.dispose();
    }

    private void rescaleObjects() {
        double ratio = (double)width/height;
        int scaledWidth = width;
        int scaledHeight = height;

        double r = 1;
        if(ratio > RATIO) {
            scaledWidth = (int)(WIDTH*(double)height/HEIGHT);
            r = (double)height/HEIGHT;
        }
        else if(ratio < RATIO) {
            scaledHeight = (int)(HEIGHT*(double)width/WIDTH);
            r = (double)width/WIDTH;
        }

        int sWidth = width-scaledWidth;
        int sHeight = height-scaledHeight;

        int[] shiftX = { 0, (int)(sWidth/2f), sWidth};
        int[] shiftY = { 0, (int)(sHeight/2f), sHeight};

        for(GameObject gameObject : gameObjects) {
            Point pos = getCenterPosition(gameObject.getPosX(), gameObject.getPosY());

            gameObject.scale(r);
            gameObject.setRelativPosition((int)(pos.getX()*r+shiftX[gameObject.getAlign()%3]), 
                                          (int)(pos.getY()*r+shiftY[gameObject.getAlign()/3]));
        }

        isRescale = false;
    }

    public void windowClosing (WindowEvent e) {  
        f.dispose();    
    }   

    public void setCursor(int cursor) { f.setCursor(cursor);}
    public void setDimension(int width, int height) { f.setSize(width, height);}

    public int getWidth() { return this.width; }
    public int getHeight() { return this.height; }

    public List<GameObject> getObjects() { return gameObjects; }
    public BufferedImage getBackgroundImage() { return background; }

    public boolean isRescale() { return isRescale; }
    public void setIsRescale(boolean v) { isRescale = v; }

    private void stopThreads() { fpsCounter.interrupt(); }
    public int getNbFrame() { return nbFrame; }
}
