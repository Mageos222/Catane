import java.util.List;

import GameEngine.BoxCollider;
import GameEngine.CircleCollider;
import GameEngine.GameObject;
import GameEngine.Renderer;
import GameEngine.UI;

import java.util.concurrent.TimeUnit;
import java.awt.*;

public class Home {
    private GameObject newButton;
    private GameObject continueButton;

    private GameObject profil1;
    private GameObject profil2;
    private GameObject profil3;
    private GameObject profil4;

    private GameObject playButton;
    private GameObject plusButton;
    private GameObject minusButton;

    private GameObject[] botButtons;

    public Home(Game game) {

        UI ui = new UI(1920, 1080);
        ui.setDimension(720, 480);
        ui.setBackground("Images/Buttons/WoodBack.jpg");

        newButton = new GameObject(new String[] {"Images/Buttons/button1-1.png", "Images/Buttons/button1-2.png"}, 300, 100);
        newButton.transform().setPosition(0, 100);

        newButton.addComponent(new BoxCollider(newButton));
        newButton.collider().setOnHoverEnterAction(() -> {
            newButton.renderer().setImage(1);
            ui.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        });
        newButton.collider().setOnHoverExitAction(() -> {
            newButton.renderer().setImage(0);
            ui.setCursor(Cursor.getDefaultCursor());
        });
        newButton.collider().setOnMouseClickedAction(this::changeScreen);

        continueButton = new GameObject(new String[] {"Images/Buttons/button2-1.png", "Images/Buttons/button2-2.png"}, 300, 100);
        continueButton.transform().setPosition(0, -100);

        profil1 = new GameObject("Images/Buttons/Profil1.png", 300, 600);
        profil1.transform().setPosition(-800, 50);
        profil1.renderer().setVisible(false);

        profil2 = new GameObject("Images/Buttons/Profil2.png", 300, 600);
        profil2.transform().setPosition(-400, 50);
        profil2.renderer().setVisible(false);

        profil3 = new GameObject("Images/Buttons/Profil3.png", 300, 600);
        profil3.transform().setPosition(0, 50);
        profil3.renderer().setVisible(false);

        profil4 = new GameObject("Images/Buttons/Profil4.png", 300, 600);
        profil4.transform().setPosition(400, 50);
        profil4.renderer().setVisible(false);

        playButton = new GameObject(new String[] { "Images/Buttons/Play1.png", "Images/Buttons/Play2.png" }, 300, 100);
        playButton.transform().setPosition(800,-400);
        playButton.renderer().setAlign(Renderer.Align.BOTTOM_RIGHT);
        playButton.renderer().setVisible(false);

        playButton.addComponent(new BoxCollider(playButton));
        playButton.collider().setOnHoverEnterAction(() -> {
            playButton.renderer().setImage(1);
            ui.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        });
        playButton.collider().setOnHoverExitAction(() -> {
            playButton.renderer().setImage(0);
            ui.setCursor(Cursor.getDefaultCursor());
        });
        playButton.collider().setOnMouseClickedAction(() -> startNewGame(ui, game));

        plusButton = new GameObject("Images/Buttons/button_add.png", 100, 100);
        plusButton.transform().setPosition(400, 50);
        plusButton.renderer().setVisible(false);

        plusButton.addComponent(new CircleCollider(plusButton));
        plusButton.collider().setOnHoverEnterAction(() -> plusButton.transform().scale(1.1));
        plusButton.collider().setOnHoverExitAction(() -> plusButton.transform().scale(0.9));
        plusButton.collider().setOnMouseClickedAction(this::addPlayer);

        minusButton = new GameObject("Images/Buttons/minusButton.png", 100, 100);
        minusButton.transform().setPosition(800, 50);
        minusButton.renderer().setVisible(false);

        minusButton.addComponent(new CircleCollider(minusButton));
        minusButton.collider().setOnHoverEnterAction(() -> minusButton.transform().scale(1.1));
        minusButton.collider().setOnHoverExitAction(() -> minusButton.transform().scale(0.9));
        minusButton.collider().setOnMouseClickedAction(this::removePlayer);

        botButtons = new GameObject[4];
        for(int i = 0; i < 4; i++) {
            botButtons[i] = new GameObject(new String[] {"Images/Buttons/PlayerButton1.png", "Images/Buttons/PlayerButton2png.png", 
                                                    "Images/Buttons/BotButton1.png", "Images/Buttons/BotButton2.png"}, 300, 100);
            botButtons[i].transform().setPosition(-800+400*i, -300);
            botButtons[i].renderer().setVisible(false);

            botButtons[i].addComponent(new BoxCollider(botButtons[i]));
            GameObject obj = botButtons[i];
            int j = i;
            botButtons[i].collider().setOnHoverEnterAction(() -> obj.renderer().nextImage());
            botButtons[i].collider().setOnHoverExitAction(() -> obj.renderer().precImage());
            botButtons[i].collider().setOnMouseClickedAction(() -> switchBot(j));
            botButtons[i].collider().setActiv(false);

            ui.add(botButtons[i]);
        }

        ui.add(newButton);
        ui.add(continueButton);

        ui.add(profil1);
        ui.add(profil2);
        ui.add(profil3);
        ui.add(profil4);

        ui.add(plusButton);
        ui.add(minusButton);

        ui.add(playButton);

        while(ui.isActive()) {
            List<UI.Event> events = ui.nextFrame();

            for(UI.Event event : events) {
                if(event == UI.Event.MOUSE_RIGHT_CLICK) {
                    //ui.setCursor(Cursor.getDefaultCursor());
                }
            }
            
            try {
                TimeUnit.MILLISECONDS.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void changeScreen() {
        this.newButton.renderer().setVisible(false);
        this.newButton.collider().setActiv(false);
        this.continueButton.renderer().setVisible(false);
        //this.continueButton.collider().setActiv(false);

        this.profil1.renderer().setVisible(true);
        this.profil2.renderer().setVisible(true);
        this.profil3.renderer().setVisible(true);

        this.plusButton.renderer().setVisible(true);

        this.playButton.renderer().setVisible(true);

        for(int i = 0; i < 3; i++) {
            botButtons[i].renderer().setVisible(true);
            botButtons[i].collider().setActiv(true);
        }
    }

    public void addPlayer() {
        plusButton.renderer().setVisible(false);
        plusButton.collider().setActiv(false);

        minusButton.renderer().setVisible(true);
        minusButton.collider().setActiv(true);

        profil4.renderer().setVisible(true);
        botButtons[3].renderer().setVisible(true);
        botButtons[3].collider().setActiv(true);
    }

    public void removePlayer() {
        plusButton.renderer().setVisible(true);
        plusButton.collider().setActiv(true);

        minusButton.renderer().setVisible(false);
        minusButton.collider().setActiv(false);

        profil4.renderer().setVisible(false);
        botButtons[3].renderer().setVisible(false);
        botButtons[3].collider().setActiv(false);
    }

    public void switchBot(int i) {
        botButtons[i].renderer().setImage((botButtons[i].renderer().getRenderIndex()+2)%4);
    }

    public void startNewGame(UI ui, Game game) {
        int width = ui.getWidth();
        int height = ui.getHeight();

        int posX = ui.getPosX();
        int posY = ui.getPosY();

        ui.close();
        game.init(posX, posY, width, height);
    }
}
