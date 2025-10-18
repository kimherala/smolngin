package xyz.kimherala.smolngin.game;


import org.lwjgl.opengl.GL11;
import xyz.kimherala.smolngin.graphics.*;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwGetTime;

public class Game implements ApplicationInterface {
    private final Window window;
    private final TextureCache textureCache;
    private final SceneRenderer sceneRenderer;
    private final TextRenderer textRenderer;
    private final ShapeRenderer shapeRenderer;
    private final SpriteRenderer spriteRenderer;
    private FontCache fontCache;
    private final List<Entity> cubes;
    private final Scene scene;
    private float rotation;
    private int fps;
    private int frames;
    private int ticks;
    private int ticksInSec;

    private static final Color WHITE = Color.hsl(40f, 0.9f, 0.5f);

    public Game(Window window) {
        this.window = window;
        textureCache = new TextureCache();
        sceneRenderer = new SceneRenderer(textureCache);
        textRenderer = new TextRenderer();
        shapeRenderer = new ShapeRenderer();
        spriteRenderer = new SpriteRenderer();

        float[] positions = new float[]{
                // V0
                -0.5f, 0.5f, 0.5f,
                // V1
                -0.5f, -0.5f, 0.5f,
                // V2
                0.5f, -0.5f, 0.5f,
                // V3
                0.5f, 0.5f, 0.5f,
                // V4
                -0.5f, 0.5f, -0.5f,
                // V5
                0.5f, 0.5f, -0.5f,
                // V6
                -0.5f, -0.5f, -0.5f,
                // V7
                0.5f, -0.5f, -0.5f,

                // For text coords in top face
                // V8: V4 repeated
                -0.5f, 0.5f, -0.5f,
                // V9: V5 repeated
                0.5f, 0.5f, -0.5f,
                // V10: V0 repeated
                -0.5f, 0.5f, 0.5f,
                // V11: V3 repeated
                0.5f, 0.5f, 0.5f,

                // For text coords in right face
                // V12: V3 repeated
                0.5f, 0.5f, 0.5f,
                // V13: V2 repeated
                0.5f, -0.5f, 0.5f,

                // For text coords in left face
                // V14: V0 repeated
                -0.5f, 0.5f, 0.5f,
                // V15: V1 repeated
                -0.5f, -0.5f, 0.5f,

                // For text coords in bottom face
                // V16: V6 repeated
                -0.5f, -0.5f, -0.5f,
                // V17: V7 repeated
                0.5f, -0.5f, -0.5f,
                // V18: V1 repeated
                -0.5f, -0.5f, 0.5f,
                // V19: V2 repeated
                0.5f, -0.5f, 0.5f,
        };
        float[] textCoords = new float[]{
                0.0f, 0.0f,
                0.0f, 0.5f,
                0.5f, 0.5f,
                0.5f, 0.0f,

                0.0f, 0.0f,
                0.5f, 0.0f,
                0.0f, 0.5f,
                0.5f, 0.5f,

                // For text coords in top face
                0.0f, 0.5f,
                0.5f, 0.5f,
                0.0f, 1.0f,
                0.5f, 1.0f,

                // For text coords in right face
                0.0f, 0.0f,
                0.0f, 0.5f,

                // For text coords in left face
                0.5f, 0.0f,
                0.5f, 0.5f,

                // For text coords in bottom face
                0.5f, 0.0f,
                1.0f, 0.0f,
                0.5f, 0.5f,
                1.0f, 0.5f,
        };
        int[] indices = new int[]{
                // Front face
                0, 1, 3, 3, 1, 2,
                // Top Face
                8, 10, 11, 9, 8, 11,
                // Right face
                12, 13, 7, 5, 12, 7,
                // Left face
                14, 15, 6, 4, 14, 6,
                // Bottom face
                16, 18, 19, 17, 16, 19,
                // Back face
                4, 6, 7, 5, 4, 7,};

        scene = new Scene(window.getWidth(), window.getHeight());

        Texture texture = textureCache.createTexture(null);
        Material material = new Material();

        material.setTexturePath(texture.getTexturePath());
        List<Material> materialList = new ArrayList<>();
        materialList.add(material);

        Mesh mesh = new Mesh(positions, textCoords, indices);
        material.getMeshList().add(mesh);

        Model cubeModel = new Model("cube-model", materialList);

        scene.addModel(cubeModel);

        cubes = new ArrayList<>();

        for (int i = 0; i < 1; i++) {
            Entity cube = new Entity("cube-entity-" + i, cubeModel.getId());
            cube.setPosition(0, i, -5);
            scene.addEntity(cube);
            cubes.add(cube);
        }
    }

    public void cleanup() {
        scene.cleanup();
        sceneRenderer.cleanup();
        textRenderer.cleanup();
        shapeRenderer.cleanup();
        spriteRenderer.cleanup();
    }

    public void update(float dt) {
        if (window.isResized() ) {
            GL11.glViewport(0, 0, (int)(window.getWidthWithScale()), (int)(window.getHeightWithScale()));
            scene.resize(window.getWidth(), window.getHeight());
            textRenderer.resize(window.getWidth(), window.getHeight());
            shapeRenderer.resize(window.getWidth(), window.getHeight());
            spriteRenderer.resize(window.getWidth(), window.getHeight());
            window.setResized(false);
        }

        rotation += 50.0f * dt;
        if (rotation > 360) {
            rotation = 0;
        }

        for (Entity cube : cubes) {
            cube.setRotation(1, 1, 1, (float) Math.toRadians(rotation));
            cube.updateModelMatrix();
        }
    }

    public void render() {
        GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        sceneRenderer.render(this.scene);
        spriteRenderer.render(0, 200, 64, 64, 0.0f + 0.0625f,0.0f,0.0625f,0.0625f);
        GL11.glDisable(GL11.GL_DEPTH_TEST);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        textRenderer.render("Fontin-Regular", "FPS: " + fps, 20,0, window.getHeight()-20, WHITE);
        textRenderer.render("Fontin-Regular", "Tick: " + ticksInSec, 20, 0, window.getHeight()-40, WHITE);
        GL11.glDisable(GL11.GL_BLEND);

        shapeRenderer.render(0, 0, 100, 100, WHITE);
        shapeRenderer.render(window.getWidth()-100, window.getHeight()-100, 100, 100, WHITE);

        glfwSwapBuffers(window.getWindow()); // swap the color buffers
    }

    public void loop() {
        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.

        float t = 0.0f;
        final float dt = 1.0f / 60.0f;
        float currentTime = (float) glfwGetTime();
        float startTime = (float) glfwGetTime();
        float accumulator = 0.0f;

        while (!glfwWindowShouldClose(window.getWindow())) {
            float newTime = (float) glfwGetTime();
            float frameTime = newTime - currentTime;
            currentTime = newTime;

            if (frameTime > 0.25f) {
                frameTime = 0.25f;
            }

            accumulator += frameTime;
            while (accumulator > dt) {
                // Poll for window events. The key callback above will only be
                // invoked during this call.
                glfwPollEvents();
                //glfwWaitEvents();

                update(dt);
                t += dt;
                accumulator -= dt;
                ticks++;
            }

            if (frames >= 100) {
                fps = (int) (frames / (currentTime - startTime));
                ticksInSec = (int) (ticks / (currentTime - startTime));
                startTime = currentTime;
                ticks = 0;
                frames = 0;
            }
            frames++;

            render();
        }
    }
}
