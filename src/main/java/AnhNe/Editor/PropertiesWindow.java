package AnhNe.Editor;

import AnhNe.Firstep.GameObject;
import AnhNe.Input_Manager.MouseListener;
import AnhNe.Renderer.PickingTexture;
import AnhNe.Scene_Manager.Scene;
import imgui.ImGui;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class PropertiesWindow {
    private GameObject activeGameObject = null;
    private PickingTexture pickingTexture;

    public PropertiesWindow(PickingTexture pickingTexture) {
        this.pickingTexture = pickingTexture;
    }

    public void update(float deltaTime , Scene currentScene) {
        if(MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
            int x = (int)MouseListener.getScreenX();
            int y = (int)MouseListener.getScreenY();
            int gameObjectID = pickingTexture.readPixel(x, y);
            activeGameObject = currentScene.getGameObject(gameObjectID);
        }
    }

    public void imgui() {
        if(activeGameObject != null) {
            ImGui.begin("Inspector");
            activeGameObject.imgui();           // the focus/target game object is inspected by imgui
            ImGui.end();
        }
    }
}
