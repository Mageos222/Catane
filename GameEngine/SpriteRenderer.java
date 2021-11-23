package GameEngine;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class SpriteRenderer extends Renderer {
    
    public SpriteRenderer(GameObject parent, String image) {
        super(parent);

        BufferedImage[] img = new BufferedImage[1];

        try {
            img[0] = ImageIO.read(new File(image));
            this.setImages(img);
        } catch (IOException e) {
            System.out.println("Error while opening file " + image + " : " + e);
        }
    }

    public SpriteRenderer(GameObject parent, String[] images) {
        super(parent);

        BufferedImage[] img = new BufferedImage[images.length];

        for(int i = 0; i < images.length; i++) {
            try {
                img[i] = ImageIO.read(new File(images[i]));
                this.setImages(img);
            } catch (IOException e) {
                System.out.println("Error while opening file " + images[i] + " : "  + e);
            }
        }
    }

    @Override
    public void setImages(String image) {
        BufferedImage[] img = new BufferedImage[1];

        try {
            img[0] = ImageIO.read(new File(image));
            this.setImages(img);
        } catch (IOException e) {
            System.out.println("Error while opening file " + image + " : " + e);
        }
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
