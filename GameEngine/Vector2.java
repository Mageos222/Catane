package GameEngine;

public class Vector2 {
    
    private int x;
    private int y;

    public Vector2(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Vector2 copy() {
        return new Vector2(x, y);
    }

    public Vector2 translate(Vector2 trans) { return new Vector2(this.x + trans.x, this.y + trans.y); }
    public Vector2 translate(int x, int y) { return new Vector2(this.x + x, this.y + y); }
    public Vector2 multiply(double n) { return new Vector2((int)(this.x * n), (int)(this.y * n)); }

    public static Vector2 multiply(Vector2 a, Vector2 b) { return new Vector2(a.x * b.x, a.y * b.y); }
    public static Vector2 add(Vector2 a, Vector2 b) { return new Vector2(a.x + b.x, a.y + b.y); }

    public int getX() { return this.x; }
    public int getY() { return this.y; }

    @Override
    public String toString() {
        return "("+x+";"+y+")";
    }
}
