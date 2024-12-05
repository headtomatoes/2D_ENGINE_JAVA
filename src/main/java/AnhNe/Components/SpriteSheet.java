package AnhNe.Components;

import Renderer.Texture;
import org.joml.Vector2f;

import java.util.ArrayList;
import java.util.List;

public class SpriteSheet {

    private Texture texture;
    private List<Sprite> sprites;

    // spriteWidth and spriteHeight are the width and height of each sprite
    // numSprites is the number of sprites in the sprite sheet
    // padding is the blank space between each sprite
    public SpriteSheet(Texture texture, int spriteWidth, int spriteHeight, int numSprites, int padding) {

        this.sprites = new ArrayList<>();
        this.texture = texture;
        int currentX = 0;                                       // x position of the current sprite
        int currentY = texture.getHeight() - spriteHeight;      // y position of the current sprite   start from the top left of the texture(image or sprite sheet)

        // loop through the sprite sheet and get the sprites in the sprite sheet
        // potential bug: if the number of sprites is larger than the number of sprites in the sprite sheet will go out of bounds
        for (int i = 0; i < numSprites; i++) {                  // normalized texture coordinates
            float topY = (currentY + spriteHeight) / (float) texture.getHeight();
            float rightX = (currentX + spriteWidth) / (float) texture.getWidth();
            float leftX = currentX / (float) texture.getWidth();
            float bottomY = currentY / (float) texture.getHeight();

            Vector2f[] texCoords = {
                    new Vector2f(rightX, topY),
                    new Vector2f(rightX, bottomY),
                    new Vector2f(leftX, bottomY),
                    new Vector2f(leftX, topY)
            };

            // add the sprite to the list of sprites
            Sprite sprite = new Sprite();
            sprite.setTexture(this.texture);
            sprite.setTexCoords(texCoords);
            this.sprites.add(sprite);

            // move to the next sprites
            currentX += spriteWidth + padding;
            // if the current x position is larger than the width of the texture, move to the next row of sprites
            if (currentX >= texture.getWidth()) {
                currentX = 0;
                currentY -= spriteHeight + padding;
            }
        }
    }

    public Sprite getSprite(int index) {

        return sprites.get(index);

    }
}
