package xyz.kimherala.smolngin.graphics;

import java.util.HashMap;
import java.util.Map;

public class FontCache {
    public record FontKey(String name, int size) {}
    private Map<FontKey, Font> fontMap;

    public FontCache() {
        fontMap = new HashMap<>();
    }

    public void addFont(String name, int fontSize) {
        fontMap.computeIfAbsent(new FontKey(name, fontSize), k -> new Font(k.name, k.size));
    }
}
