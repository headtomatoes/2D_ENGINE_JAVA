package AnhNe.Components;

import AnhNe.Engine.GameObject;
import AnhNe.Engine.Window;
import AnhNe.Input_Manager.MouseListener;
import AnhNe.Utility.Settings;


import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class MouseControls extends Component {
    private GameObject holdingObject = null;

    public void pickUpObject(GameObject gameObject) {
        this.holdingObject = gameObject;
        Window.getScene().addGameObjectToScene(gameObject);
    }

    public void dropObject() {
        if (holdingObject != null) {
            // Future plan: Add drop logic like snapping to grid or checking valid drop location
            this.holdingObject = null;
        }
    }

    public void update(float deltaTime) {
        if (holdingObject != null) {
            holdingObject.transform.position.x = MouseListener.getOrthoX();
            holdingObject.transform.position.y = MouseListener.getOrthoY();

            // Snapping the object onto the grid by trunked the position to the nearest grid position
            holdingObject.transform.position.x = (int)(holdingObject.transform.position.x / Settings.GRID_WIDTH) * Settings.GRID_WIDTH;
            holdingObject.transform.position.y = (int)(holdingObject.transform.position.y / Settings.GRID_HEIGHT) * Settings.GRID_HEIGHT;

            // Drop object when left mouse button is pressed again
            // Future plan: Add a true drag and drop logic
            if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
                dropObject();
            }
        }
    }
}
