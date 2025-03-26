package xyz.kimherala.smolngin.graphics;


import static org.lwjgl.opengl.GL11.*;

public class Renderer {
    private SceneRenderer sceneRenderer;

    public Renderer() {
        sceneRenderer = new SceneRenderer();
    }

    public void render(Window window, Scene scene) {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        if (window.isResized() ) {
            glViewport(0, 0, window.getWidth(), window.getHeight());
            scene.resize(window.getWidth() , window.getHeight());
            window.setResized(false);
        }

        sceneRenderer.render(scene);
    }

    public void cleanup() {
        sceneRenderer.cleanup();
    }
}
