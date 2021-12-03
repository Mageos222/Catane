package GameEngine;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;

import javax.imageio.ImageIO;

public class RessourceManager {
    
    private static Dictionary<String, BufferedImage> images;

    private RessourceManager() { }

    public static void load(String path) {
        images = new Hashtable<>();
        File root = new File(path);
        
        if(!root.isDirectory()) {
            System.out.println("The specified path is not a directory");
            return;
        }

        loadImages(root, path);
        
    }

    private static void loadImages(File dir, String fullPath) {
        for(File file : dir.listFiles()) {
            if(file.isDirectory()) loadImages(file, fullPath + "/" + file.getName());
            else {
                String[] ext = file.getName().split("\\.");
                if(ext.length > 0 && (ext[ext.length-1].equals("png") || ext[ext.length-1].equals("jpg"))) {
                    try {
                        BufferedImage img = ImageIO.read(file);
                        images.put(fullPath+"/"+file.getName(), img);
                    } catch (IOException e) {
                        System.out.println("Error while opening file " + file.getName() + " : "  + e);
                    }
                }
            }
        }
    }

    public static BufferedImage getImage (String path) throws FileNotFoundException {
        BufferedImage res = images.get(path);

        if(res == null) throw new FileNotFoundException();

        return res;
    }
}
