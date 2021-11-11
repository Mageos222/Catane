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

        GameObject loading = new GameObject(images, width, height);
        loading.renderer().setAnimSpeed(10);
        loading.renderer().startAnim();
        ui.add(loading);
    }

    @Override
    public void run() {
        while(ui.isActive()) {
            ui.nextFrame();
            
            try {
                TimeUnit.MILLISECONDS.sleep(50);
            } catch (InterruptedException e) {
                break;
            }
        }
    }

    public boolean isActiv() { return ui.isActive(); }

    public void close() {
        ui.close();
    }
}
