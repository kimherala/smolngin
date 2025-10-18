package xyz.kimherala.smolngin.graphics;

import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.opengl.GL11;
import org.lwjgl.stb.STBImage;
import xyz.kimherala.smolngin.ResourceLoader;
import xyz.kimherala.smolngin.game.Game;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {
    // The window handle
    private final long window;
    private String title;
    private int width;
    private int height;
    private int windowWidth;
    private int windowHeight;
    private int framebufferWidth;
    private int framebufferHeight;
    private float currentScaleX;
    private float currentScaleY;
    boolean resized = false;

    public Window(String title, int width, int height) {
        this.title = title;
        this.width = width;
        this.height = height;

        ResourceLoader resourceLoader = new ResourceLoader().getInstance();

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
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

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

        glfwSetWindowSizeCallback(window, (window, widthNew, heightNew) -> {
            this.width = widthNew;
            this.height = heightNew;
            this.setResized(true);

            try (MemoryStack stack = stackPush()) {
                IntBuffer pWidth = stack.mallocInt(1); // int*
                IntBuffer pHeight = stack.mallocInt(1); // int*

                // Get the window size passed to glfwCreateWindow
                glfwGetWindowSize(window, pWidth, pHeight);

                this.windowWidth = pWidth.get(0);
                this.windowHeight = pHeight.get(0);
            }

            this.currentScaleX = (float)this.framebufferWidth / this.windowWidth;
            this.currentScaleY = (float)this.framebufferHeight / this.windowHeight;
        });

        glfwSetFramebufferSizeCallback(window, (window, widthNew, heightNew) -> {
            this.setResized(true);

            try (MemoryStack stack = stackPush()) {
                IntBuffer fWidth = stack.mallocInt(1); // int*
                IntBuffer fHeight = stack.mallocInt(1); // int*

                // Get the window size passed to glfwCreateWindow
                glfwGetFramebufferSize(window, fWidth, fHeight);

                this.framebufferWidth = fWidth.get(0);
                this.framebufferHeight = fHeight.get(0);
            }

            this.currentScaleX = (float)this.framebufferWidth / this.windowWidth;
            this.currentScaleY = (float)this.framebufferHeight / this.windowHeight;
        });

        // Get the thread stack and push a new frame
        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*
            IntBuffer fWidth = stack.mallocInt(1); // int*
            IntBuffer fHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight);

            // Get framebuffer size in pixels
            glfwGetFramebufferSize(window, fWidth, fHeight);

            this.windowWidth = pWidth.get(0);
            this.windowHeight = pHeight.get(0);
            this.framebufferWidth = fWidth.get(0);
            this.framebufferHeight = fHeight.get(0);
        }

        this.currentScaleX = (float)this.framebufferWidth / this.windowWidth;
        this.currentScaleY = (float)this.framebufferHeight / this.windowHeight;

        // Make the OpenGL context current
        glfwMakeContextCurrent(this.window);
        // Enable v-sync
        glfwSwapInterval(1);

        GL.createCapabilities();

        // Make the window visible
        glfwShowWindow(this.window);
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

    public float getWidthWithScale() {
        return width*currentScaleX;
    }

    public float getHeightWithScale() {
        return height*currentScaleY;
    }

    public boolean isResized() {
        return resized;
    }

    public void setResized(boolean resized) {
        this.resized = resized;
    }

    public long getWindow() {
        return window;
    }
}
