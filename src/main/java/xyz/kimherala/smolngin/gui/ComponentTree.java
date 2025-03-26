package xyz.kimherala.smolngin.gui;

// ComponentTree is a bad name for this class.
// Better name would be a node in the tree, but it is what it is.
public class ComponentTree<T extends UIComponent> {
    private ComponentNode<T> root;

    public ComponentTree(T rootComponent) {
        root = new ComponentNode<>(rootComponent);
    }

    public ComponentNode<T> getRoot() {
        return root;
    }

    public ComponentNode<T> findNode(String componentId) {
        return findNodeRecursive(root, componentId);
    }

    public ComponentNode<T> findNodeRecursive(ComponentNode<T> current, String componentId) {
        if (current.getComponent().getId().equals(componentId)) {
            return current;
        }

        for (ComponentNode<T> child : current.getChildren()) {
            ComponentNode<T> found = findNodeRecursive(current, componentId);
            if (found != null) {
                return found;
            }
        }

        return null;
    }

}
