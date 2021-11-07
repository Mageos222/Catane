import java.awt.*;

public class Interactable extends GameObject {

    private boolean isHover;

    private Action onHoverEnter;
    private Action onHoverExit;
    private Action onMouseClicked;

    private Distance dst;

    public Interactable(String file, int width, int height) {    
        super(file, width, height);
        init();
    }

    public Interactable(String file) {
        super(file);
        init();
    }

    public Interactable(String[] files, int width, int height) {
        super(files, width, height);
        init();
    }

    public void init() {
        this.dst = this.circle;

        this.onHoverEnter = () -> { };
        this.onHoverExit = () -> { };
        this.onMouseClicked = () -> { };
    }

    private Distance circle = new Distance() {
        public boolean isOn(Point p) {
            return distanceFrom(p) < getRelativWidth()/2f;
        }
        public double distanceFrom(Point p) {
            Point pos = new Point();
            pos.setLocation(getRelativCenterX(), getRelativCenterY());
            return pos.distance(p);
        }
    };
    private Distance square = new Distance() {
        public boolean isOn(Point p) {
            return p.getX() < getRelativCenterX()+getRelativWidth()/2f && p.getX() > getRelativCenterX()-getRelativWidth()/2f &&
                p.getY() < getRelativCenterY()+getRelativHeight()/2f && p.getY() > getRelativCenterY()-getRelativHeight()/2f;
        }
        public double distanceFrom(Point p) {
            if(isOn(p)) return 0;
            Point pos = new Point();
            pos.setLocation(getRelativCenterX(), getRelativCenterY());
            return pos.distance(p);
        }
    };

    public void setDistanceToSquare() { this.dst = square; }
    public void setDistanceToCircle() { this.dst = circle; }

    public boolean isOn(Point p) { return dst.isOn(p); }
    public double distanceFrom(Point p) { return dst.distanceFrom(p); }

    public void setOnHoverEnterAction(Action action) { this.onHoverEnter = action; }
    public void setOnHoverExitAction(Action action) { this.onHoverExit = action; }
    public void setOnMouseClickedAction(Action action) { this.onMouseClicked = action; }

    public void onHoverEnter() { onHoverEnter.execute(); }
    public void onHoverExit() { onHoverExit.execute(); }
    public void onMouseClicked() { onMouseClicked.execute(); }

    public void setHover(boolean v) { this.isHover = v; }
    public boolean isHover() { return isHover; }

    @Override
    public boolean isInteractable() { return true; }
    
    interface Distance {
        public boolean isOn(Point p);
        public double distanceFrom(Point p);
    }
}
