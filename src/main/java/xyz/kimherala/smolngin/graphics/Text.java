/*package xyz.kimherala.lwjgltesting.graphics;

import org.joml.Vector2i;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.system.MemoryStack;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.stb.STBTruetype.*;

public class Text {
    private static final String DEFAULT_FONT = "../resources/main/font/Fontin-Regular.ttf";
    private String text;
    private int fontSize = 12;
    private Projection projection;

    public Text(String text, int fontSize) {
        this.text = text;
        this.fontSize = fontSize;

        characters = new HashMap<>();

        ByteBuffer fontBuffer;
        try (FileChannel fileChannel = FileChannel.open(Path.of(DEFAULT_FONT), StandardOpenOption.READ)) {
            long fileSize = fileChannel.size();
            fontBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileSize);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load font", e);
        }

        STBTTFontinfo fontInfo = STBTTFontinfo.create();

        if (!stbtt_InitFont(fontInfo, fontBuffer, stbtt_GetFontOffsetForIndex(fontBuffer,0))) {
            throw new RuntimeException("Failed to load font!");
        }

        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

        for (int i = 0; i < 128; i++) {
            ByteBuffer bitmapBuffer;

            byte[] utf8Bytes = new byte[] {(byte) i};
            int utf8UnicodePoint = new String(utf8Bytes, StandardCharsets.UTF_8).codePointAt(0);

            int codePoint = stbtt_FindGlyphIndex(fontInfo, utf8UnicodePoint);
            float scale = stbtt_ScaleForMappingEmToPixels(fontInfo, 48);

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

                int textureId = glGenTextures();
                glBindTexture(GL_TEXTURE_2D, textureId);

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

            glTexImage2D(
                    GL_TEXTURE_2D,
                    0,
                    GL_RED,
                    characters.get(utf8UnicodePoint).size.x,
                    characters.get(utf8UnicodePoint).size.y,
                    0,
                    GL_RED,
                    GL_UNSIGNED_BYTE,
                    bitmapBuffer
            );

            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

            stbtt_FreeBitmap(bitmapBuffer);
        }

        vao = glGenVertexArrays();
        vbo = glGenBuffers();

        glBindVertexArray(vao);
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, 6 * 4, GL_DYNAMIC_DRAW);
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 4, GL_FLOAT, false, 0, 0);

        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);
    }

    public String getText() {
        return text;
    }

    public Projection getProjection() {
        return projection;
    }

    public int getWidth() {
        int result = 0;

        for (String c : text.split("")) {
            CharacterInfo ch = getCharacter(c);
            result += ch.advance();
        }

        return result;
    }

    public int getHeight() {
        int result = 0;

        for (String c : text.split("")) {
            CharacterInfo ch = getCharacter(c);

            if (ch.size().y > result) {
                result = ch.size().y;
            }
        }

        return result;
    }

    public void cleanup() {
        glDeleteBuffers(vbo);
        glDeleteVertexArrays(vao);
    }
}
*/