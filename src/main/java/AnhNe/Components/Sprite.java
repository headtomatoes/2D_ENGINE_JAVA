package AnhNe.Components;

import Renderer.Texture;
import org.joml.Vector2f;

public class Sprite {
    private float width , height;
    private Texture texture = null;
    private Vector2f[] texCoords = {
                new Vector2f(1.0f, 1.0f),       // top right
                new Vector2f(1.0f, 0.0f),       // top left
                new Vector2f(0.0f, 0.0f),       // bottom left
                new Vector2f(0.0f, 1.0f)        // bottom right
    };

    public Texture getTexture() {
        return this.texture;
    }

    public Vector2f[] getTexCoords() {
        return this.texCoords;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public void setTexCoords(Vector2f[] texCoords) {
        this.texCoords = texCoords;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public int getTextureID() {
        return texture == null ? -1 : texture.getID();
    }
}
