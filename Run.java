import java.util.concurrent.TimeUnit;

import GameEngine.*;

public class Run {

    private static float timeSleep = 25;
    public static void main(String[] args) {
        Transform value = new Transform(720, 480);

        RessourceManager manager = new RessourceManager();
        manager.load("Images/LoadingPage", false);

        try {
            MusicPlayer music = new MusicPlayer("Music/Music.wav");
            music.loop();
        } catch(Exception e) {
            System.out.println("Error while playing music");
        }

        LoadingPage load = new LoadingPage(0, 0, 720, 480, value);
        load.start();

        manager.load("Images");
        waitManager();
        load.close();

        Home home = new Home(value);
        Player[] players = home.run();

        UI ui = new UI();
        ui.setVisible(false);

        Game game = new Game(3);
        Canvas canvas = new Canvas(ui);

        Controller controller = new Controller(game, canvas);
        canvas.setController(controller);

        LoadingPage loading = new LoadingPage(value.getPosition().getX(), -value.getPosition().getY(), 
                                                value.getSize().getX(), value.getSize().getY(), value);
        loading.start();
        game.init(players, canvas);
        game.run(controller);
        ui.setLocation(loading.getX(), loading.getY());
        ui.setDimension(loading.getWidth(), loading.getHeight());

        loading.close();
        loading = null;

        ui.setVisible(true);

        FPSCounter fps = new FPSCounter(ui);
        fps.start();
        //fps.show();

        boolean closed = false;

        while(!closed || (loading != null && loading.isActiv()) || ui.getNbFrame() < 10) {
            try {
                ui.nextFrame();
                game.update();
            } catch(Exception e) {
                e.printStackTrace();
                System.exit(1);
            }

            int count = fps.getFPS();
            if(count > 64 && count > 0) timeSleep += (count-32)*0.005f;
            else if(timeSleep > 0 && count > 0) timeSleep -= (32-count)*0.005f;

            //System.out.println(fps.getFPS() + " -> " + timeSleep);

            try {
                TimeUnit.MILLISECONDS.sleep((int)timeSleep);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        fps.interrupt();
        System.out.println("Game closed");
        System.exit(0);
    }

    private static void waitManager() {
        while(true) {
            if(!RessourceManager.isLoading())
                return;
            try {
                TimeUnit.MILLISECONDS.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
    }
}
