package xyz.kimherala.smolngin.graphics;

import org.joml.Matrix4f;
import org.lwjgl.opengl.*;

import java.util.ArrayList;
import java.util.List;

public class ShapeRenderer {
    private ShaderProgram shaderProgram;
    private UniformsMap uniformsMap;
    private final Matrix4f projection;
    private int vao;
    private int vbo;

    public ShapeRenderer() {
        List<ShaderProgram.ShaderModuleData> shaderModuleDataList = new ArrayList<>();
        shaderModuleDataList.add(new ShaderProgram.ShaderModuleData("shape.vert", GL20.GL_VERTEX_SHADER));
        shaderModuleDataList.add(new ShaderProgram.ShaderModuleData("shape.frag", GL20.GL_FRAGMENT_SHADER));
        shaderProgram = new ShaderProgram(shaderModuleDataList);
        createUniforms();

        vao = GL30.glGenVertexArrays();
        vbo = GL15.glGenBuffers();

        GL30.glBindVertexArray(vao);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);

        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, Float.BYTES * 6 * 4, GL15.GL_DYNAMIC_DRAW);
        GL20.glEnableVertexAttribArray(0);
        GL20.glVertexAttribPointer(0, 4, GL11.GL_FLOAT, false, 0, 0);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL30.glBindVertexArray(0);

        projection = new Matrix4f().setOrtho(0.0f, 720.0f, 0.0f, 720.0f, 0.0f, 1000.0f);
    }

    public void cleanup() {
        shaderProgram.cleanup();
    }

    public void createUniforms() {
        uniformsMap = new UniformsMap(shaderProgram.getProgramId());
        uniformsMap.createUniform("projectionMatrix");
        uniformsMap.createUniform("shapeColor");
    }

    public void render(float x, float y, int width, int height) {
        shaderProgram.bind();

        uniformsMap.setUniform("projectionMatrix", projection);
        uniformsMap.setUniform("shapeColor", 1.0f, 1.0f, 1.0f);

        GL30.glBindVertexArray(vao);

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

                xpos, ypos + h, 0.0f, 0.0f,
                xpos + w, ypos, 1.0f, 1.0f,
                xpos + w, ypos + h, 1.0f, 0.0f
        };

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
        GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, vertices);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

        GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 6);

        GL30.glBindVertexArray(0);
        shaderProgram.unbind();
    }

    public void resize(int width, int height) {
        projection.setOrtho(0.0f, width, 0.0f, height, 0.0f, 1000.0f);
    }
}
