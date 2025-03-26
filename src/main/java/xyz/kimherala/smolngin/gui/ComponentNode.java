package xyz.kimherala.smolngin.gui;

import java.util.ArrayList;
import java.util.List;

public class ComponentNode<T extends UIComponent> {
    private T component;
    private ComponentType type;
    private List<ComponentNode<T>> children;
    private ComponentNode<T> parent;

    public ComponentNode(T component) {
        this.component = component;
        this.children = new ArrayList<>();
    }

    public ComponentNode<T> getParent() {
        return parent;
    }

    public void setParent(ComponentNode<T> parent) {
        this.parent = parent;
    }

    public List<ComponentNode<T>> getChildren() {
        return new ArrayList<>(children);
    }

    public void addChild(ComponentNode<T> child) {
        children.add(child);
        child.setParent(this);
    }

    public T getComponent() {
        return component;
    }

    public void setComponent(T component) {
        this.component = component;
    }

    public boolean isLeaf() {
        return children.isEmpty();
    }

    public boolean isRoot() {
        return parent == null;
    }

    public void setType(ComponentType type) {
        this.type = type;
    }

    public ComponentType getType() {
        return type;
    }
}
