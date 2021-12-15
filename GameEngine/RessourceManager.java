package GameEngine;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;

import javax.imageio.ImageIO;

public class RessourceManager extends Thread {
    
    private static Dictionary<String, BufferedImage> images;

    private File rootDir;
    private String rootPath;

    private static boolean isLoading;

    public void load(String path) {
        load(path, true);
    }

    public void load(String path, boolean async) {
        resetImages();
        File root = new File(path);
        
        if(!root.isDirectory()) {
            System.out.println("The specified path is not a directory");
            return;
        }

        this.rootDir = root;
        this.rootPath = path;

        if(async) {
            setLoading(true);
            start();
        }
        else loadImages(root, path);
    }

    @Override
    public void run() {
        loadImages(rootDir, rootPath);
        setLoading(false);
        interrupt();
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

    private static void resetImages() { images = new Hashtable<>(); }
    private static void setLoading(boolean v) { isLoading = v; }
    public static boolean isLoading() { return isLoading; }
}
