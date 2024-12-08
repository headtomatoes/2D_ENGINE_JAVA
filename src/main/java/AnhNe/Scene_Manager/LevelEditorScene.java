package AnhNe.Scene_Manager;


import AnhNe.Components.MouseControls;
import AnhNe.Components.Sprite;
import AnhNe.Components.SpriteRenderer;
import AnhNe.Components.SpriteSheet;
import AnhNe.Firstep.*;
import AnhNe.Input_Manager.MouseListener;
import AnhNe.Utility.AssetPool;

import Renderer.DebugDraw;
import imgui.ImGui;
import imgui.ImVec2;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class LevelEditorScene extends Scene {
    private GameObject obj1;
    private SpriteSheet sprites;
    SpriteRenderer obj1Sprite;

    MouseControls mouseControls = new MouseControls();

    public LevelEditorScene() {

    }

    @Override
    public void init() {
        // Load the resources
        loadResources();

        this.camera = new Camera(new Vector2f(-150, -150));
        // block sprite sheet
        sprites = AssetPool.getSpriteSheet("assets/spriteSheet/mario/decorationsAndBlocks.png");
        DebugDraw.addLine2D(new Vector2f(600, 100), new Vector2f(100, 300), new Vector3f(1, 0, 0), 1000);
        if(levelLoaded) {
            this.activeGameObject = this.gameObjects.get(0);
            return;
        }

        obj1 = new GameObject("object1", new Transform(new Vector2f(100,200) , new Vector2f(256,256)), 3);
        obj1Sprite = new SpriteRenderer();
        obj1Sprite.setColor(new Vector4f(0, 1, 0, 1));
        obj1.addComponent(obj1Sprite);
        obj1.addComponent(new RigidBody());

        this.addGameObjectToScene(obj1);

        this.activeGameObject = obj1;// set the active game object(cheat)

        GameObject obj2 = new GameObject("object2", new Transform(new Vector2f(600,200) , new Vector2f(256,256)),-2);
        SpriteRenderer obj2SpriteRenderer = new SpriteRenderer();
        Sprite obj2Sprite = new Sprite();
        obj2Sprite.setTexture(AssetPool.getTexture("assets/image/areUGays.png"));
        obj2SpriteRenderer.setSprite(obj2Sprite);
        obj2.addComponent(obj2SpriteRenderer);
        this.addGameObjectToScene(obj2);

    }

    public void loadResources(){
        // Load the shader
        AssetPool.getShader("assets/shaders/default.glsl");

        // Load the texture
        AssetPool.addSpriteSheet("assets/spriteSheet/mario/decorationsAndBlocks.png" ,
                new SpriteSheet(AssetPool.getTexture("assets/spriteSheet/mario/decorationsAndBlocks.png"),
                            16, 16, 81, 0));


        AssetPool.getTexture("assets/image/areUGays.png");
    }


    float time = 0.0f;
    @Override
    public void update(float deltaTime) {

        float y = ((float) Math.sin(time) * 200.0f) + 450.0f;
        float x = ((float) Math.cos(time) * 200.0f) + 450.0f;
        time += 0.05f;
        DebugDraw.addLine2D(new Vector2f(450, 450), new Vector2f(x, y), new Vector3f(0, 0, 1), 600);
        // Update the mouse
        this.mouseControls.update(deltaTime);
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

        ImVec2 windowPos = new ImVec2();
        ImGui.getWindowPos(windowPos);

        ImVec2 windowSize = new ImVec2();
        ImGui.getWindowSize(windowSize);

        ImVec2 itemSpacing = new ImVec2();
        ImGui.getStyle().getItemSpacing(itemSpacing);

        float windowX2 = windowPos.x + windowSize.x; // Window right most position(pivot for resizing the window)

        for (int i = 0; i < sprites.size(); i++) {  // looping through the sprites in the sprite sheet to take the icon for each diff block
            Sprite sprite = sprites.getSprite(i);
            float spriteWidth = sprite.getWidth() * 4;
            float spriteHeight = sprite.getHeight() * 4;
            int spriteTextureID = sprite.getTextureID();
            Vector2f[] texCoords = sprite.getTexCoords();

            ImGui.pushID(i);
            // Get the position of the sprite icon
            if(ImGui.imageButton(spriteTextureID, spriteWidth, spriteHeight, texCoords[0].x, texCoords[0].y, texCoords[2].x, texCoords[2].y)) {
                System.out.println("Button " + i + " : clicked");
                GameObject obj = PreFabricate.generateSpriteObject(sprite, spriteWidth, spriteHeight);
                // Attach the sprite icon to the mouse cursor
                this.mouseControls.pickUpObject(obj);
            }
            ImGui.popID();

            ImVec2 lastButtonPos = new ImVec2();
            ImGui.getItemRectMax(lastButtonPos);
            float lastButtonX2 = lastButtonPos.x;                           // Right most position of the last iterate button
            float nextButtonX2 = lastButtonX2 + itemSpacing.x + spriteWidth;// Next button right most position

            if(i + 1 < sprites.size() && nextButtonX2 < windowX2) {
                ImGui.sameLine();
            }
        }
        ImGui.end();
    }
}