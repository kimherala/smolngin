package xyz.kimherala.smolngin.graphics;

import org.joml.Matrix4f;
import org.lwjgl.opengl.*;

import java.util.ArrayList;
import java.util.List;

public class SpriteRenderer {
    private ShaderProgram shaderProgram;
    private UniformsMap uniformsMap;
    private Matrix4f projection;
    private int vao;
    private int vboPositions;
    private int vboIndices;
    private Texture spriteSheet;

    public SpriteRenderer() {
        List<ShaderProgram.ShaderModuleData> shaderModuleDataList = new ArrayList<>();
        shaderModuleDataList.add(new ShaderProgram.ShaderModuleData("sprite.vert", GL20.GL_VERTEX_SHADER));
        shaderModuleDataList.add(new ShaderProgram.ShaderModuleData("sprite.frag", GL20.GL_FRAGMENT_SHADER));
        shaderProgram = new ShaderProgram(shaderModuleDataList);
        createUniforms();

        spriteSheet = new Texture("../resources/main/spritesheet/spritesheet.png");

        vao = GL30.glGenVertexArrays();
        vboPositions = GL15.glGenBuffers();
        vboIndices = GL15.glGenBuffers();

        GL30.glBindVertexArray(vao);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboPositions);

        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, Float.BYTES * 4 * 4, GL15.GL_DYNAMIC_DRAW);
        GL20.glEnableVertexAttribArray(0);
        GL20.glVertexAttribPointer(0, 4, GL11.GL_FLOAT, false, 0, 0);

        int[] indices = new int[]{
                0, 1, 3, 3, 1, 2
        };

        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboIndices);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indices, GL15.GL_STATIC_DRAW);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL30.glBindVertexArray(0);

        projection = new Matrix4f().setOrtho(0.0f, 720.0f, 0.0f, 720.0f, 0.0f, 1000.0f);
    }

    public void cleanup() {
        shaderProgram.cleanup();
        spriteSheet.cleanup();
    }

    public void createUniforms() {
        uniformsMap = new UniformsMap(shaderProgram.getProgramId());
        uniformsMap.createUniform("projectionMatrix");
        uniformsMap.createUniform("textureSampler");
        uniformsMap.createUniform("spriteRect");
    }

    public void resize(int width, int height) {
        projection.setOrtho(0.0f, width, 0.0f, height, 0.0f, 1000.0f);
    }

    public void render(int x, int y, int width, int height, float x2, float y2, float width2, float height2) {
        shaderProgram.bind();

        uniformsMap.setUniform("projectionMatrix", projection);
        uniformsMap.setUniform("textureSampler", 0);
        uniformsMap.setUniform("spriteRect", x2, y2, width2, height2);

        float z = 1.0f;
        float scale = 1.0f;

        float xpos = x * scale;
        float ypos = y * scale;

        float w = width * scale;
        float h = height * scale;

        float[] vertices = new float[]{
                xpos, ypos + h, 0.0f, 0.0f,
                xpos, ypos, 0.0f, 1.0f,
                xpos + w, ypos, 1.0f, 1.0f,
                xpos + w, ypos + h, 1.0f, 0.0f
        };

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL30.glBindVertexArray(vao);
        spriteSheet.bind();

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboPositions);
        GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, vertices);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

        GL11.glDrawElements(GL11.GL_TRIANGLES, 6, GL11.GL_UNSIGNED_INT, 0);

        GL30.glBindVertexArray(0);
        shaderProgram.unbind();
    }
}
