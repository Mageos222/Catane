import java.util.concurrent.TimeUnit;

import GameEngine.GameObject;
import GameEngine.UI;

public class LoadingPage extends Thread {
    UI ui;

    public LoadingPage(int posX, int posY, int width, int height) {
        ui = new UI(1920, 1080);
        ui.setDimension(width, height);
        ui.setLocation(posX, posY);
        ui.setBackground("Images/Buttons/WoodBack.jpg");

        String[] images = new String[12];
        for(int i = 0; i < 12; i++) images[i] = "Images/LoadingPage/anim" + (i+1) + ".png";

        GameObject loading = new GameObject(images, 1920, 1080);
        loading.renderer().setAnimSpeed(10);
        loading.renderer().startAnim();
        ui.add(loading);
    }

    @Override
    public void run() {
        while(ui.isActive()) {
            ui.nextFrame();
            
            try {
                sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        System.out.println("loading closed");
    }

    public boolean isActiv() { return ui.isActive(); }

    public void close() {
        ui.close();
    }

    public int getX() { return ui.getPosX(); }
    public int getY() { return ui.getPosY(); }
    public int getWidth() { return ui.getWidth(); }
    public int getHeight() { return ui.getHeight(); }
}