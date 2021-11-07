package GameEngine;

public class FPSCounter extends Thread {
    private UI ui;
    private int frames;

    public FPSCounter(UI ui) {
        this.ui = ui;
    }

    @Override
    public void run() {
        while(true) {
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }

            int newFrames = ui.getNbFrame();
            System.out.println(newFrames - frames + " fps");
            frames = newFrames;
        }
    }
}
