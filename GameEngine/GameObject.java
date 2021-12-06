package GameEngine;

import java.util.ArrayList;
import java.util.List;

public class GameObject {

    private Transform transform;
    private Collider collider;
    private Renderer renderer;

    private List<GameObject> children;

    public GameObject(String file, int width, int height) {    
        this.renderer = new SpriteRenderer(this, file);
        this.transform = new Transform(width, height);
        this.children = new ArrayList<>();
    }

    public GameObject(String file) {
        this.renderer = new SpriteRenderer(this, file);
        this.transform = new Transform(renderer.getWidth(), renderer.getHeight());
        this.children = new ArrayList<>();
    }

    public GameObject(String[] files, int width, int height) {
        this.renderer = new SpriteRenderer(this, files);
        this.transform = new Transform(width, height);
        this.children = new ArrayList<>();
    }

    public GameObject(int width, int height) {
        this.renderer = new SpriteRenderer(this);
        this.transform = new Transform(width, height);
        this.children = new ArrayList<>();
    }

    public void addComponent(Component component) {
        switch(component.getId()) {
            case 0: renderer = (Renderer)component; break;
            case 1: collider = (Collider)component; break;
            default: System.out.println("Error");
        }
    } 

    public void addChild(GameObject child) { this.children.add(child); }
    public List<GameObject> getChildren() { return this.children; }

    public Transform transform() { return this.transform; }
    public Collider collider() { return this.collider; }
    public Renderer renderer() { return this.renderer; }
}