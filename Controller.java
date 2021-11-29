import GameEngine.UI;
import GameEngine.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Controller {
    private Game game;
    private UI ui;
    private Canvas canvas;

    private boolean canBuildVillage;
    private boolean canBuildRoad;
    private boolean voleur;

    private boolean addObject = false;

    private final Ressource roadCost = new Ressource(0, 1, 0, 0, 1);
    private final Ressource villageCost = new Ressource(1, 1, 1, 0, 1);
    private final Ressource townCost = new Ressource(2, 0, 0, 3, 0);

    private GameObject tower;

    public Controller(Game g, UI u) {
        this.game = g;
        this.ui = u;

        canBuildRoad = true;
        canBuildVillage = true;
        voleur = true;

        String[] towers = {"Images/Colonies/townRed.png","Images/Colonies/townBlue.png", 
            "Images/Colonies/townGreen.png", "Images/Colonies/townYellow.png"};
        tower = new GameObject(towers, 40, 40);
    }

    public void setCanvas(Canvas c) { this.canvas = c; }

    public void updateText() {
        for(int j = 0; j < game.getPlayer().length; j++)
            for(int i = 0; i < 5; i++) 
                canvas.getRessourceText()[j][i].renderer().setImages(String.valueOf(game.getPlayer()[j].getRessource(i)));
    }

    public void setNewObject() {
        if(voleur) return;
        addObject = true;
        ui.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public void build(GameObject object, int x1, int y1, int x2, int y2, boolean isVillage, boolean isTown) {
        if(!object.collider().isHover() || !addObject) return;

        if(!isTown && (canBuildRoad || game.getPlayer()[game.getTurn()].possesse(roadCost)) && game.getMap().canBuildRoad(game.getTurn(), y1, x1, y2, x2)) {
            if(!canBuildRoad) {
                game.getPlayer()[game.getTurn()].pay(roadCost);
                game.getPlayer()[game.getTurn()].increment(1);
                updateText();
            }
            game.getMap().buildRoad(game.getTurn(), y1, x1, y2, x2);
            canBuildRoad = false;
        }
        else if(isVillage && (canBuildVillage&& game.getMap().canBuildFirstVillage(game.getTurn(), x1, y1) || 
                            game.getPlayer()[game.getTurn()].possesse(villageCost) && game.getMap().canBuildVillage(game.getTurn(), x1, y2))) {
            if(!canBuildVillage) {
                game.getPlayer()[game.getTurn()].pay(villageCost);
                game.getPlayer()[game.getTurn()].increment(1);
                updateText();
            }
            game.getMap().buildVillage(game.getTurn(), x1, y1);
            canBuildVillage = false;
            game.getPlayer()[game.getTurn()].addColony(game.getMap().getColony(x1, y1));
            System.out.println("Colony (" + x1+";"+y1+"):\n"+game.getMap().getColony(x1, y1).toString());
        }
        else if(isTown && game.getPlayer()[game.getTurn()].possesse(townCost)) {
            game.getPlayer()[game.getTurn()].pay(townCost);
            game.getMap().getColony(x1, y1).upgrade();
            updateText();
        }
        else return;

        object.collider().setHover(false);
        addObject = false;

        if(isVillage) {
            BufferedImage[] images = {object.renderer().getImages()[object.renderer().getRenderIndex()],
                tower.renderer().getImages()[object.renderer().getRenderIndex()]};
            object.renderer().setImages(images);
            object.renderer().setImage(0);

            int actualTurn = game.getTurn();
            object.collider().setOnHoverEnterAction(() -> snapUpdate(object, actualTurn));
            object.collider().setOnHoverExitAction(() -> unsnapUpdate(object, actualTurn));
            object.collider().setOnMouseClickedAction(() -> build(object, x1, y1, x2, y2, false, true));
        }
        else {
            if(isTown) {
                object.collider().setOnHoverEnterAction(() -> focus(object, 10));
                object.collider().setOnHoverExitAction(() -> unfocus(object, 10));
            }
            else {
                object.collider().setOnHoverEnterAction(() -> { });
                object.collider().setOnHoverExitAction(() -> { });
            }
            object.collider().setOnMouseClickedAction(() -> { });
        }

        ui.setCursor(Cursor.getDefaultCursor());
    }

    public void focus(GameObject object, int size) {
        object.transform().scale(size, size);
        object.collider().setHover(true);
        object.renderer().setZindex(object.renderer().getZindex()+5);
    }

    public void unfocus(GameObject object, int size) {
        object.transform().scale(-size, -size);
        object.collider().setHover(false);
        object.renderer().setZindex(object.renderer().getZindex()-5);
    } 

    public void snap(GameObject object) {
        if(!addObject) return;
        object.collider().setHover(true);
        object.renderer().setImage(game.getTurn());

        object.renderer().setVisible(true);
    }

    public void unsnap(GameObject object) {
        object.renderer().setVisible(false);
        object.collider().setHover(false);
    }

    public void snapUpdate(GameObject object, int i) {
        if(!addObject || i != game.getTurn()) {
            focus(object, 10);
            return; 
        }
        object.collider().setHover(true);
        object.renderer().nextImage();
    }

    public void unsnapUpdate(GameObject object, int i) {
        if(!addObject || i != game.getTurn()) unfocus(object, 10);

        object.collider().setHover(false);
        object.renderer().setImage(0);
    }

    public void nextTurn() {
        if(canBuildRoad || canBuildVillage || voleur) return;

        canvas.getProfils()[game.getTurn()].transform().scale(0.8);

        game.setTurn((game.getTurn()+1) % game.getPlayer().length);
        game.increment();
        if(game.getNbTurn() < 2*game.getPlayer().length) {
            canBuildVillage = true;
            canBuildRoad = true;
        }

        canvas.getProfils()[game.getTurn()].transform().scale(1.2);
        game.setTurnAction(false);

        addObject = false;
    }

    public void addRessourceText(int index, int x, int y, Renderer.Align align) {
        String[] file = new String[] { "wheatIco.png", "wood.png", "sheepIco.png", "rock.png", "clay.png" };

        canvas.getRessourceText()[index] = new GameObject[5];
        for(int i = 0; i < 5; i++) {
            canvas.getRessourceText()[index][i] = new GameObject(50, 75);
            canvas.getRessourceText()[index][i].transform().setPosition(x-(x/Math.abs(x))*i*60, y-30);
            canvas.getRessourceText()[index][i].addComponent(new TextRenderer(canvas.getRessourceText()[0][i], "0"));
            canvas.getRessourceText()[index][i].renderer().setZindex(1);
            canvas.getRessourceText()[index][i].renderer().setAlign(align);
            ui.add(canvas.getRessourceText()[index][i]);

            GameObject icon = new GameObject("Images/GamePage/" + file[i], 50, 50);
            icon.transform().setPosition(x-(x/Math.abs(x))*i*60, y+30);
            icon.renderer().setZindex(1);
            icon.renderer().setAlign(align);
            ui.add(icon);
        }
    }

    public void moveVoleur(int x, int y) {
        if(!voleur) return;
        canvas.moveVoleur(x, -y);
    }

    public void putVoleur(Colony[] colonies) {
        if(!voleur) return;

        if(game.getMap() != null) game.getMap().resetBlocked();
        for(Colony colony : colonies)
            colony.setBlocked(true);

        voleur = false;
    }

    public void setVoleur(boolean v) { this.voleur = v; }
    public void setAddObject(boolean v) { this.addObject = v; }
}
