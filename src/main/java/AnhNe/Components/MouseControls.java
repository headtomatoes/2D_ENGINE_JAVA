package AnhNe.Components;

import AnhNe.Firstep.GameObject;
import AnhNe.Firstep.Window;
import AnhNe.Input_Manager.MouseListener;
import org.joml.Vector2f;


import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class MouseControls extends Component {
    private GameObject holdingObject = null;
    private Vector2f offset = new Vector2f(16, 16); // Centralize offset

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
            holdingObject.transform.position.x = MouseListener.getOrthoX() - offset.x;
            holdingObject.transform.position.y = MouseListener.getOrthoY() - offset.y;

            // Drop object when left mouse button is pressed again
            // Future plan: Add a true drag and drop logic
            if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
                dropObject();
            }
        }
    }
}
