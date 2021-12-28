import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import GameEngine.*;

public class Canvas {

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

    private GameObject dice1;
    private GameObject dice2;
    private GameObject dice1Small;
    private GameObject dice2Small;

    private List<GameObject> temp;

    public Canvas(UI u) {
        this.ui = u;

        this.signTexts = new GameObject[5];
        this.temp = new LinkedList<>();
    }

    public void setController(Controller c) { this.controller = c; }

    public void drawCanvas(int nbPlayer, int size) {
        this.profils = new GameObject[nbPlayer];
        this.ressourceText = new GameObject[nbPlayer][];

        //#region Profils
        this.profils[0] = new GameObject("Images/Profils/playerProfil1.png", 200, 200);
        this.profils[0].transform().setPosition(-825, 375);
        this.profils[0].renderer().setAlign(Renderer.Align.TOP_LEFT);
        ui.add(this.profils[0]);
        addRessourceText(0, -675, 375, Renderer.Align.TOP_LEFT);

        this.profils[1] = new GameObject("Images/Profils/playerProfil2.png", 200, 200);
        this.profils[1].transform().setPosition(825, 375);
        this.profils[1].renderer().setAlign(Renderer.Align.TOP_RIGHT);
        ui.add(this.profils[1]);
        addRessourceText(1, 675, 375, Renderer.Align.TOP_RIGHT);

        this.profils[2] = new GameObject("Images/Profils/playerProfil3.png", 200, 200);
        this.profils[2].transform().setPosition(-825, -375);
        this.profils[2].renderer().setAlign(Renderer.Align.BOTTOM_LEFT);
        ui.add(this.profils[2]);
        addRessourceText(2, -675, -375, Renderer.Align.BOTTOM_LEFT);

        if(nbPlayer == 4) {
            this.profils[3] = new GameObject("Images/Profils/playerProfil4.png", 200, 200);
            this.profils[3].transform().setPosition(825, -375);
            this.profils[3].renderer().setAlign(Renderer.Align.BOTTOM_RIGHT);
            ui.add(this.profils[3]);
            addRessourceText(3, 675, -375, Renderer.Align.BOTTOM_RIGHT);
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
            ui.close();
        });

        ui.add(pause);
        //#endregion

        //#region Dices
        String[] dice = new String[6];
        for(int i = 1; i <= 6; i++) dice[i-1] = "Images/GamePage/dice"+i+".png";

        dice1 = new GameObject(dice, 200, 200);
        dice2 = new GameObject(dice, 200, 200);
        dice1Small = new GameObject(dice, 75, 75);
        dice2Small = new GameObject(dice, 75, 75);

        dice1.transform().setPosition(-150, 0);
        dice1.renderer().setZindex(9);
        dice2.transform().setPosition(150, 0);
        dice2.renderer().setZindex(9);

        dice1Small.transform().setPosition(-750, 0);
        dice1Small.renderer().setZindex(9);
        dice1Small.renderer().setAlign(Renderer.Align.CENTER_LEFT);
        dice2Small.transform().setPosition(-650, 0);
        dice2Small.renderer().setZindex(9);
        dice2Small.renderer().setAlign(Renderer.Align.CENTER_LEFT);

        dice1.renderer().setVisible(false);
        dice2.renderer().setVisible(false);
        dice1Small.renderer().setVisible(false);
        dice2Small.renderer().setVisible(false);

