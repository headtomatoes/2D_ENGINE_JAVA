package AnhNe.Firstep;


import AnhNe.Components.Sprite;
import AnhNe.Components.SpriteRenderer;
import AnhNe.Components.SpriteSheet;
import AnhNe.Utility.AssetPool;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import imgui.ImGui;
import org.joml.Vector2f;
import org.joml.Vector4f;

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

        obj1 = new GameObject("object1", new Transform(new Vector2f(100,200) , new Vector2f(256,256)), 3);
        SpriteRenderer obj1Sprite = new SpriteRenderer();
        obj1Sprite.setColor(new Vector4f(0, 1, 0, 1));
        obj1.addComponent(obj1Sprite);
        this.addGameObjectToScene(obj1);

        this.activeGameObject = obj1;// set the active game object(cheat)

        GameObject obj2 = new GameObject("object2", new Transform(new Vector2f(600,200) , new Vector2f(256,256)),-2);
        SpriteRenderer obj2SpriteRenderer = new SpriteRenderer();
        Sprite obj2Sprite = new Sprite();
        obj2Sprite.setTexture(AssetPool.getTexture("assets/image/areUGays.png"));
        obj2SpriteRenderer.setSprite(obj2Sprite);
        obj2.addComponent(obj2SpriteRenderer);
        this.addGameObjectToScene(obj2);

        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Component.class, new ComponentsDeserializer())
                .registerTypeAdapter(Component.class, new GameObjectDeserializer())
                .create(); // import Gson library to use this
        String serialized = gson.toJson(obj1);
        gson.fromJson(serialized, SpriteRenderer.class);
        System.out.println(gson.toJson(obj1));
    }

    public void loadResources(){
        // Load the shader
        AssetPool.getShader("assets/shaders/default.glsl");

        // Load the texture
        AssetPool.addSpriteSheet("assets/image/spritesheet.png" ,
                new SpriteSheet(AssetPool.getTexture("assets/image/spritesheet.png"),
                            16, 16, 26, 0));

    }

    @Override
    public void update(float deltaTime) {
        // Update the game objects
        for (GameObject gameObject : this.gameObjects) {
            gameObject.update(deltaTime);
        }

        // Render the game objects
        this.renderer.render();
    }

    @Override
    public void imgui() {
        // Scene imgui
        ImGui.begin("Level Editor Scene");
        ImGui.text("DM CHO 5 KY");
        if (ImGui.button("Exit")) {
            Window.changeScene(1);
        }
        ImGui.end();
    }
}
