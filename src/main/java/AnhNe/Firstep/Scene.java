package AnhNe.Firstep;

import AnhNe.Components.SpriteRenderer;
import Renderer.Renderer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import imgui.ImGui;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public abstract class Scene {

    protected Renderer renderer = new Renderer();
    protected Camera camera;
    private boolean isRunning = false;
    protected List<GameObject> gameObjects = new ArrayList<>();
    protected GameObject activeGameObject = null;
    private boolean levelLoaded = false;

    public Scene() {

    }

    public abstract void update(float deltaTime);

    public void init() {}

    public void start() {
        for (GameObject gameObject : gameObjects) {
            gameObject.start();
            this.renderer.add(gameObject);
        }
        isRunning = true;
    }

    public void addGameObjectToScene(GameObject gameObject) {
        if(!isRunning) {
            gameObjects.add(gameObject);
        } else {
            gameObjects.add(gameObject);
            gameObject.start();
            this.renderer.add(gameObject);
        }
    }

    public Camera camera() {
        return this.camera;
    }

    public void sceneImgui() {
        if(activeGameObject != null) {
            ImGui.begin("Inspector");
            activeGameObject.imgui();           // the focus/target game object is inspected by imgui
            ImGui.end();
        }
        imgui();
    }
    public void imgui() {

    }

    public void saveExit(){
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Component.class, new ComponentsDeserializer())
                .registerTypeAdapter(Component.class, new GameObjectDeserializer())
                .create(); // import Gson library to use this

        try {
            FileWriter writer = new FileWriter("level.txt");
            writer.write(gson.toJson(this.gameObjects));
            writer.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load() {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Component.class, new ComponentsDeserializer())
                .registerTypeAdapter(Component.class, new GameObjectDeserializer())
                .create(); // import Gson library to use this

        String inFile = "";

        try {
            inFile = new String(Files.readAllBytes(Paths.get("level.txt")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(inFile.equals("")) {
            GameObject[] objs = gson.fromJson(inFile, GameObject[].class);
            for (GameObject obj : objs) {
                addGameObjectToScene(obj);
            }
            this.levelLoaded = true;
        }
    }
}
