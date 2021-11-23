package GameEngine;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.FontMetrics;
import java.awt.Color;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class TextRenderer extends Renderer {
    
    private static final String FONT = "Arial Black";

    public TextRenderer(GameObject parent, String text) {
        super(parent);

        BufferedImage[] img = new BufferedImage[1];

        img[0] = new BufferedImage(text.length()*15, 50, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = img[0].createGraphics();

        graphics.setFont(new Font(FONT, Font.BOLD, 20));
        graphics.setPaint(Color.black);
        FontMetrics fm = graphics.getFontMetrics();
        int x = img[0].getWidth() - fm.stringWidth(text) - 5;
        int y = fm.getHeight();
        graphics.drawString(text, x, y);

        graphics.dispose();
        
        this.setImages(img);
    }

    public TextRenderer(GameObject parent, String[] texts) {
        super(parent);

        BufferedImage[] img = new BufferedImage[texts.length];

        for(int i = 0; i < texts.length; i++) {
            img[0] = new BufferedImage(10, texts[i].length()*5, BufferedImage.TYPE_BYTE_GRAY);
            Graphics2D graphics = img[0].createGraphics();
            graphics.setFont(new Font(FONT, Font.BOLD, 20));
            graphics.setPaint(Color.black);
            FontMetrics fm = graphics.getFontMetrics();
            int x = img[0].getWidth() - fm.stringWidth(texts[i]) - 5;
            int y = fm.getHeight();
            graphics.drawString(texts[i], x, y);
            graphics.drawString(texts[i], 10, 25);
            graphics.dispose();
        }
        this.setImages(img);
    }

    @Override
    public void setImages(String text) {
        BufferedImage[] img = new BufferedImage[1];

        img[0] = new BufferedImage(text.length()*15, 50, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = img[0].createGraphics();

        graphics.setFont(new Font(FONT, Font.BOLD, 20));
        graphics.setPaint(Color.black);
        FontMetrics fm = graphics.getFontMetrics();
        int x = img[0].getWidth() - fm.stringWidth(text) - 5;
        int y = fm.getHeight();
        graphics.drawString(text, x, y);

        graphics.dispose();
        
        this.setImages(img);
    }

    @Override
    public void addImage(String image) {
        BufferedImage[] actual = getImages();
        BufferedImage[] newArray = new BufferedImage[actual.length+1];
        
        for(int i = 0; i < actual.length; i++) newArray[i] = actual[i];

        try {
            newArray[actual.length] = ImageIO.read(new File(image));
            this.setImages(newArray);
        } catch (IOException e) {
            System.out.println("Error while opening file " + image + " : " + e);
        }
    }
}
