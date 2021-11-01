import javax.imageio.ImageIO;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.awt.event.*; 

public class UI extends Canvas {
    
    private static String root = "./Images";
    private static Frame f;

    public static int size = 100;

    public UI () {
        setBackground (Color.CYAN);    
        setSize(720, 480); 

        f = new Frame("Catane");   
        f.add(this);    
    
        f.setLayout(null);    
        f.setSize(720, 480);    
        f.setVisible(true);   
        
        // Close event
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                f.dispose();
            }
        });
        
    }

    public Point getRelativPosition(Point p) {
        Point r = p;
        r.x = (int)(p.getX() - f.getX() - f.WIDTH/2);
        r.y = (int)(p.getY() - f.getY() - f.HEIGHT/2);

        return r;
    }
    
    public boolean isActive() {
        return f.isActive();
    }

    public static void main(String[] args) {
        UI test = new UI();
        Boolean change = false;

        while(test.isActive()) {
            Point mouse = test.getRelativPosition(MouseInfo.getPointerInfo().getLocation());

            if(mouse.getX() > 50 && mouse.getX() < 150 && mouse.getY() > 50 && mouse.getY() < 150) {
                test.size = 120;
                if(!change) {
                    test.repaint();
                    change = true;
                }   
            }
            else {
                test.size = 100;
                if(change) {
                    test.repaint();
                    change = false;
                }   
            }
            //System.out.println(mouse.toString());
        }
    }

    @Override
    public void paint(Graphics g)    
    {    
        BufferedImage originalImage;

        try {
            originalImage = ImageIO.read(new File(root, "Field.png"));
            Image field = originalImage.getScaledInstance(size, size, Image.SCALE_SMOOTH);
            g.drawImage(field, 50, 50, this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }   

    @Override
    public void update(Graphics g) {
        g.clearRect(0, 0, 720, 480);
        BufferedImage originalImage;

        try {
            originalImage = ImageIO.read(new File(root, "Field.png"));
            Image field = originalImage.getScaledInstance(size, size, Image.SCALE_SMOOTH);
            g.drawImage(field, 50, 50, this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void windowClosing (WindowEvent e) {  
        f.dispose();    
    }   
}
