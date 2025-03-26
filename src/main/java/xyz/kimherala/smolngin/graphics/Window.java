package xyz.kimherala.smolngin.graphics;

import org.lwjgl.opengl.GL11;
import xyz.kimherala.smolngin.game.Game;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
    // The window handle
    private long window;
    private String title;
    private int width;
    private int height;
    boolean resized = false;
    private TextRenderer textRenderer;
    private Font font;

    public Window(String title, int width, int height) {
        this.title = title;
        this.width = width;
        this.height = height;

        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);

        // glfwWindowHint(GLFW_TRANSPARENT_FRAMEBUFFER, GLFW_TRUE);
        // glfwWindowHint(GLFW_DECORATED, GLFW_FALSE);

        // Create the window
        this.window = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);
        if (this.window == NULL)
            throw new RuntimeException("Failed to create the GLFW window");

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(this.window, (window, key, scancode, action, mods) -> {
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
        });

        glfwSetWindowSizeCallback(this.window, (window, widthNew, heightNew) -> {
            this.width = widthNew;
            this.height = heightNew;
            this.setResized(true);
        });

        // Get the thread stack and push a new frame
        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(this.window, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            /*
            // Center the window
            glfwSetWindowPos(
                    this.window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
             */
        } // the stack frame is popped automatically

        // Make the OpenGL context current
        glfwMakeContextCurrent(this.window);
        // Enable v-sync
        glfwSwapInterval(0);

        GL.createCapabilities();

        // Make the window visible
        glfwShowWindow(this.window);

        textRenderer = new TextRenderer();
    }

    public void updateLoop(Renderer renderer, Game game) {
        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.

        final double fpsLimit = 1.0 / 30.0;
        double lastUpdateTime = 0;
        double lastFrameTime = 0;

        while (!glfwWindowShouldClose(this.window)) {
            double now = glfwGetTime();
            double deltaTime = now - lastUpdateTime;

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();
            //glfwWaitEvents();

            if ((now - lastFrameTime) >= fpsLimit) {
                game.update();

                GL11.glEnable(GL11.GL_DEPTH_TEST);
                renderer.render(this, game.getScene());
                GL11.glDisable(GL11.GL_DEPTH_TEST);

                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                textRenderer.render("Fontin-Regular","Hello, World!", 20,100, 100);
                GL11.glEnable(GL11.GL_BLEND);

                glfwSwapBuffers(this.window); // swap the color buffers
                lastFrameTime = now;
            }

            lastUpdateTime = now;
        }
    }

    public void cleanup() {
        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(this.window);
        glfwDestroyWindow(this.window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isResized() {
        return resized;
    }

    public void setResized(boolean resized) {
        this.resized = resized;
    }
}
