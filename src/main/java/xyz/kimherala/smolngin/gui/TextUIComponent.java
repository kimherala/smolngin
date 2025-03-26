package xyz.kimherala.smolngin.gui;

import xyz.kimherala.smolngin.graphics.FontCache;

public class TextUIComponent implements UIComponent {
    private String id;
    private int width;
    private int height;
    private int x;
    private int y;

    private String fontName = "Fontin-Regular";
    private String text = "Hello, World";
    private int fontSize = 12;

    public TextUIComponent(int x, int y) {
    }

    public String getId() {
        return id;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String getText() {
        return text;
    }

    public String getFont() {
        return fontName;
    }

    public int getFontSize() {
        return fontSize;
    }
}
