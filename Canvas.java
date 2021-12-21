import java.util.LinkedList;
import java.util.List;

import GameEngine.*;

public class Canvas {

    private Game game;
    private Controller controller;
    private UI ui;

    private GameObject[] profils;
    private GameObject[][] ressourceText;

    private GameObject voleur;

    private GameObject sign;
    private GameObject[] signTexts;
    private GameObject signInfo;

    private GameObject ressourceChoice;
    private GameObject playerChoice;

    private List<GameObject> temp;

    public Canvas(Game g, Controller c, UI u) {
        this.game = g;
        this.controller = c;
        this.ui = u;

        this.signTexts = new GameObject[5];
        this.temp = new LinkedList<>();
    }

    public void drawCanvas() {
        this.profils = new GameObject[game.getPlayer().length];
        this.ressourceText = new GameObject[game.getPlayer().length][];

        //#region Profils
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
        //#endregion

        //#region Buttons
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

        GameObject deal = new GameObject("Images/GamePage/deal.png", 75, 75);
        deal.transform().setPosition(-150, -450);
        deal.renderer().setZindex(2);
        deal.renderer().setAlign(Renderer.Align.BOTTOM);
        deal.addComponent(new BoxCollider(deal));

        deal.collider().setOnHoverEnterAction(() -> controller.focus(deal, 20));
        deal.collider().setOnHoverExitAction(() -> controller.unfocus(deal, 20));
        deal.collider().setOnMouseClickedAction(controller::openDeal);

        ui.add(deal);

        GameObject pause = new GameObject("Images/GamePage/Pause.png", 75, 75);
        pause.transform().setPosition(0, 450);
        pause.renderer().setZindex(15);
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
        //#endregion

        //#region Dices
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
        //#endregion

        //#region Sign
        sign = new GameObject("Images/GamePage/sign.png", 1200, 1200);
        sign.transform().setPosition(0, 150);
        sign.addComponent(new BoxCollider(sign));
        sign.renderer().setZindex(10);
        ui.add(sign); 

        GameObject closeButton = new GameObject("Images/Buttons/cancelButton.png", 75, 75);
        closeButton.transform().setPosition(-300, -225);
        closeButton.renderer().setZindex(11);
        closeButton.addComponent(new CircleCollider(closeButton));
        closeButton.collider().setOnHoverEnterAction(() -> controller.focus(closeButton, 20));
        closeButton.collider().setOnHoverExitAction(() -> controller.unfocus(closeButton, 20));
        closeButton.collider().setOnMouseClickedAction(controller::closeDeal);
        sign.addChild(closeButton);
        ui.add(closeButton);

        //#endregion

        //#region Ressource Choice
        ressourceChoice = new GameObject(0, 0);
        ressourceChoice.addComponent(new BoxCollider(ressourceChoice));
        sign.addChild(ressourceChoice);

        String[] files = { "Images/Card/wheatCard.png", "Images/Card/woodCard.png", "Images/Card/sheepCard.png",
                            "Images/Card/rockCard.png", "Images/Card/clayCard.png"};

        for(int i = 0; i < 5; i++) {
            GameObject text = new GameObject(100, 100);
            text.addComponent(new TextRenderer(text, "0"));
            text.transform().setPosition(-300+i*150, 75);
            text.renderer().setZindex(11);
            ressourceChoice.addChild(text);
            ui.add(text);
            signTexts[i] = text;

            GameObject obj = new GameObject(files[i], 150, 200);
            obj.transform().setPosition(-300+i*150, -50);
            obj.renderer().setZindex(11);
            obj.addComponent(new BoxCollider(obj));
            int val = i;
            obj.collider().setOnMouseClickedAction(() -> controller.addValueToDealProp(val));
            ressourceChoice.addChild(obj);
            ui.add(obj);
        }

        signInfo = new GameObject(600, 100);
        signInfo.addComponent(new TextRenderer(signInfo, "Welcome"));
        signInfo.transform().setPosition(0, 150);
        signInfo.renderer().setZindex(11);
        ressourceChoice.addChild(signInfo);
        ui.add(signInfo);

        GameObject resetButton = new GameObject("Images/GamePage/nextButton.png", 75, 75);
        resetButton.transform().setPosition(0, -225);
        resetButton.renderer().setZindex(11);
        resetButton.addComponent(new CircleCollider(resetButton));
        resetButton.collider().setOnHoverEnterAction(() -> controller.focus(resetButton, 20));
        resetButton.collider().setOnHoverExitAction(() -> controller.unfocus(resetButton, 20));
        resetButton.collider().setOnMouseClickedAction(controller::resetDeal);
        ressourceChoice.addChild(resetButton);
        ui.add(resetButton);

        GameObject validButton = new GameObject("Images/Buttons/validateButton.png", 75, 75);
        validButton.transform().setPosition(300, -225);
        validButton.renderer().setZindex(11);
        validButton.addComponent(new CircleCollider(validButton));
        validButton.collider().setOnHoverEnterAction(() -> controller.focus(validButton, 20));
        validButton.collider().setOnHoverExitAction(() -> controller.unfocus(validButton, 20));
        validButton.collider().setOnMouseClickedAction(controller::validDeal);
        ressourceChoice.addChild(validButton);
        ui.add(validButton);

        //#endregion

        sign.renderer().setVisible(false);
        sign.collider().setActiv(false);

        GameObject costCard = new GameObject("Images/Card/costCard.png", 240, 310);
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

    public void addEmptyVillage(int posX, int posY, int x, int y, int size) {
        String[] houses = {"Images/Colonies/villageRed.png", "Images/Colonies/villageBlue.png", 
            "Images/Colonies/villageGreen.png", "Images/Colonies/villageYellow.png"};
        GameObject empty = new GameObject(houses, 220/size, 220/size);
        empty.transform().setPosition(posX, posY);
        empty.renderer().setZindex(4);
        empty.renderer().setVisible(false);

        empty.addComponent(new CircleCollider(empty));
        empty.collider().setOnHoverEnterAction(() -> controller.snap(empty));
        empty.collider().setOnHoverExitAction(() -> controller.unsnap(empty));
        empty.collider().setOnMouseClickedAction(() -> controller.build(empty, x, y, 0, 0, true, true));
        ui.add(empty);
    }

    public void addEmptyRoad(int posX, int posY, int x1, int y1, int x2, int y2, int i, int size) {
        String[][] imgFiles = { {"Images/Colonies/RoadRightRed.png", "Images/Colonies/RoadRightBlue.png", 
            "Images/Colonies/RoadRightGreen.png", "Images/Colonies/RoadRightYellow.png"}, 
        {"Images/Colonies/RoadLeftRed.png", "Images/Colonies/RoadLeftBlue.png", 
            "Images/Colonies/RoadLeftGreen.png", "Images/Colonies/RoadLeftYellow.png" }, 
        {"Images/Colonies/RoadRed.png", "Images/Colonies/RoadBlue.png", 
            "Images/Colonies/RoadGreen.png", "Images/Colonies/RoadYellow.png" }};

        GameObject emptyRoad = new GameObject(imgFiles[i], 280/size, 280/size);
        emptyRoad.transform().setPosition(posX, posY);
        emptyRoad.renderer().setVisible(false);
        emptyRoad.renderer().setZindex(3);

        emptyRoad.addComponent(new CircleCollider(emptyRoad));

        emptyRoad.collider().setOnHoverEnterAction(() -> controller.snap(emptyRoad));
        emptyRoad.collider().setOnHoverExitAction(() -> controller.unsnap(emptyRoad));
        emptyRoad.collider().setOnMouseClickedAction(() -> controller.build(emptyRoad, x1, y1, x2, y2, false, false));
        ui.add(emptyRoad);
    }

    public void addPort(int x, int y, int size, int orientation, int type) {
        String[]  boats = { "Images/GamePage/boatSheep.png", "Images/GamePage/boatWheat.png", "Images/GamePage/boatWood.png",
                            "Images/GamePage/boatRock.png", "Images/GamePage/boatClay.png", "Images/GamePage/boatNeutral.png"};

        GameObject port = new GameObject(boats[type], 240/size, 240/size);
        port.transform().setPosition(x, y);
        port.renderer().setZindex(8);

        port.addComponent(new CircleCollider(port));
        port.collider().setOnHoverEnterAction(() -> controller.focus(port, 50));
        port.collider().setOnHoverExitAction(() -> controller.unfocus(port, 50));

        ui.add(port);

        String[] files = { "Images/GamePage/bridgeBL.png", "Images/GamePage/bridgeBR.png", "Images/GamePage/bridgeL.png", 
                        "Images/GamePage/bridgeR.png", "Images/GamePage/bridgeTL.png", "Images/GamePage/bridgeTR.png"};

        GameObject bridge = new GameObject(files[orientation], 540/size, 540/size);
        bridge.transform().setPosition(x, y);
        bridge.renderer().setZindex(8);

        ui.add(bridge);
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

    public void showPlayersChoice(int[] players) {
        for(int i = 0; i < players.length; i++) {
            GameObject player = new GameObject("Images/Profils/Profil"+(players[i]+1)+".png", 100, 200);
            player.transform().setPosition(-100*(players.length-1)+i*200, -25);
            player.renderer().setZindex(11);

            player.addComponent(new BoxCollider(player));
            int val = i;
            player.collider().setOnHoverEnterAction(() -> controller.selectPlayer(val, player));
            player.collider().setOnHoverExitAction(() -> controller.unselectPlayer(val, player));
            player.collider().setOnMouseClickedAction(() -> controller.confirmPlayer(val));

            ui.add(player);

            temp.add(player);
        }
    }

    public void showCard(Ressource[] dealVal) {
        String[] file = { "Images/Card/wheatCard.png", "Images/Card/woodCard.png", 
                            "Images/Card/sheepCard.png", "Images/Card/rockCard.png", "Images/Card/clayCard.png"};

        int counter = 0;
        for(int i = 0; i < 5; i++) {
            for(int j = 0; j < dealVal[1].getRessource(i); j++) {
                GameObject obj = new GameObject(file[i], 75, 110);
                obj.transform().setPosition(-300+counter*50, 150);
                obj.renderer().setZindex(11+counter);
                ui.add(obj);
                temp.add(obj);
                counter++;
            }
        }
        for(int i = 0; i < 5; i++) {
            for(int j = 0; j < dealVal[0].getRessource(i); j++) {
                GameObject obj = new GameObject(file[i], 75, 110);
                obj.transform().setPosition(50+counter*50, 150);
                obj.renderer().setZindex(11+counter);
                ui.add(obj);
                temp.add(obj);          
                counter++;
            }
        }
    }

    public void emptyTemp() {
        for(GameObject gameObject : temp)
            ui.remove(gameObject);
        temp.clear();
    }

    public GameObject[] getProfils() { return this.profils; }
    public GameObject[][] getRessourceText() { return this.ressourceText; }

    public GameObject getSign() { return this.sign; }
    public GameObject[] getSignTexts() { return this.signTexts; }
    public GameObject getSignInfo() { return this.signInfo; }

    public GameObject getRessourceChoice() { return this.ressourceChoice; }
    public GameObject getPlayerChoice() { return this.playerChoice; }
}
