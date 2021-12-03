package GameEngine;

import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;

public class SpriteRenderer extends Renderer {
    
    public SpriteRenderer(GameObject parent, String image) {
        super(parent);

        BufferedImage[] img = new BufferedImage[1];

        try {
            img[0] = RessourceManager.getImage(image);
            this.setImages(img);
        } catch (FileNotFoundException e) {
            printError(e, image);
        }
    }

    public SpriteRenderer(GameObject parent, String[] images) {
        super(parent);

        BufferedImage[] img = new BufferedImage[images.length];

        for(int i = 0; i < images.length; i++) {
            try {
                img[i] = RessourceManager.getImage(images[i]);
                this.setImages(img);
            } catch (FileNotFoundException e) {
                printError(e, images[i]);
            }
        }
    }

    @Override
    public void setImages(String image) {
        BufferedImage[] img = new BufferedImage[1];

        try {
            img[0] = RessourceManager.getImage(image);
            this.setImages(img);
        } catch (FileNotFoundException e) {
            printError(e, image);
        }
    }

    @Override
    public void addImage(String image) {
        BufferedImage[] actual = getImages();
        BufferedImage[] newArray = new BufferedImage[actual.length+1];
        
        for(int i = 0; i < actual.length; i++) newArray[i] = actual[i];

        try {
            newArray[actual.length] = RessourceManager.getImage(image);
            this.setImages(newArray);
        } catch (FileNotFoundException e) {
            printError(e, image);
        }
    }

    private void printError(Exception e, String image) {
        System.out.println("File " + image + " not found");
        e.printStackTrace();
    }
}
