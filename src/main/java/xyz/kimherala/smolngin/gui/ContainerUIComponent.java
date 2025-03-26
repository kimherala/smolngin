package xyz.kimherala.smolngin.gui;

public class ContainerUIComponent implements UIComponent {
    private String id;
    private int width;
    private int height;
    private int x;
    private int y;

    public ContainerUIComponent(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public String getId() {
        return id;
    }
}
