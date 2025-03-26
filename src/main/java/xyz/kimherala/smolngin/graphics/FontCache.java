package xyz.kimherala.smolngin.graphics;

import java.util.HashMap;
import java.util.Map;

public class FontCache {
    public record FontKey(String name, int size) {}
    private final Map<FontKey, Font> fontMap;

    public FontCache() {
        fontMap = new HashMap<>();
    }

    public void cleanup() {
        fontMap.values().forEach(Font::cleanup);
    }

    public Font getFont(String name, int fontSize) {
        return fontMap.computeIfAbsent(new FontKey(name, fontSize), k -> new Font(k.name, k.size));
    }
}
