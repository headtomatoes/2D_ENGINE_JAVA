package AnhNe.Scene_Manager;


import AnhNe.Components.*;
import AnhNe.Firstep.*;
import AnhNe.Utility.AssetPool;

import AnhNe.physics2d.PhysicsSystem2D;
import AnhNe.physics2d.rigidbody.Rigidbody2D;
import imgui.ImGui;
import imgui.ImVec2;
import org.joml.Vector2f;

public class LevelEditorScene extends Scene {
    private SpriteSheet sprites;

    PhysicsSystem2D physics = new PhysicsSystem2D(1.0f / 60.0f, new Vector2f(0, -10));
    Transform obj1, obj2;
    Rigidbody2D rb1, rb2;

    GameObject levelEditorStuff = new GameObject("LevelEditorStuff", new Transform(new Vector2f(), new Vector2f()), 0);

    public LevelEditorScene() {

    }

    @Override
    public void init() {
        // Load the resources
        loadResources();
        // block sprite sheet
        sprites = AssetPool.getSpriteSheet("assets/spriteSheet/mario/decorationsAndBlocks.png");
        SpriteSheet gizmos = AssetPool.getSpriteSheet("assets/spriteSheet/mario/gizmos.png");
        // Set the camera
        this.camera = new Camera(new Vector2f(-150, -150));
        // set up the grid lines system
        levelEditorStuff.addComponent(new MouseControls());
        levelEditorStuff.addComponent(new GridlLines());
        levelEditorStuff.addComponent(new EditorCamera(this.camera));
        levelEditorStuff.addComponent(new TranslateGizmo(gizmos.getSprite(1),
                Window.getImguiLayer().getPropertiesWindow()));

        // Add the level editor stuff to the scene
        levelEditorStuff.start();

        //        obj1 = new Transform(new Vector2f(100, 500));
//        obj2 = new Transform(new Vector2f(100, 300));
//
//        rb1 = new Rigidbody2D();
//        rb2 = new Rigidbody2D();
//        rb1.setRawTransform(obj1);
//        rb2.setRawTransform(obj2);
//        rb1.setMass(100.0f);
//        rb2.setMass(200.0f);
//
//        Circle c1 = new Circle();
//        c1.setRadius(10.0f);
//        c1.setRigidbody(rb1);
//        Circle c2 = new Circle();
//        c2.setRadius(20.0f);
//        c2.setRigidbody(rb2);
//        rb1.setCollider(c1);
//        rb2.setCollider(c2);
//
//        physics.addRigidbody(rb1, true);
//        physics.addRigidbody(rb2, false);
    }

    public void loadResources(){
        // Load the shader
        AssetPool.getShader("assets/shaders/default.glsl");

        // Load the texture
        AssetPool.addSpriteSheet("assets/spriteSheet/mario/decorationsAndBlocks.png" ,
                new SpriteSheet(AssetPool.getTexture("assets/spriteSheet/mario/decorationsAndBlocks.png"),
                            16, 16, 81, 0));

        AssetPool.addSpriteSheet("assets/spriteSheet/mario/gizmos.png",
                new SpriteSheet(AssetPool.getTexture("assets/spriteSheet/mario/gizmos.png"),
                        24, 48, 2, 0));

        AssetPool.getTexture("assets/image/areUGays.png");

        for (GameObject gObj : gameObjects) {
            if(gObj.getComponent(SpriteRenderer.class) != null) {
                SpriteRenderer sprite = gObj.getComponent(SpriteRenderer.class);
                if(sprite.getTexture() == null) {
                    sprite.setTexture(AssetPool.getTexture(sprite.getTexture().getFilePath()));
                }
            }
        }
    }

    @Override
    public void update(float deltaTime) {
        // Update the level editor stuff (grid lines, mouse controls) currently
        levelEditorStuff.update(deltaTime);
        this.camera.adjustProjection();
        // Update the game objects
        for (GameObject gameObject : this.gameObjects) {
            gameObject.update(deltaTime);
        }

//        DebugDraw.addCircle(obj1.position, 10.0f, new Vector3f(1, 0, 0));
//        DebugDraw.addCircle(obj2.position, 20.0f, new Vector3f(0.2f, 0.8f, 0.1f));
//        physics.update(dt);
    }

    @Override
    public void render() {
        // Render the game objects
        this.renderer.render();
    }

    @Override
    public void imgui() {
        // Scene imgui
        ImGui.begin("Level Editor Scene");
        levelEditorStuff.imgui();
        ImGui.end();

        ImGui.begin("Test window");
        ImVec2 windowPos = new ImVec2();
        ImGui.getWindowPos(windowPos);

        ImVec2 windowSize = new ImVec2();
        ImGui.getWindowSize(windowSize);

        ImVec2 itemSpacing = new ImVec2();
        ImGui.getStyle().getItemSpacing(itemSpacing);

        float windowX2 = windowPos.x + windowSize.x; // Window right most position(pivot for resizing the window)

        for (int i = 0; i < sprites.size(); i++) {  // looping through the sprites in the sprite sheet to take the icon for each diff block
            Sprite sprite = sprites.getSprite(i);
            float spriteWidth = sprite.getWidth() * 2;
            float spriteHeight = sprite.getHeight() * 2;
            int spriteTextureID = sprite.getTextureID();
            Vector2f[] texCoords = sprite.getTexCoords();

            ImGui.pushID(i);
            // Get the position of the sprite icon
            if(ImGui.imageButton(spriteTextureID, 32, 32, texCoords[2].x, texCoords[0].y, texCoords[0].x, texCoords[2].y)) {
                System.out.println("Button " + i + " : clicked");
                GameObject obj = PreFabricate.generateSpriteObject(sprite, spriteWidth, spriteHeight);
                // Attach the sprite icon to the mouse cursor
                levelEditorStuff.getComponent(MouseControls.class).pickUpObject(obj);
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
