package xyz.kimherala.smolngin;

import xyz.kimherala.smolngin.game.Game;
import xyz.kimherala.smolngin.graphics.Renderer;
import xyz.kimherala.smolngin.graphics.Window;

public class Main {
    private Window window;
    private Game game;

    public static void main(String[] args) {
        new Main().run();
    }

    public void run() {
        window = new Window("smolngin-demo", 720, 720);
        game = new Game(window);

        game.loop();

        cleanup();
    }

    public void cleanup() {
        game.cleanup();
        window.cleanup();
    }
}
