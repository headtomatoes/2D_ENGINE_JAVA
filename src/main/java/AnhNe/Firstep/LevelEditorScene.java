package AnhNe.Firstep;


import java.awt.event.KeyEvent;

public class LevelEditorScene extends Scene {
    private boolean isChangingScene = false;
    private float timeToChangeScene = 2.0f;

    public LevelEditorScene() {
        System.out.println("LevelEditorScene created!");
    }


    @Override
    public void update(float deltaTime) {

        System.out.println("Frame time per second: " + (1.0/deltaTime) + " FPS");

        if(!isChangingScene && KeyListener.isKeyPressed(KeyEvent.VK_SPACE)) {
            isChangingScene = true;
        }

        if(isChangingScene && timeToChangeScene > 0) {
            timeToChangeScene -= deltaTime;
            Window.get().r -= deltaTime * 5;
            Window.get().g -= deltaTime * 5;
            Window.get().b -= deltaTime * 5;

        } else if (isChangingScene) {
            Window.changeScene(1);
        }
    }
    @Override
    public void render() {
    }
    @Override
    public void imgui() {
    }
    @Override
    public void imguiUpdate() {
    }
    @Override
    public void cleanUp() {
    }
}
