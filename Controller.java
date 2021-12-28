import GameEngine.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class Controller {
    private Game game;
    private Canvas canvas;

    private boolean canBuildVillage;
    private int canBuildRoad;
    private boolean robber;

    private boolean addObject = false;

    private Player[] dealPlayer;
    private int maxDeal;
    private Ressource[] dealVal;
    private int dealTurn;

    private GameObject tower;

    private int selectedPlayer = -1;

    private boolean steal;

    public Controller(Game g, Canvas c) {
        this.game = g;
        this.canvas = c;

        canBuildRoad = 1;
        canBuildVillage = true;
        robber = true;

        String[] towers = {"Images/Colonies/townRed.png","Images/Colonies/townBlue.png", 
            "Images/Colonies/townGreen.png", "Images/Colonies/townYellow.png"};
        tower = new GameObject(towers, 40, 40);

        this.dealVal = new Ressource[2];
        dealVal[0] = new Ressource();
        dealVal[1] = new Ressource();

        this.dealPlayer = new Player[2];
    }

    public void setNewObject() {
        if(robber) return;
        addObject = true;
        canvas.setCursor(12);
    }

    public boolean build(GameObject object, int x1, int y1, int x2, int y2, boolean isVillage, boolean isTown) {
        //if(!object.collider().isHover() || !addObject) return;
        if(!addObject) return false;

        if(!isTown && game.canBuildRoad(canBuildRoad > 0, x1, y1, x2, y2)) {
            game.addRoad(x1, y1, x2, y2, canBuildRoad==0);
            canBuildRoad--;

            object.collider().setOnHoverEnterAction(() -> { });
            object.collider().setOnHoverExitAction(() -> { });

            System.out.println("Road builded");
        }
        else if(isVillage && game.canBuildVillage(canBuildVillage, x1, y1)) {
            game.addVillage(x1, y1, !canBuildVillage);
            canBuildVillage = false;

            BufferedImage[] images = {
                object.renderer().getImages()[object.renderer().getRenderIndex()],
                tower.renderer().getImages()[object.renderer().getRenderIndex()]};
            object.renderer().setImages(images);
            object.renderer().setImage(0);

            int actualTurn = game.getTurn();
            object.collider().setOnHoverEnterAction(() -> snapUpdate(object, actualTurn));
            object.collider().setOnHoverExitAction(() -> unsnapUpdate(object, actualTurn));
            object.collider().setOnMouseClickedAction(() -> build(object, x1, y1, x2, y2, false, true));
        }
        else if(isTown && !isVillage && game.canBuildTown(x1, y1)) {
            game.addTown(x1, y1);

            object.collider().setOnHoverEnterAction(() -> focus(object, 10));
            object.collider().setOnHoverExitAction(() -> unfocus(object, 10));
        }
        else return false;

        object.collider().setHover(false);
        addObject = false;

        if(!isVillage) object.collider().setOnMouseClickedAction(() -> { });

        canvas.setCursor(0);

        return true;
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
        if(canBuildRoad > 0 || canBuildVillage || robber || game.isPlayingAnim()) return;

        int next = game.changeTurn();
        if(next == -1) {
            canBuildVillage = true;
            canBuildRoad = 1;
        }
        else if(next == 7) robber = true;

        addObject = false;
        canvas.setCursor(0);
    }

    public void moveRobber(int x, int y) {
        if(!robber) return;
        canvas.moveRobber(x, -y);
    }

    public void putRobber(Colony[] colonies) {
        if(!robber) return;

        game.setRobber();

        for(Colony colony : colonies)
            colony.setBlocked(true);

        robber = false;
        steal();
    }

    public void addValueToDealProp(int val) {
        if(!(dealPlayer[dealTurn] != null && !dealPlayer[dealTurn].possesse(dealVal[dealTurn])) && dealVal[dealTurn].sum() <= maxDeal) 
            dealVal[dealTurn].add(val, 1);
        else return;

        canvas.getSignTexts()[val].renderer().setImages(String.valueOf(dealVal[dealTurn].getRessource(val)));
    }

    public void openDeal() {
        if(canvas.getSign().renderer().isVisible()) return;

        showDeal();

        maxDeal = 5;
        dealTurn = 0;
        dealPlayer[0] = game.getCurrentPlayer();
        dealPlayer[1] = null;

        canvas.getSignInfo().renderer().setImages("What do you want to exchange?");
    }

    public void steal() {

        int count = 0;
        for(int i = 0; i < game.getPlayer().length; i++) 
            if(i != game.getTurn() && game.getPlayer()[i].isBlocked() && game.getPlayer()[i].getRessources().sum() >= 0) 
                count++;

        if(count == 0) return;

        int[] list = new int[count];
        int counter = 0;
        for(int i = 0; i < game.getPlayer().length; i++) 
            if(i != game.getTurn() && game.getPlayer()[i].isBlocked() && game.getPlayer()[i].getRessources().sum() >= 0) {
                list[counter] = i;
                counter++;
            }

        steal = true;
        dealPlayer[0] = game.getCurrentPlayer();

        canvas.getSign().renderer().setVisible(true);
        canvas.getSign().collider().setActiv(true);
        canvas.getRessourceChoice().renderer().setVisible(false);
        canvas.getRessourceChoice().collider().setActiv(false);

        canvas.showPlayersChoice(list);
    }

    private void showDeal() {
        if(canvas.getSign() == null) return;
        canvas.getSign().renderer().setVisible(true);
        canvas.getSign().collider().setActiv(true);

        for(GameObject obj : canvas.getSignTexts())
            obj.renderer().setImages("0");
    }

    public void resetDeal() {
        dealVal[dealTurn] = new Ressource();
        showDeal();
    }

    public void validDeal() {
        if(dealTurn == 1 && !dealVal[dealTurn].equals(new Ressource())) {
            canvas.getRessourceChoice().renderer().setVisible(false);
            canvas.getRessourceChoice().collider().setActiv(false);

            canvas.showCard(dealVal);

            int count = 0;
            for(int i = 0; i < game.getPlayer().length; i++) 
                if(i != game.getTurn() && game.getPlayer()[i].possesse(dealVal[1]))
                    count++;
            for(Port port : dealPlayer[0].getPorts())
                if(port.possesse(dealVal[1]) && port.getCost()*dealVal[1].sum() <= dealVal[0].sum()) {
                    count++;
                    break;
                }

            int[] players = new int[count];
            int index = 0;
            for(int i = 0; i < game.getPlayer().length; i++) 
                if(i != game.getTurn() && game.getPlayer()[i].possesse(dealVal[1])) {
                    players[index] = i;
                    index++;
                }
            for(Port port : dealPlayer[0].getPorts())
                if(port.possesse(dealVal[1]) && port.getCost()*dealVal[1].sum() <= dealVal[0].sum()) {
                    players[index] = port.getType()+4;
                    break;
                }

            canvas.showPlayersChoice(players);
        }
        else if(!dealVal[dealTurn].equals(new Ressource())) {
            dealTurn++;
            canvas.getSignInfo().renderer().setImages("What do you want to get?");
            showDeal();
        }
    }

    public void closeDeal() {
        if(steal) return; 

        canvas.getSign().renderer().setVisible(false);
        canvas.getSign().collider().setActiv(false);

        dealVal[0] = new Ressource();
        dealVal[1] = new Ressource();
        dealTurn = 0;

        canvas.emptyTemp();
    }   

    public void selectPlayer(int player, GameObject obj) {
        focus(obj, 20);
        selectedPlayer = player;
    }

    public void unselectPlayer(int player, GameObject obj) {
        if(selectedPlayer == player) {
            unfocus(obj, 20);
            selectedPlayer = -1;
        }
    }

    public void confirmPlayer(int player) {
        if(selectedPlayer == player) {
            selectedPlayer = -1;
            if(!steal) {
                Player target;
                if(player <= 3) target = game.getPlayer()[player];
                else target = dealPlayer[0].getPort(player-4);
                System.out.println("Deal with " + target.getName());

                target.pay(dealVal[1]);
                target.receive(dealVal[0]);
                dealPlayer[0].pay(dealVal[0]);
                dealPlayer[0].receive(dealVal[1]);
            }
            else {
                Random rnd = new Random();
                Ressource ressource = new Ressource(rnd.nextInt(5), 1);
                while(!game.getPlayer()[player].possesse(ressource)) 
                    ressource = new Ressource(rnd.nextInt(5), 1);

                game.getPlayer()[player].pay(ressource);
                dealPlayer[0].receive(ressource);
            }
            steal = false;

            closeDeal();
            game.updateText();
        }
    }

    public void setRobber(boolean v) { this.robber = v; }
    public void setAddObject(boolean v) { this.addObject = v; }
    public void setCanBuildRoad(int i) {this.canBuildRoad = i; }
}
