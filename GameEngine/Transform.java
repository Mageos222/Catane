package GameEngine;

import GameEngine.Renderer.Align;

public class Transform {

    private Vector2 position;
    private Vector2 size;

    private Vector2 relativPosition;
    private Vector2 relativSize;

    private double ratio;
    private int align = 4;

    private int[] shiftX;
    private int[] shiftY;

    public Transform(int width, int height) {
        this.position = new Vector2(0, 0);
        this.relativPosition = new Vector2(0, 0);

        this.size = new Vector2(width, height);
        this.relativSize = new Vector2(width, height);
    }

    public void setRelativ(int[] shiftX, int[] shiftY, double r) {
        this.shiftX = shiftX;
        this.shiftY = shiftY;
        this.ratio = r;

        //applyRelativ();
    }

    public void applyRelativ() {
        try {
            Vector2 pos = UI.getCenterPosition(position.getX(), position.getY());

            this.relativSize = this.size.multiply(ratio).translate(1, 1);
            this.relativPosition = new Vector2((int)(pos.getX()*ratio+shiftX[align%3]), (int)(pos.getY()*ratio+shiftY[align/3]));
        }
        catch(NullPointerException e) {
            this.relativSize = size.copy();
            this.relativPosition = position.copy();
        }
    }

    public void translate(int x, int y) { 
        this.position = this.position.translate(x, -y); 
        applyRelativ();
    }

    public void scale(int x, int y) { 
        this.size = this.size.translate(x, y); 
        applyRelativ();
    }
    public void scale(double n) { 
        this.size = this.size.multiply(n); 
        applyRelativ();
    }

    public void setPosition(int x, int y) {
        this.position = new Vector2(x, -y);
        applyRelativ();
    }

    public void setPosition(Vector2 pos) {
        this.position = pos.copy();
        applyRelativ();
    }

    public void setSize(int w, int h) {
        this.size = new Vector2(w, h);
        applyRelativ();
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

    public void setAlign(int a) { this.align = a; }
}
