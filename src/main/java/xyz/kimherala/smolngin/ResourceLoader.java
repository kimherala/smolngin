package xyz.kimherala.smolngin;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class ResourceLoader {
    private static ResourceLoader INSTANCE;
    private static final String resourceRoot = "../resources/main/";

    public ResourceLoader() {
    }

    public ResourceLoader getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ResourceLoader();
        }
        return INSTANCE;
    }

    public ByteBuffer loadFont(String fontName) {
        ByteBuffer result;

        byte[] bytesRead = loadBytes(resourceRoot + "font/" + fontName + ".ttf");
        result = ByteBuffer.allocateDirect(bytesRead.length);
        result.put(bytesRead);
        result.flip();

        return result;
    }

    public String loadShader(String fileName) {
        return loadText(resourceRoot + "shader/" + fileName);
    }

    private byte[] loadBytes(String filePath) {
        try {
            return Files.readAllBytes(Path.of(filePath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String loadText(String filePath) {
            StringBuilder result = new StringBuilder();

            try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(String.valueOf(filePath)))) {
                byte[] buffer = new byte[8192];

                while (bis.read(buffer) > 0) {
                    result.append(new String(buffer, StandardCharsets.UTF_8));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            return result.toString();
    }
}
