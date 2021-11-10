package GameEngine;

import java.awt.*;

public abstract class Collider implements Component {
    private GameObject parent;
    
    private boolean isHover;
    private boolean activ = true;

    private Action onHoverEnter;
    private Action onHoverExit;
    private Action onMouseClicked;

    protected Collider(GameObject parent) {    
        this.parent = parent;
    }

    public void init() {
        this.onHoverEnter = () -> { };
        this.onHoverExit = () -> { };
        this.onMouseClicked = () -> { };
    }

    public int getId() { return 1; }

    public abstract boolean isOn(Point p);
    public abstract double distanceFrom(Point p);

    public void setActiv(boolean activ) { this.activ = activ; }
    public boolean isActiv() { return activ; }

    public void setOnHoverEnterAction(Action action) { this.onHoverEnter = action; }
    public void setOnHoverExitAction(Action action) { this.onHoverExit = action; }
    public void setOnMouseClickedAction(Action action) { this.onMouseClicked = action; }

    public void onHoverEnter() { onHoverEnter.execute(); }
    public void onHoverExit() { onHoverExit.execute(); }
    public void onMouseClicked() { onMouseClicked.execute(); }

    public void setHover(boolean v) { this.isHover = v; }
    public boolean isHover() { return isHover; }

    public boolean isInteractable() { return true; }

    public GameObject getParent() { return this.parent; }
}
