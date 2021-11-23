package GameEngine;

public class FPSCounter extends Thread {
    private UI ui;
    private int frames;
    private int fps = 0;

    private boolean print = false;

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
            if(print)
                System.out.println(fps + " fps");
            frames = newFrames;
        }
    }

    public int getFPS() { return fps; }

    public void show() { this.print = true; }
    public void hide() { this.print = false; }
}