        ui.add(dice1);
        ui.add(dice2);
        ui.add(dice1Small);
        ui.add(dice2Small);
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
        ui.addMouseListener((MouseEvent e) -> {
            if(e.getButton() == MouseEvent.BUTTON3) {
                controller.setAddObject(false);
                setCursor(0);
            }
        });
    }

    public GameObject addEmptyVillage(int posX, int posY, int x, int y, int size) {
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

        return empty;
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
        String[] boats = { "Images/GamePage/boatWood.png", "Images/GamePage/boatWheat.png", "Images/GamePage/boatClay.png",
                        "Images/GamePage/boatRock.png", "Images/GamePage/boatSheep.png", "Images/GamePage/boatNeutral.png"};

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
        bridge.renderer().setZindex(1);

        ui.add(bridge);
    }

    public void addTile(int x, int y, Tiles tile, int type, int size, Colony[] adja) {
        String[] files = {
            "Images/GamePage/wheat.png",
            "Images/GamePage/lumber.png",
            "Images/GamePage/sheep.png",
            "Images/GamePage/ore.png",
            "Images/GamePage/brick.png",
            "Images/GamePage/Desert.png"
        };

        int tileSize = 520/size;
        int xOffset = (int)(0.5f*tileSize);
        int yOffset = (int)(0.74f*tileSize);

        Vector2 pos = Vector2.multiply(new Vector2(2*(x+Math.max(0, y-size+1))-(size-1+y), size-y-1), new Vector2(xOffset, yOffset));

        GameObject obj = new GameObject(files[type], tileSize, tileSize);       // creation d'un objet
        obj.renderer().setZindex(1);
        obj.transform().setPosition(pos);
        if(tile.getValue() != 7) obj.renderer().mix(tile.getImage());
        
        obj.addComponent(new CircleCollider(obj));
        obj.collider().setOnHoverEnterAction(() -> controller.moveRobber(pos.getX(), pos.getY()));
        obj.collider().setOnHoverExitAction(() -> { });
        obj.collider().setOnMouseClickedAction(() -> controller.putRobber(adja));

        ui.add(obj);

        if(type == 5) { 
            moveRobber(pos.getX(), -pos.getY());
            controller.putRobber(adja);
        }
    }

    public void moveRobber(int x, int y) {
        if(this.voleur == null) {
            String[] sprites = new String[8];
            for(int i = 0; i < 8; i++) sprites[i] = "Images/GamePage/Ninja/ninja"+i+".png";
            voleur = new GameObject(sprites, 100, 100);
            voleur.renderer().setZindex(5);

            voleur.renderer().setAnimSpeed(5);
            voleur.renderer().startAnim();
            ui.add(voleur);
        }
        voleur.transform().setPosition(x, y);
    }

    public void showPlayersChoice(int[] players) {
        String[] files = { "Images/GamePage/boatWheat.png", "Images/GamePage/boatWood.png", "Images/GamePage/boatSheep.png",
                        "Images/GamePage/boatRock.png", "Images/GamePage/boatClay.png", "Images/GamePage/boatNeutral.png"};

        for(int i = 0; i < players.length; i++) {
            GameObject player;
            if(players[i] <= 3) player = new GameObject("Images/Profils/Profil"+(players[i]+1)+".png", 100, 200);
            else player  = new GameObject(files[players[i]-4], 200, 200);
            System.out.println(players[i]);

            player.transform().setPosition(-100*(players.length-1)+i*200, -25);
            player.renderer().setZindex(11);

            player.addComponent(new BoxCollider(player));
            int val = players[i];
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

    public void addRessourceText(int index, int x, int y, Renderer.Align align) {
        String[] file = new String[] { "wheatIco.png", "wood.png", "sheepIco.png", "rock.png", "clay.png" };

        ressourceText[index] = new GameObject[5];
        for(int i = 0; i < 5; i++) {
            ressourceText[index][i] = new GameObject(50, 75);
            ressourceText[index][i].transform().setPosition(x-(x/Math.abs(x))*i*60, y-30);
            ressourceText[index][i].addComponent(new TextRenderer(ressourceText[index][i], "0"));
            ressourceText[index][i].renderer().setZindex(1);
            ressourceText[index][i].renderer().setAlign(align);
            ui.add(ressourceText[index][i]);

            GameObject icon = new GameObject("Images/GamePage/" + file[i], 50, 50);
            icon.transform().setPosition(x-(x/Math.abs(x))*i*60, y+30);
            icon.renderer().setZindex(1);
            icon.renderer().setAlign(align);
            ui.add(icon);
        }
    }

    public void playDiceAnim(int diceAnim, int dice1Value, int dice2Value) {
        Random rnd = new Random();
        if(diceAnim > 0) {
            if(diceAnim < 15) {
                dice1.renderer().setImage(rnd.nextInt(6));
                dice2.renderer().setImage(rnd.nextInt(6));
            }
            else if(diceAnim == 15) {
                dice1.renderer().setImage(dice1Value-1);
                dice2.renderer().setImage(dice2Value-1);
            }
            else if(diceAnim > 30) {
                diceAnim = -1;
                dice1.renderer().setVisible(false);
                dice2.renderer().setVisible(false);

                dice1Small.renderer().setImage(dice1Value-1);
                dice2Small.renderer().setImage(dice2Value-1);
                dice1Small.renderer().setVisible(true);
                dice2Small.renderer().setVisible(true);
            }
        }
    }

    public void showDices() {
        dice1.renderer().setVisible(true);
        dice2.renderer().setVisible(true);
    }

    public void setCursor(int cursor) {
        ui.setCursor(cursor);
    }

    public GameObject[] getProfils() { return this.profils; }
    public GameObject[][] getRessourceText() { return this.ressourceText; }

    public GameObject getSign() { return this.sign; }
    public GameObject[] getSignTexts() { return this.signTexts; }
    public GameObject getSignInfo() { return this.signInfo; }

    public GameObject getRessourceChoice() { return this.ressourceChoice; }
    public GameObject getPlayerChoice() { return this.playerChoice; }
}
