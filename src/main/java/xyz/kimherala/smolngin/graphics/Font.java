package xyz.kimherala.smolngin.graphics;

import org.joml.Vector2i;
import org.lwjgl.opengl.*;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.system.MemoryStack;
import xyz.kimherala.smolngin.ResourceLoader;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;

import static org.lwjgl.stb.STBTruetype.*;

public class Font {
    //private static final String DEFAULT_FONT = "../resources/main/font/Fontin-Regular.ttf";
    public record CharacterInfo(int textureId, Vector2i size, Vector2i bearing, int advance, int lineGap, float baseline) {}

    private int fontHeight;
    private HashMap<Integer, CharacterInfo> characters;
    private int vao;
    private int vbo;

    public Font(String fontName, int fontSize) {
        ResourceLoader resourceLoader = new ResourceLoader().getInstance();
        characters = new HashMap<>();

        ByteBuffer fontBuffer = resourceLoader.loadFont(fontName);

        STBTTFontinfo fontInfo = STBTTFontinfo.create();

        if (!stbtt_InitFont(fontInfo, fontBuffer, stbtt_GetFontOffsetForIndex(fontBuffer,0))) {
            throw new RuntimeException("Failed to load font!");
        }

        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);

        for (int i = 0; i < 128; i++) {
            ByteBuffer bitmapBuffer;

            byte[] utf8Bytes = new byte[] {(byte) i};
            int utf8UnicodePoint = new String(utf8Bytes, StandardCharsets.UTF_8).codePointAt(0);

            int codePoint = stbtt_FindGlyphIndex(fontInfo, utf8UnicodePoint);
            float scale = stbtt_ScaleForMappingEmToPixels(fontInfo, fontSize);

            int advanceWidth;
            int leftSideBearing;
            int ascent;
            int descent;
            int lineGap;
            int iy0;

            try (MemoryStack stack = MemoryStack.stackPush()) {
                IntBuffer leftSideBearingBuffer = stack.mallocInt(1);
                IntBuffer advanceWidthBuffer = stack.mallocInt(1);
                stbtt_GetGlyphHMetrics(fontInfo, codePoint, advanceWidthBuffer, leftSideBearingBuffer);

                IntBuffer ascentBuffer = stack.mallocInt(1);
                IntBuffer descentBuffer = stack.mallocInt(1);
                IntBuffer lineGapBuffer = stack.mallocInt(1);
                stbtt_GetFontVMetrics(fontInfo, ascentBuffer, descentBuffer, lineGapBuffer);

                IntBuffer iy0Buffer = stack.mallocInt(1);
                stbtt_GetGlyphBitmapBox(fontInfo, codePoint, scale, scale, null, iy0Buffer, null, null);

                advanceWidth = advanceWidthBuffer.get();
                leftSideBearing = leftSideBearingBuffer.get();
                ascent = ascentBuffer.get();
                descent = descentBuffer.get();
                lineGap = lineGapBuffer.get();
                iy0 = iy0Buffer.get();

                IntBuffer width = stack.mallocInt(1);
                IntBuffer height = stack.mallocInt(1);

                // Doesn't like control characters
                bitmapBuffer = stbtt_GetGlyphBitmap(
                        fontInfo,
                        scale,
                        scale,
                        codePoint,
                        width,
                        height,
                        null,
                        null
                );

                int bearingX = (int) (leftSideBearing * scale);
                int bearingY = -iy0;
                float baseline = ascent * scale;

                int textureId = GL11.glGenTextures();
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);

                characters.put(utf8UnicodePoint, new CharacterInfo(
                        textureId,
                        new Vector2i(width.get(), height.get()),
                        new Vector2i(bearingX, bearingY),
                        (int) (advanceWidth * scale),
                        (int) ((ascent - descent + lineGap) * scale),
                        baseline
                ));
            }

            // bitmap could be a control character == invisible
            // skip making a texture in this case
            if (bitmapBuffer == null)  {
                continue;
            }

            GL11.glTexImage2D(
                    GL11.GL_TEXTURE_2D,
                    0,
                    GL11.GL_RED,
                    characters.get(utf8UnicodePoint).size.x,
                    characters.get(utf8UnicodePoint).size.y,
                    0,
                    GL11.GL_RED,
                    GL11.GL_UNSIGNED_BYTE,
                    bitmapBuffer
            );

            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);

            stbtt_FreeBitmap(bitmapBuffer);
        }

        setFontHeight();

        vao = GL30.glGenVertexArrays();
        vbo = GL15.glGenBuffers();

        GL30.glBindVertexArray(vao);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);

        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, Float.BYTES * 6 * 4, GL15.GL_DYNAMIC_DRAW);
        GL20.glEnableVertexAttribArray(0);
        GL20.glVertexAttribPointer(0, 4, GL11.GL_FLOAT, false, 0, 0);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL30.glBindVertexArray(0);
    }

    public void cleanup() {
        GL15.glDeleteBuffers(vbo);
        GL30.glDeleteVertexArrays(vao);

        for (CharacterInfo ci : characters.values()) {
            GL11.glDeleteTextures(ci.textureId);
        }
    }

    public int getVaoId() {
        return vao;
    }

    public int getVboId() {
        return vbo;
    }

    public CharacterInfo getCharacter(String key) {
        return characters.get(key.codePointAt(0));
    }

    public int getWidth(String text) {
        int result = 0;

        for (String c : text.split("")) {
            CharacterInfo ch = getCharacter(c);
            result += ch.advance();
        }

        return result;
    }

    private void setFontHeight() {
        int result = 0;

        for (CharacterInfo ch : characters.values()) {
            if (ch.size().y > result) {
                result = ch.size().y;
            }
        }

        fontHeight = result;
    }

    public int getFontHeight() {
        return fontHeight;
    }
}

