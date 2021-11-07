package GameEngine;

public class Transform {

    private Vector2 position;
    private Vector2 size;

    private Vector2 relativPosition;
    private Vector2 relativSize;

    private double ratio;

    public Transform(int width, int height) {
        this.position = new Vector2(0, 0);
        this.relativPosition = new Vector2(0, 0);

        this.size = new Vector2(width, height);
        this.relativSize = new Vector2(width, height);
    }

    public void translate(int x, int y) { 
        this.position = this.position.translate(x, y); 
        this.relativPosition = this.relativPosition.translate((int)(x*ratio), (int)(y*ratio));
    }

    public void scale(int x, int y) { 
        this.size = this.size.translate(x, y); 
        calculateRelativSize();
    }
    public void scale(double n) { 
        this.size = this.size.multiply(n); 
        calculateRelativSize();
    }

    public void setPosition(int x, int y) {
        this.position = new Vector2(x, y);
        this.relativPosition = Vector2.add(this.position, new Vector2(-x, -y)).multiply(ratio);
    }

    public void setPosition(Vector2 pos) {
        this.position = pos.copy();
        this.relativPosition = Vector2.add(this.position, pos.multiply(-ratio));
    }

    public void setSize(int w, int h) {
        this.size = new Vector2(w, h);
        calculateRelativSize();
    }

    public void setRelativPosition(int x, int y) {
        this.relativPosition = new Vector2(x, y);
    }

    public void setRelativSize(double ratio) {
        this.ratio = ratio;
        calculateRelativSize();
    }

    private void calculateRelativSize() {
        this.relativSize = this.size.multiply(ratio).translate(1, 1);
    }

    public Vector2 getPosition() { return this.position; }
    public Vector2 getCenterPosition() { return this.position.translate(this.size.multiply(-0.5)); }
    public Vector2 getRelativCenterPosition() { return this.relativPosition.translate(this.relativSize.multiply(-0.5)); }
    public Vector2 getRelativPosition() { return this.relativPosition; }
    public Vector2 getSize() { return this.size; }
    public Vector2 getRelativSize() { return this.relativSize; }
}
