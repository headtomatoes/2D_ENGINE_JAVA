package AnhNe.Engine;

import AnhNe.Components.Sprite;
import AnhNe.Components.SpriteRenderer;
import org.joml.Vector2f;

public class PreFabricate {

    // This method is used to generate a sprite object from a sprite that is passed to in
    public static GameObject generateSpriteObject(Sprite sprite , float sizeX, float sizeY) {
        GameObject block = new GameObject("Sprite_Object_Gen"
                , new Transform(new Vector2f(), new Vector2f(sizeX,sizeY))
                ,0);
        SpriteRenderer blockSpriteRenderer = new SpriteRenderer();
        blockSpriteRenderer.setSprite(sprite);
        block.addComponent(blockSpriteRenderer);
        return block;
    }


}
