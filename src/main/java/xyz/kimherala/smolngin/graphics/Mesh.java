package xyz.kimherala.smolngin.graphics;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

// OpenGL uses vertexes counterclockwise
// Indices are shared data between triangles that is also used counterclockwise
//
// Position:
// v0, v3
// v1, v2
//
// [v0, v1, v2, v3]
//
// Indices:
// [0, 1, 3, 3, 1, 2]
public class Mesh {
    private final int vaoId;
    private final List<Integer> vboIdList;
    private final int vertexCount;

    public Mesh(float[] positions, float[] textCoords, int[] indices) {
        vertexCount = indices.length;

        vaoId = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vaoId);

        vboIdList = new ArrayList<>();

        // Position VBO
        storeDataInAttributeList(0, 3, positions);

        // Texture VBO
        storeDataInAttributeList(1, 2, textCoords);

        // Indices VBO
        bindIndicesBuffer(indices);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL30.glBindVertexArray(0);
    }

    public Mesh(float[] positions, int[] indices) {
        vertexCount = indices.length;

        vaoId = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vaoId);

        vboIdList = new ArrayList<>();

        // Position VBO
        storeDataInAttributeList(0, 3, positions);

        // Indices VBO
        bindIndicesBuffer(indices);

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL30.glBindVertexArray(0);
    }

    public void cleanup() {
        vboIdList.forEach(GL30::glDeleteBuffers);
        GL30.glDeleteVertexArrays(vaoId);
    }

    public void storeDataInAttributeList(int attributeNumber, int size, float[] data) {
        FloatBuffer dataBuffer;

        int vboId = GL15.glGenBuffers();
        vboIdList.add(vboId);
        dataBuffer = MemoryUtil.memCallocFloat(data.length);
        dataBuffer.put(data).flip();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, dataBuffer, GL15.GL_STATIC_DRAW);
        GL20.glEnableVertexAttribArray(attributeNumber);
        GL20.glVertexAttribPointer(attributeNumber, size, GL11.GL_FLOAT, false, 0, 0);

        MemoryUtil.memFree(dataBuffer);
    }

    public void bindIndicesBuffer(int[] data) {
        IntBuffer dataBuffer;

        int vboId = GL15.glGenBuffers();
        vboIdList.add(vboId);
        dataBuffer = MemoryUtil.memCallocInt(data.length);
        dataBuffer.put(data).flip();
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboId);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, dataBuffer, GL15.GL_STATIC_DRAW);

        MemoryUtil.memFree(dataBuffer);
    }

    public int getVaoId() {
        return vaoId;
    }

    public int getVertexCount() {
        return vertexCount;
    }
}