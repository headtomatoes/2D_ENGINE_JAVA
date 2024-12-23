package AnhNe.Scene_Manager;

import AnhNe.Engine.Window;

public class LevelScene extends Scene {

    public LevelScene() {
        System.out.println("LevelScene created!");
        Window.get().r = 1.0f;
        Window.get().g = 1.0f;
        Window.get().b = 1.0f;
    }

    @Override
    public void update(float deltaTime) {

    }

    @Override
    public void render() {
    }
}
