package xyz.kimherala.smolngin.graphics;

import java.util.HashMap;
import java.util.Map;

public class FontCache {
    public record FontKey(String fontName, int fontSize) {}
    private final Map<FontKey, Font> fontMap;

    public FontCache() {
        fontMap = new HashMap<>();
    }

    public void cleanup() {
        fontMap.values().forEach(Font::cleanup);
    }

    public Font getFont(String fontName, int fontSize) {
        return fontMap.computeIfAbsent(new FontKey(fontName, fontSize), k -> new Font(k.fontName, k.fontSize));
    }
}
