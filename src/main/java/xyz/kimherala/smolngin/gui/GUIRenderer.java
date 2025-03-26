package xyz.kimherala.smolngin.gui;

import xyz.kimherala.smolngin.graphics.TextRenderer;
import xyz.kimherala.smolngin.graphics.Window;

public class GUIRenderer {
    private ComponentTree<?> tree;

    private TextRenderer textRenderer;

    public GUIRenderer(ComponentTree<?> ComponentTree) {
        this.tree = ComponentTree;
        textRenderer = new TextRenderer();
    }

    public void render(Window window) {
        renderRecursive(window, tree.getRoot());
    }

    public ComponentNode<?> renderRecursive(Window window, ComponentNode<?> current) {
        switch (current.getComponent()) {
            case TextUIComponent component:
                renderText(window, component);
                break;
            default:
                break;
        }

        for (ComponentNode<?> child : current.getChildren()) {
            ComponentNode<?> found = renderRecursive(window, child);
            if (found != null) {
                return found;
            }
        }

        return null;
    }

    public void renderText(Window window, TextUIComponent component) {
        textRenderer.render(component.getFont(), component.getText(), component.getFontSize(), component.getX(), component.getY());
    }
}
