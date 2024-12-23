package AnhNe.Renderer;

import AnhNe.Components.SpriteRenderer;
import AnhNe.Engine.GameObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Renderer {
    private final int MAX_BATCH_SIZE = 1000;
    private List<RenderBatch> batches;
    private static Shader currentShader;

    public Renderer() {
        this.batches = new ArrayList<>();
    }

    public void add(GameObject gameObject) {
        SpriteRenderer sprites = gameObject.getComponent(SpriteRenderer.class);
        if (sprites != null) {
            add(sprites);
        }
    }

    public void add(SpriteRenderer sprite) {
        boolean added = false;
        for (RenderBatch batch : batches) {
            if (batch.hasRoom() && batch.getZIndex() == sprite.gameObject.getZIndex()) {
                Texture texture = sprite.getTexture();
                if(texture == null || batch.hasTexture(texture) || batch.hasTextureRoom()) {
                    batch.addSprite(sprite);
                    added = true;
                    break;
                }
            }
        }

        if (!added) {
            RenderBatch newBatch = new RenderBatch(MAX_BATCH_SIZE, sprite.gameObject.getZIndex());
            newBatch.start();
            batches.add(newBatch);
            newBatch.addSprite(sprite);
            Collections.sort(batches);
        }
    }

    public static void bindShader(Shader shader) {
        currentShader = shader;
    }

    public static Shader getBoundShader() {
        return currentShader;
    }

    public void render() {
        currentShader.use();
        for (RenderBatch batch : batches) {
            batch.render();
        }
    }
}
