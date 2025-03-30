package xyz.kimherala.smolngin.graphics;

import org.joml.Matrix4f;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

public class TextRenderer {
    private ShaderProgram shaderProgram;
    private UniformsMap uniformsMap;
    private FontCache fontCache;
    private Matrix4f projection;

    public TextRenderer() {
        List<ShaderProgram.ShaderModuleDate> shaderModuleDateList = new ArrayList<>();
        shaderModuleDateList.add(new ShaderProgram.ShaderModuleDate("../resources/main/shader/glyph.vert", GL_VERTEX_SHADER));
        shaderModuleDateList.add(new ShaderProgram.ShaderModuleDate("../resources/main/shader/glyph.frag", GL_FRAGMENT_SHADER));
        shaderProgram = new ShaderProgram(shaderModuleDateList);
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

        glActiveTexture(GL_TEXTURE0);
        glBindVertexArray(font.getVaoId());

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

            glBindTexture(GL_TEXTURE_2D, ch.textureId());
            glBindBuffer(GL_ARRAY_BUFFER, font.getVboId());

            FloatBuffer vertexBuffer = MemoryUtil.memCallocFloat(vertices.length);
            vertexBuffer.put(vertices).flip();
            glBufferSubData(GL_ARRAY_BUFFER, 0, vertexBuffer);
            //glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);
            MemoryUtil.memFree(vertexBuffer);

            glBindBuffer(GL_ARRAY_BUFFER, 0);

            glDrawArrays(GL_TRIANGLES, 0, 6);

            x += ch.advance() * scale;
        }
        glBindTexture(GL_TEXTURE_2D, 0);

        glBindVertexArray(0);
        shaderProgram.unbind();
    }

    public void resize(int width, int height) {
        projection.setOrtho(0.0f, width, 0.0f, height, 0.0f, 1000.0f);
    }
}
