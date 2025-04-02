package xyz.kimherala.smolngin.graphics;

import org.joml.Matrix4f;
import org.lwjgl.opengl.*;

import java.util.ArrayList;
import java.util.List;

public class TextRenderer {
    private final ShaderProgram shaderProgram;
    private UniformsMap uniformsMap;
    private final FontCache fontCache;
    private final Matrix4f projection;

    public TextRenderer() {
        List<ShaderProgram.ShaderModuleData> shaderModuleDataList = new ArrayList<>();
        shaderModuleDataList.add(new ShaderProgram.ShaderModuleData("../resources/main/shader/glyph.vert", GL20.GL_VERTEX_SHADER));
        shaderModuleDataList.add(new ShaderProgram.ShaderModuleData("../resources/main/shader/glyph.frag", GL20.GL_FRAGMENT_SHADER));
        shaderProgram = new ShaderProgram(shaderModuleDataList);
        createUniforms();

        projection = new Matrix4f().setOrtho(0.0f, 720.0f, 0.0f, 720.0f, 0.0f, 1000.0f);
        fontCache = new FontCache();
    }

    public void cleanup() {
        shaderProgram.cleanup();
        fontCache.cleanup();
    }

    public void createUniforms() {
        uniformsMap = new UniformsMap(shaderProgram.getProgramId());
        uniformsMap.createUniform("projectionMatrix");
        uniformsMap.createUniform("textureSampler");
        uniformsMap.createUniform("textColor");
    }

    public void render(String fontName, String text, int fontSize, float x, float y) {
        Font font = fontCache.getFont(fontName, fontSize);

        shaderProgram.bind();

        uniformsMap.setUniform("projectionMatrix", projection);
        uniformsMap.setUniform("textureSampler", 0);
        uniformsMap.setUniform("textColor", 1.0f, 1.0f, 1.0f);

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL30.glBindVertexArray(font.getVaoId());

        float z = 1.0f;
        float scale = 1.0f;

        float copyX = x;

        for (String c : text.split("")) {
            Font.CharacterInfo ch = font.getCharacter(c);

            if (String.valueOf(' ').equals(c)) {
                x += ch.advance() * scale;
                continue;
            }
            if (String.valueOf('\n').equals(c)) {
                y -= ch.lineGap() * scale;
                x = copyX;
                continue;
            }

            float xpos = x + ch.bearing().x * scale;
            float ypos = y - (ch.size().y - ch.bearing().y) * scale;

            float w = ch.size().x * scale;
            float h = ch.size().y * scale;

            float[] vertices = new float[]{
                    xpos, ypos + h, 0.0f, 0.0f,
                    xpos, ypos, 0.0f, 1.0f,
                    xpos + w, ypos, 1.0f, 1.0f,

                    xpos, ypos + h, 0.0f, 0.0f,
                    xpos + w, ypos, 1.0f, 1.0f,
                    xpos + w, ypos + h, 1.0f, 0.0f
            };

            GL11.glBindTexture(GL11.GL_TEXTURE_2D, ch.textureId());
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, font.getVboId());
            GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, vertices);
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

            GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 6);

            x += ch.advance() * scale;
        }

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        GL30.glBindVertexArray(0);
        shaderProgram.unbind();
    }

    public void resize(int width, int height) {
        projection.setOrtho(0.0f, width, 0.0f, height, 0.0f, 1000.0f);
    }
}
