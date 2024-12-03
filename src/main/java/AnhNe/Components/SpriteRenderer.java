package AnhNe.Components;

import AnhNe.Firstep.Component;
import AnhNe.Firstep.Transform;
import Renderer.Texture;
import org.joml.Vector2f;
import org.joml.Vector4f;

public class SpriteRenderer extends Component {
    private Vector4f color;
    private Sprite sprite;
    private boolean isDirty = true;

    private Transform lastTransform;

    public SpriteRenderer() {
        // constructor
    }

    public SpriteRenderer(Vector4f color) {
        this.color = color;
        this.sprite = new Sprite(null);
    }

    public SpriteRenderer(Sprite sprite) {
        this.sprite = sprite;
        this.color = new Vector4f(1, 1, 1, 1);
    }
    @Override
    public void start() {
        if(!this.lastTransform.equals(this.gameObject.transform)) {
            this.gameObject.transform.copy(this.lastTransform);
            isDirty = true;
        }
    }

    @Override
    public void update(float deltaTime) {

    }

    public Vector4f getColor() {
        return this.color;
    }

    public Texture getTexture() {
        return sprite.getTexture();
    }

    public Vector2f[] getTextureCoordinates() {
        return sprite.getTexCoords();
    }

    public void setSprite(Sprite sprite) {
//        if(!this.sprite.equals(sprite)) {
//            this.sprite = sprite;
//            isDirty = true;
//        }
        this.sprite = sprite;
        isDirty = true;
    }

    public void setColor(Vector4f color) {
        if(!this.color.equals(color)) {
            this.color.set(color);
            this.isDirty = true;
        }
    }

    public boolean isDirty() {
        return this.isDirty;
    }

    public void setClean() {
        this.isDirty = false;
    }
}
