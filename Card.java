public abstract class Card {

    // Knight : set robber from Controller to true
    // University : add 1 point to the player score
    // Road : set canBuildRoad from Controller to 2 

    protected Controller controller;

    protected Card(Controller controller) {
        this.controller = controller;
    }

    public abstract void use();
    public abstract int getType();
}
