package xyz.kimherala.smolngin.gui;

public class TextUIComponent implements UIComponent {
    private String id;
    private int width;
    private int height;
    private int x;
    private int y;

    private String text = "Hello, World";
    private int fontSize = 12;

    public TextUIComponent(int x, int y) {
    }

    public String getId() {
        return id;
    }
}
