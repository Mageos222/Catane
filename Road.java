public class Road extends Card{

    public Road(Controller controller){
        super(controller);
    }

    public void use(){
        this.controller.setCanBuildRoad(2);
    }
} 
