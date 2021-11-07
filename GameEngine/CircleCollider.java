package GameEngine;

import java.awt.*;

public class CircleCollider extends Collider {

    public CircleCollider(GameObject parent) {
        super(parent);
    }

    @Override
    public boolean isOn(Point p) {
        return distanceFrom(p) < getParent().transform().getRelativSize().getX()/2f;
    }
    @Override
    public double distanceFrom(Point p) {
        Point pos = new Point();
        pos.setLocation(getParent().transform().getRelativPosition().getX(), getParent().transform().getRelativPosition().getY());
        return pos.distance(p);
    }
}
