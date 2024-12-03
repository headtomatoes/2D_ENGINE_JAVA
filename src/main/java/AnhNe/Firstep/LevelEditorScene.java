package AnhNe.Firstep;


import AnhNe.Components.SpriteRenderer;
import AnhNe.Components.SpriteSheet;
import AnhNe.Utility.AssetPool;

import org.joml.Vector2f;

public class LevelEditorScene extends Scene {
    private GameObject obj1;
    private SpriteSheet sprites;

    public LevelEditorScene() {

    }

    @Override
    public void init() {
        // Load the resources
        loadResources();

        this.camera = new Camera(new Vector2f(-250, 0));

        sprites = AssetPool.getSpriteSheet("assets/image/spritesheet.png");
//        SpriteSheet sprites = AssetPool.getSpriteSheet("assets/image/_Run.png");
        obj1 = new GameObject("object1", new Transform(new Vector2f(200,200) , new Vector2f(256,256)));
        obj1.addComponent(new SpriteRenderer(sprites.getSprite(5)));
        this.addGameObjectToScene(obj1);

        GameObject obj2 = new GameObject("object2", new Transform(new Vector2f(600,200) , new Vector2f(256,256)));
        obj2.addComponent(new SpriteRenderer(sprites.getSprite(10)));
        this.addGameObjectToScene(obj2);

    }

    public void loadResources(){
        // Load the shader
        AssetPool.getShader("assets/shaders/default.glsl");

        // Load the texture
        AssetPool.addSpriteSheet("assets/image/spritesheet.png" ,
                new SpriteSheet(AssetPool.getTexture("assets/image/spritesheet.png"),
                            16, 16, 26, 0));

    }

    // test animation
    private int spriteIndex = 0;
    private float spriteFliptime = 0.2f;
    private float spriteFliptimeLeft = 0.0f;

    @Override
    public void update(float deltaTime) {
        spriteFliptimeLeft -= deltaTime;
        if(spriteFliptimeLeft <= 0) {
            spriteIndex++;
            spriteFliptimeLeft = spriteFliptime;
            if (spriteIndex >= 4) {
                spriteIndex = 0;
            }
            obj1.getComponent(SpriteRenderer.class).setSprite(sprites.getSprite(spriteIndex));
        }
        // Update the game objects
        for (GameObject gameObject : this.gameObjects) {
            gameObject.update(deltaTime);
        }

        // Render the game objects
        this.renderer.render();
    }
}
