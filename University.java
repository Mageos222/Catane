public class University extends Card{
    
    public University(Controller controller){
        super(controller);
    }

    public void use(){
        controller.addPoint();
    }

    @Override
    public int getType() {
        return 0;
    }
}

