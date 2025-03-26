package xyz.kimherala.smolngin.graphics;

import java.util.ArrayList;
import java.util.List;

public class Model {
    private final String id;
    private List<Entity> entityList;
    private List<Material> materialList;

    public Model(String id, List<Material> materialList) {
        this.id = id;
        entityList = new ArrayList<>();
        this.materialList = materialList;
    }

    public void cleanup() {
        materialList.forEach(Material::cleanup);
    }

    public List<Entity> getEntityList() {
        return entityList;
    }

    public String getId() {
        return id;
    }

    public List<Material> getMaterialList() {
        return materialList;
    }
}
