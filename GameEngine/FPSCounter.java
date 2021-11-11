package GameEngine;

public class FPSCounter extends Thread {
    private UI ui;
    private int frames;
    private int fps = 0;

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
            fps = newFrames - frames;
            System.out.println(fps + " fps");
            frames = newFrames;
        }
    }

    public int getFPS() { return fps; }
}
