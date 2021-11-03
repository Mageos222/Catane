import java.util.ArrayList;
import java.util.List;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.awt.event.*; 

public class UI extends Canvas {
    
    private static Frame f;

    private static int WIDTH = 720;
    private static int HEIGHT = 480;

    List<GameObject> gameObjects;
    private boolean addObject;

    private static GameObject tower;

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
                    addObject = false;
                    f.setCursor(Cursor.DEFAULT_CURSOR);
                }
            }  
            public void mouseEntered(MouseEvent e) { }  
            public void mouseExited(MouseEvent e) { }  
            public void mousePressed(MouseEvent e) { }  
            public void mouseReleased(MouseEvent e) { } 
        });  

        String[] towers = {"Images/townRed.png","Images/townBlue.png", "Images/townGreen.png", "Images/townYellow.png"};
        tower = new GameObject(towers, 40, 40);

        System.out.println("Ready");
        
    }

    public void analyse() {
        Point mousePos = getRelativPosition(MouseInfo.getPointerInfo().getLocation());

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
        addObject = true;
        f.setCursor(Cursor.HAND_CURSOR);
    }

    public void addNewObject(GameObject object, boolean isUpdatable) {
        if(!object.isHover()) return;
        object.setHover(false);
        addObject = false;

        if(isUpdatable) {
            Image[] newSprite = {object.getImage(), tower.getImage(object.getRenderIndex())};
            File[] files = {object.getFiles()[object.getRenderIndex()], tower.getFiles()[object.getRenderIndex()]};
            object.setImage(newSprite, files);

            object.setOnHoverEnterAction(i -> i.snapUpdate(object));
            object.setOnHoverExitAction(i -> i.unsnapUpdate(object));
            object.setOnMouseClickedAction(i -> i.addNewObject(object, false));
        }
        else {
            object.setOnHoverEnterAction(i -> { });
            object.setOnHoverExitAction(i -> { });
            object.setOnMouseClickedAction(i -> { });
        }

        f.setCursor(Cursor.DEFAULT_CURSOR);
    }

    public void focus(GameObject object, int size) {
        object.scale(object.getWidth() + size, object.getHeight() + size);
        object.setHover(true);
        object.setZindex(object.getZindex()+5);
    }

    public void unfocus(GameObject object, int size) {
        object.scale(object.getWidth() - size, object.getHeight() - size);
        object.setHover(false);
        object.setZindex(object.getZindex()-5);
    } 

    public void snap(GameObject object) {
        if(!addObject) return;
        object.setHover(true);

        object.setVisible(true);
    }

    public void unsnap(GameObject object) {
        object.setVisible(false);
        object.setHover(false);
    }

    public void snapUpdate(GameObject object) {
        if(!addObject) {
            focus(object, 10);
            return; 
        }
        object.setHover(true);
        object.scale(50, 50);

        object.nextImage();
    }

    public void unsnapUpdate(GameObject object) {
        if(!addObject) unfocus(object, 10);
        object.setHover(false);
        object.scale(40, 40);

        object.setImage(0);
    }
}
