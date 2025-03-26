package xyz.kimherala.smolngin;

import xyz.kimherala.smolngin.game.Game;
import xyz.kimherala.smolngin.graphics.Renderer;
import xyz.kimherala.smolngin.graphics.Window;

public class Main {
    private Window window;
    private Renderer renderer;
    private Game game;

    public static void main(String[] args) {
        new Main().run();
    }

    public void run() {
        window = new Window("lwjgltesting", 720, 720);
        renderer = new Renderer();
        game = new Game(window);

        window.updateLoop(renderer, game);

        terminate();
    }

    public void terminate() {
        game.cleanup();
        renderer.cleanup();
        window.cleanup();
    }
}
