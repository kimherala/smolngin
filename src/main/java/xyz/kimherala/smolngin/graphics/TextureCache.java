package xyz.kimherala.smolngin.graphics;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TextureCache {
    private static final String DEFAULT_TEXTURE = "../resources/main/texture/default_texture.png";
    private final Map<String, Texture> textureMap;

    public TextureCache() {
        textureMap = new HashMap<>();
        textureMap.put(DEFAULT_TEXTURE, new Texture(DEFAULT_TEXTURE));
    }

    public void cleanup() {
        textureMap.values().forEach(Texture::cleanup);
    }

    public Texture createTexture(String texturePath) {
        return textureMap.computeIfAbsent(Objects.requireNonNullElse(texturePath, DEFAULT_TEXTURE), Texture::new);
    }

    public Texture getTexture(String texturePath) {
        Texture texture = null;

        if (texturePath != null) {
            texture = textureMap.get(texturePath);
        }
        if (texturePath == null) {
            texture = textureMap.get(DEFAULT_TEXTURE);
        }

        return texture;
    }
}
