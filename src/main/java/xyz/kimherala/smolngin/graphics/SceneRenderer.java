package xyz.kimherala.smolngin.graphics;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SceneRenderer {
    private ShaderProgram shaderProgram;
    private UniformsMap uniformsMap;
    private TextureCache textureCache;

    public SceneRenderer(TextureCache textureCache) {
        List<ShaderProgram.ShaderModuleData> shaderModuleDataList = new ArrayList<>();
        shaderModuleDataList.add(new ShaderProgram.ShaderModuleData("texture.vert", GL20.GL_VERTEX_SHADER));
        shaderModuleDataList.add(new ShaderProgram.ShaderModuleData("texture.frag", GL20.GL_FRAGMENT_SHADER));
        shaderProgram = new ShaderProgram(shaderModuleDataList);
        createUniforms();

        this.textureCache = textureCache;
    }

    public void cleanup() {
        shaderProgram.cleanup();
    }

    public void createUniforms() {
        uniformsMap = new UniformsMap(shaderProgram.getProgramId());
        uniformsMap.createUniform("projectionMatrix");
        uniformsMap.createUniform("modelMatrix");
        uniformsMap.createUniform("textureSampler");
    }

    public void render(Scene scene) {
        shaderProgram.bind();

        uniformsMap.setUniform("projectionMatrix", scene.getProjection().getProjectionMatrix());
        uniformsMap.setUniform("textureSampler", 0);

        Collection<Model> models = scene.getModelMap().values();

        for (Model model : models) {
            List<Entity> entities = model.getEntityList();

            for (Material material : model.getMaterialList()) {
                Texture texture = textureCache.getTexture(material.getTexturePath());
                GL13.glActiveTexture(GL13.GL_TEXTURE0);
                texture.bind();

                for (Mesh mesh : material.getMeshList()) {
                    GL30.glBindVertexArray(mesh.getVaoId());
                    for (Entity entity : entities) {
                        uniformsMap.setUniform("modelMatrix", entity.getModelMatrix());
                        GL11.glDrawElements(GL11.GL_TRIANGLES, mesh.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
                    }
                }
            }
        }

        GL30.glBindVertexArray(0);
        shaderProgram.unbind();
    }
}
