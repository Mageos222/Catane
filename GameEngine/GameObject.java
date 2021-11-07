package GameEngine;

public class GameObject {

    private Transform transform;
    private Collider collider;
    private Renderer renderer;

    public GameObject(String file, int width, int height) {    
        this.renderer = new SpriteRenderer(this, file);
        this.transform = new Transform(width, height);
    }

    public GameObject(String file) {
        this.renderer = new SpriteRenderer(this, file);
        this.transform = new Transform(renderer.getWidth(), renderer.getHeight());
    }

    public GameObject(String[] files, int width, int height) {
        this.renderer = new SpriteRenderer(this, files);
        this.transform = new Transform(width, height);

    }

    public void addComponent(Component component) {
        switch(component.getId()) {
            case 0: renderer = (Renderer)component; break;
            case 1: collider = (Collider)component; break;
            default: System.out.println("Error");
        }
    }

    public Transform transform() { return this.transform; }
    public Collider collider() { return this.collider; }
    public Renderer renderer() { return this.renderer; }
}