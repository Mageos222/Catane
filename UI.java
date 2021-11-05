import java.util.ArrayList;
import java.util.List;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.*; 

public class UI extends Canvas {
    public enum Event { MOUSE_LEFT_CLICK, MOUSE_RIGHT_CLICK } 

    private Frame f;

    private static final int WIDTH = 720;
    private static final int HEIGHT = 480;

    private int width;
    private int height;

    private transient List<GameObject> gameObjects;

    private boolean mouseClicked = false;
    private boolean rescale = false;

    private List<Event> events;

    public UI () {
        width = WIDTH;
        height = HEIGHT;

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

        System.out.println("Ready");
        
    }
    
    public void setBackground(String file) {
        GameObject background = new GameObject(file, WIDTH, HEIGHT);
        background.setPosition(0, 0);
        add(background);
    }

    public List<Event> nextFrame() {
        Point mousePos = getRelativPosition(MouseInfo.getPointerInfo().getLocation());
        mousePos.setLocation(mousePos.getX(), mousePos.getY());

        if(rescale) rescale();
        checkCollision(mousePos);

        repaint();
        
        mouseClicked = false;

        List<Event> ret = new ArrayList<>(events);
        events.clear();

        return ret;
    }

    public void checkCollision(Point mousePos) {
        Interactable res = null;

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
    }
 
    public Point getRelativPosition(Point p) {
        Point r = p;
        r.setLocation((p.getX() - f.getX() - width/2f)*((float)WIDTH/width), (p.getY() - f.getY() - height/2f)*((float)HEIGHT/height));

        return r;
    }

    public Point getCenterPosition(int x, int y) {
        Point p = new Point();
        p.setLocation(x+WIDTH/2, y+HEIGHT/2);
        return p;
    }
    
    public boolean isActive() {
        return f.isActive();
    }

    public void add(GameObject gameObject) {
        this.gameObjects.add(gameObject);
    }

    @Override
    public void update(Graphics g) {
        BufferedImage frame = new BufferedImage(WIDTH,HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics2D dis = frame.createGraphics();
        dis.setBackground(Color.blue);

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
                if(gameObject.getZindex() == index) {
                    Point pos = getCenterPosition(gameObject.getPosX(), gameObject.getPosY());
                    dis.drawImage(gameObject.getImage(), (int)pos.getX(), (int)pos.getY(), this);
                    renderQueu.remove(i);
                }
                else i++;
            }
            index++;
        }

        Image res = frame.getScaledInstance(f.getWidth(), f.getHeight(), Image.SCALE_SMOOTH);

        g.drawImage(res, 0, 0, width, height, this);
    }

    public void windowClosing (WindowEvent e) {  
        f.dispose();    
    }   

    public void setCursor(int cursor) { f.setCursor(cursor);}
}
