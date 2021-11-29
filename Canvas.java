import GameEngine.*;

public class Canvas {

    private Game game;
    private Controller controller;
    private UI ui;

    private GameObject[] profils;
    private GameObject[][] ressourceText;

    private GameObject voleur;

    public Canvas(Game g, Controller c, UI u) {
        this.game = g;
        this.controller = c;
        this.ui = u;
    }

    public void drawCanvas() {
        this.profils = new GameObject[game.getPlayer().length];
        this.ressourceText = new GameObject[game.getPlayer().length][];

        this.profils[0] = new GameObject("Images/Profils/playerProfil1.png", 200, 200);
        this.profils[0].transform().setPosition(-825, 375);
        this.profils[0].renderer().setAlign(Renderer.Align.TOP_LEFT);
        ui.add(this.profils[0]);
        controller.addRessourceText(0, -675, 375, Renderer.Align.TOP_LEFT);

        this.profils[1] = new GameObject("Images/Profils/playerProfil2.png", 200, 200);
        this.profils[1].transform().setPosition(825, 375);
        this.profils[1].renderer().setAlign(Renderer.Align.TOP_RIGHT);
        ui.add(this.profils[1]);
        controller.addRessourceText(1, 675, 375, Renderer.Align.TOP_RIGHT);

        this.profils[2] = new GameObject("Images/Profils/playerProfil3.png", 200, 200);
        this.profils[2].transform().setPosition(-825, -375);
        this.profils[2].renderer().setAlign(Renderer.Align.BOTTOM_LEFT);
        ui.add(this.profils[2]);
        controller.addRessourceText(2, -675, -375, Renderer.Align.BOTTOM_LEFT);

        if(game.getPlayer().length == 4) {
            this.profils[3] = new GameObject("Images/Profils/playerProfil4.png", 200, 200);
            this.profils[3].transform().setPosition(825, -375);
            this.profils[3].renderer().setAlign(Renderer.Align.BOTTOM_RIGHT);
            ui.add(this.profils[3]);
            controller.addRessourceText(3, 675, -375, Renderer.Align.BOTTOM_RIGHT);
        }
        this.profils[0].transform().scale(1.2);

        GameObject build = new GameObject("Images/GamePage/Hammer.png", 75, 75);
        build.transform().setPosition(0, -450);
        build.renderer().setZindex(2);
        build.renderer().setAlign(Renderer.Align.BOTTOM);
        build.addComponent(new BoxCollider(build));

        build.collider().setOnHoverEnterAction(() -> controller.focus(build, 20));
        build.collider().setOnHoverExitAction(() -> controller.unfocus(build, 20));
        build.collider().setOnMouseClickedAction(controller::setNewObject);

        ui.add(build);

        GameObject next = new GameObject("Images/GamePage/nextButton.png", 75, 75);
        next.transform().setPosition(150, -450);
        next.renderer().setZindex(2);
        next.renderer().setAlign(Renderer.Align.BOTTOM);
        next.addComponent(new BoxCollider(next));

        next.collider().setOnHoverEnterAction(() -> controller.focus(next, 20));
        next.collider().setOnHoverExitAction(() -> controller.unfocus(next, 20));
        next.collider().setOnMouseClickedAction(controller::nextTurn);

        ui.add(next);

        GameObject pause = new GameObject("Images/GamePage/Pause.png", 75, 75);
        pause.transform().setPosition(0, 450);
        pause.renderer().setZindex(2);
        pause.renderer().setAlign(Renderer.Align.TOP);
        pause.addComponent(new CircleCollider(pause));

        pause.collider().setOnHoverEnterAction(() -> controller.focus(pause, 20));
        pause.collider().setOnHoverExitAction(() -> controller.unfocus(pause, 20));
        pause.collider().setOnMouseClickedAction(() -> {
            Home home = new Home(new Game(game.getSize()), ui.getWidth(), ui.getHeight(), ui.getPosX(), ui.getPosY());
            home.start();
            ui.close();
            game.interrupt();
        });

        ui.add(pause);

        game.getDice1().transform().setPosition(-150, 0);
        game.getDice1().renderer().setZindex(9);
        game.getDice2().transform().setPosition(150, 0);
        game.getDice2().renderer().setZindex(9);

        game.getDice1Small().transform().setPosition(-750, 0);
        game.getDice1Small().renderer().setZindex(9);
        game.getDice1Small().renderer().setAlign(Renderer.Align.CENTER_LEFT);
        game.getDice2Small().transform().setPosition(-650, 0);
        game.getDice2Small().renderer().setZindex(9);
        game.getDice2Small().renderer().setAlign(Renderer.Align.CENTER_LEFT);

        game.getDice1().renderer().setVisible(false);
        game.getDice2().renderer().setVisible(false);
        game.getDice1Small().renderer().setVisible(false);
        game.getDice2Small().renderer().setVisible(false);

        ui.add(game.getDice1());
        ui.add(game.getDice2());
        ui.add(game.getDice1Small());
        ui.add(game.getDice2Small());

        GameObject costCard = new GameObject("Images/GamePage/costCard.png", 240, 310);
        costCard.transform().setPosition(750, 0);
        costCard.renderer().setAlign(Renderer.Align.CENTER_RIGHT);
        costCard.renderer().setZindex(9);
        costCard.addComponent(new BoxCollider(costCard));
        costCard.collider().setOnHoverEnterAction(() -> controller.focus(costCard, 100));
        costCard.collider().setOnHoverExitAction(() -> controller.unfocus(costCard, 100));
        costCard.collider().setOnMouseClickedAction(() -> { });
        ui.add(costCard);

        ui.setBackground("Images/GamePage/Water.png");
    }

