package AnhNe.Scene_Manager;

import AnhNe.Components.Component;
import AnhNe.Components.ComponentsDeserializer;
import AnhNe.Engine.Camera;
import AnhNe.Engine.GameObject;
import AnhNe.Engine.GameObjectDeserializer;
import AnhNe.Renderer.Renderer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class Scene {

    protected Renderer renderer = new Renderer();
    protected Camera camera;
    private boolean isRunning = false;
    protected List<GameObject> gameObjects = new ArrayList<>();
    protected boolean levelLoaded = false;

    public Scene() {

    }

    public abstract void update(float deltaTime);

    public abstract void render();

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

    public void imgui() {

    }
    private Gson createGson() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Component.class, new ComponentsDeserializer())
                .registerTypeAdapter(GameObject.class, new GameObjectDeserializer())
                .create();
    }

    public void saveExit(){
        Gson gson = createGson();
        try (FileWriter writer = new FileWriter("levelTest.txt")) {
            List <GameObject> objsToSerialize = new ArrayList<>();
            for (GameObject gameObject : this.gameObjects) {
                if (gameObject.doSerialization()) {
                    objsToSerialize.add(gameObject);
                }
            }
            writer.write(gson.toJson(objsToSerialize));
        } catch (IOException e) {
            e.printStackTrace();
            // Consider adding more robust error handling
            // Perhaps log to a file or show a user-friendly error message
        }
    }

    public void load() {
        try {
            String inFile = new String(Files.readAllBytes(Paths.get("levelTest.txt")));

            if (!inFile.isEmpty()) {
                Gson gson = createGson();

                int maxGameObjectId = -1;
                int maxComponentId = -1;
                GameObject[] objs = gson.fromJson(inFile, GameObject[].class);
                for (int i = 0; i < objs.length; i++) {
                    addGameObjectToScene(objs[i]);

                    for (Component component : objs[i].getAllComponents()) {
                        if (component.getUID() > maxComponentId) {
                            maxComponentId = component.getUID();
                        }
                    }

                    if (objs[i].getUID() > maxGameObjectId) {
                        maxGameObjectId = objs[i].getUID();
                    }
                }

                maxGameObjectId++;
                maxComponentId++;
                GameObject.init(maxGameObjectId);
                Component.init(maxComponentId);
                this.levelLoaded = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            this.levelLoaded = false;
        }
    }

    public GameObject getGameObject(int gameObjectId) {
        Optional<GameObject> result = this.gameObjects.stream()
                .filter(gameObject -> gameObject.getUID() == gameObjectId).findFirst();
        return result.orElse(null);
    }
}
