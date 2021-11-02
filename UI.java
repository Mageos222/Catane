import java.util.ArrayList;
import java.util.List;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.*; 

public class UI extends Canvas {
    
    private static Frame f;

    private static int WIDTH = 720;
    private static int HEIGHT = 480;

    List<GameObject> gameObjects;
    private GameObject newObject;

    private boolean mouseClicked = false;

    public UI () {
        setBackground (Color.CYAN);    
        setSize(WIDTH, HEIGHT); 

        f = new Frame("Catane");   
        f.add(this);    
    
        f.setLayout(null);    
        f.setSize(WIDTH, HEIGHT);    
        f.setVisible(true); 
        
        gameObjects = new ArrayList<>();

        GameObject background = new GameObject("Images/Water.png", WIDTH, HEIGHT);
        background.setPosition(0, 0);
        add(background);
        
        // Close event
        f.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent we) {
                f.dispose();
            }
        });
        addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {  
                if(e.getButton() == 1) 
                    mouseClicked = true;
                else if(e.getButton() == 3) {
                    gameObjects.remove(newObject);
                    newObject = null;
                }
            }  
            public void mouseEntered(MouseEvent e) { }  
            public void mouseExited(MouseEvent e) { }  
            public void mousePressed(MouseEvent e) { }  
            public void mouseReleased(MouseEvent e) { } 
        });  

        System.out.println("Ready");
        
    }

    public void analyse() {
        Point mousePos = getRelativPosition(MouseInfo.getPointerInfo().getLocation());

        if(newObject != null) newObject.setPosition((int)mousePos.getX(), (int)mousePos.getY());

        GameObject res = null;

        for(GameObject obj : gameObjects) {
            if(!obj.isInteractable()) continue;

            if(obj.isOn(mousePos)) {
                if(res == null) res = obj;
                else {
                    if(obj.distanceFrom(mousePos) < res.distanceFrom(mousePos)) {
                        if(res.isHover()) res.onHoverExit(this);
                        res = obj;
                    }
                    else if(obj.isHover()) obj.onHoverExit(this);
                }
            }
            if(obj.isHover()) obj.onHoverExit(this);
        }

        if(res != null) {
            if(!res.isHover()) res.onHoverEnter(this);
            if(mouseClicked) res.onMouseClicked(this);
        } 

        repaint();
        mouseClicked = false;

        //System.out.println(mousePos.toString());
    }

    public Point getRelativPosition(Point p) {
        Point r = p;
        r.setLocation(p.getX() - f.getX(), p.getY() - f.getY());

        return r;
    }
    
    public boolean isActive() {
        return f.isActive();
    }

    public void add(GameObject gameObject) {
        gameObject.translate(WIDTH/2, HEIGHT/2);
        this.gameObjects.add(gameObject);
    }

    @Override
    public void update(Graphics g) {
        //g.clearRect(0, 0, 720, 480);        

        BufferedImage frame = new BufferedImage(WIDTH,HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics2D dis = frame.createGraphics();

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
                    dis.drawImage(gameObject.getImage(), gameObject.getPosX(), gameObject.getPosY(), this);
                    renderQueu.remove(i);
                }
                else i++;
            }
            index++;
        }

        g.drawImage(frame, 0, 0, WIDTH, HEIGHT, this);

        //System.out.println("Updated");
    }

    public void windowClosing (WindowEvent e) {  
        f.dispose();    
    }   

    public void setNewObject(GameObject object) {
        Point mousePos = getRelativPosition(MouseInfo.getPointerInfo().getLocation());
        if(newObject != null) gameObjects.remove(newObject);

        this.newObject = object;
        object.setPosition((int)mousePos.getX(), (int)mousePos.getY());
        object.setInteractable(false);
        object.setZindex(3);
        add(object);
    }

    public void addNewObject(GameObject object) {
        object.setHover(false);
        newObject = null;
    }

    public void focus(GameObject object) {
        object.scale(object.getWidth() + 20, object.getHeight() + 20);
        object.setHover(true);
        object.setZindex(2);
    }

    public void unfocus(GameObject object) {
        object.scale(object.getWidth() - 20, object.getHeight() - 20);
        object.setHover(false);
        object.setZindex(1);
    } 

    public void snap(GameObject object) {
        if(object.getImage() != null) return;

        if(newObject == null) return;
        object.setHover(true);

        object.setImage(newObject.getImage());
        object.setVisible(true);
        newObject.setVisible(false);
    }

    public void exit(GameObject object) {
        object.setVisible(false);
        object.setHover(false);

        object.setImage(null);

        if(newObject == null) return;
        newObject.setVisible(true);
    }
}