    public void addEmptyVillage(int posX, int posY, int x, int y) {
        String[] houses = {"Images/Colonies/villageRed.png", "Images/Colonies/villageBlue.png", 
            "Images/Colonies/villageGreen.png", "Images/Colonies/villageYellow.png"};
        GameObject empty = new GameObject(houses, 70, 70);
        empty.transform().setPosition(posX, posY);
        empty.renderer().setZindex(4);
        empty.renderer().setVisible(false);

        empty.addComponent(new CircleCollider(empty));
        empty.collider().setOnHoverEnterAction(() -> controller.snap(empty));
        empty.collider().setOnHoverExitAction(() -> controller.unsnap(empty));
        empty.collider().setOnMouseClickedAction(() -> controller.build(empty, x, y, 0, 0, true, true));
        ui.add(empty);
    }

    public void addEmptyRoad(int posX, int posY, int x1, int y1, int x2, int y2, int i) {
        String[][] imgFiles = { {"Images/Colonies/RoadRightRed.png", "Images/Colonies/RoadRightBlue.png", 
            "Images/Colonies/RoadRightGreen.png", "Images/Colonies/RoadRightYellow.png"}, 
        {"Images/Colonies/RoadLeftRed.png", "Images/Colonies/RoadLeftBlue.png", 
            "Images/Colonies/RoadLeftGreen.png", "Images/Colonies/RoadLeftYellow.png" }, 
        {"Images/Colonies/RoadRed.png", "Images/Colonies/RoadBlue.png", 
            "Images/Colonies/RoadGreen.png", "Images/Colonies/RoadYellow.png" }};

        GameObject emptyRoad = new GameObject(imgFiles[i], 90, 90);
        emptyRoad.transform().setPosition(posX, posY);
        emptyRoad.renderer().setVisible(false);
        emptyRoad.renderer().setZindex(3);

        emptyRoad.addComponent(new CircleCollider(emptyRoad));

        emptyRoad.collider().setOnHoverEnterAction(() -> controller.snap(emptyRoad));
        emptyRoad.collider().setOnHoverExitAction(() -> controller.unsnap(emptyRoad));
        emptyRoad.collider().setOnMouseClickedAction(() -> controller.build(emptyRoad, x1, y1, x2, y2, false, false));
        ui.add(emptyRoad);
    }

    public void moveVoleur(int x, int y) {
        if(this.voleur == null) {
            String[] sprites = new String[8];
            for(int i = 0; i < 8; i++) sprites[i] = "Images/GamePage/Ninja/ninja"+i+".png";
            voleur = new GameObject(sprites, 100, 100);
            voleur.renderer().setZindex(5);

            voleur.renderer().setAnimSpeed(2);
            voleur.renderer().startAnim();
            ui.add(voleur);
        }
        voleur.transform().setPosition(x, y);
    }

    public GameObject[] getProfils() { return this.profils; }
    public GameObject[][] getRessourceText() { return this.ressourceText; }
}
