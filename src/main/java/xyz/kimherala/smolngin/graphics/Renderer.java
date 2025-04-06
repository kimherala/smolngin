package xyz.kimherala.smolngin.graphics;


import static org.lwjgl.opengl.GL11.*;

public class Renderer {
    private SceneRenderer sceneRenderer;

    public Renderer() {
        sceneRenderer = new SceneRenderer();
    }

    public void render(Scene scene) {
        sceneRenderer.render(scene);
    }

    public void cleanup() {
        sceneRenderer.cleanup();
    }
}
