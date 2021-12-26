public class Knight extends Card{
    
    public Knight(Controller controller){
        super(controller);
    }

    public void use(){
        this.controller.setRobber(true);
    }
}
