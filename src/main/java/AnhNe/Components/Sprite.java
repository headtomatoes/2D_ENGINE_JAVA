package AnhNe.Components;

import Renderer.Texture;
import org.joml.Vector2f;

public class Sprite {

    private Texture texture;
    private Vector2f[] texCoords;

    public Sprite(Texture texture) {
        this.texture = texture;
        Vector2f[] texCoords = {
//            new Vector2f(0.0f, 0.0f),       // bottom left
//            new Vector2f(1.0f, 0.0f),       // bottom right               // not set true in stbi_flip_vertically_on_write
//            new Vector2f(1.0f, 1.0f),       // top right
//            new Vector2f(0.0f, 1.0f)        // top left

                new Vector2f(1.0f, 1.0f),       // top right
                new Vector2f(1.0f, 0.0f),       // top left
                new Vector2f(0.0f, 0.0f),       // bottom left
                new Vector2f(0.0f, 1.0f)        // bottom right
        };
        this.texCoords = texCoords;
    }

    public Sprite(Texture texture, Vector2f[] texCoords) {
        this.texture = texture;
        this.texCoords = texCoords;
    }

    public Texture getTexture() {
        return this.texture;
    }

    public Vector2f[] getTexCoords() {
        return this.texCoords;
    }
}
