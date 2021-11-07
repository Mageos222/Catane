package GameEngine;

import java.awt.*;

public class BoxCollider extends Collider {

    public BoxCollider(GameObject parent) {
        super(parent);
    }

    @Override
    public boolean isOn(Point p) {
        return p.getX() < getParent().transform().getRelativPosition().getX() + getParent().transform().getRelativSize().getX() / 2f 
            && p.getX() > getParent().transform().getRelativPosition().getX() - getParent().transform().getRelativSize().getX() / 2f 
            && p.getY() < getParent().transform().getRelativPosition().getY() + getParent().transform().getRelativSize().getY() / 2f 
            && p.getY() > getParent().transform().getRelativPosition().getY() - getParent().transform().getRelativSize().getY() / 2f;
    }
    @Override
    public double distanceFrom(Point p) {
        Point pos = new Point();
        pos.setLocation(getParent().transform().getRelativPosition().getX(), getParent().transform().getRelativPosition().getY());
        return pos.distance(p);
    }
}
