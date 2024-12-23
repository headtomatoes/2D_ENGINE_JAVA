package AnhNe.Editor;

import AnhNe.Engine.GameObject;
import AnhNe.Input_Manager.MouseListener;
import AnhNe.Renderer.PickingTexture;
import AnhNe.Scene_Manager.Scene;
import imgui.ImGui;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class PropertiesWindow {
    private GameObject activeGameObject = null;
    private PickingTexture pickingTexture;

    private float debounce = 0.2f;

    public PropertiesWindow(PickingTexture pickingTexture) {
        this.pickingTexture = pickingTexture;
    }

    public void update(float deltaTime , Scene currentScene) {
        debounce -= deltaTime;
        if(MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT) && debounce < 0) {
            int x = (int)MouseListener.getScreenX();
            int y = (int)MouseListener.getScreenY();
            int gameObjectID = pickingTexture.readPixel(x, y);
            if(gameObjectID < 0) {
                this.debounce = 0.2f;
                return;
            }
            System.out.println(gameObjectID);
            activeGameObject = currentScene.getGameObject(gameObjectID);
            this.debounce = 0.2f;
        }
    }

    public void imgui() {
        if(activeGameObject != null) {
            ImGui.begin("Properties");
            activeGameObject.imgui();           // the focus/target game object is inspected by imgui
            ImGui.end();
        }
    }

    public GameObject getActiveGameObject() {
        return this.activeGameObject;
    }
}
