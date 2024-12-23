package AnhNe.Components;

import AnhNe.Engine.Camera;
import AnhNe.Input_Manager.KeyListener;
import AnhNe.Input_Manager.MouseListener;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.*;

public class EditorCamera extends Component{

    private Camera levelEditorCamera;
    private Vector2f clickOrigin; // the position where the click started
    private float dragDebounce = 0.032f; // after x seconds, the drag is initiated
    private float dragsensitivity = 300.0f; // the speed of the drag
    private float zoomSensitivity = 0.1f; // the speed of the zoom
    private float lerpTime = 0.1f;
    private boolean reset = false;

    public EditorCamera(Camera levelEditorCamera) {
        this.levelEditorCamera = levelEditorCamera;
    }
    @Override
    public void update(float deltaTime) {
        if(MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_MIDDLE) && dragDebounce > 0) {
            this.clickOrigin = new Vector2f(MouseListener.getOrthoX(), MouseListener.getOrthoY());
            dragDebounce -= deltaTime;
            return;
        }else if(MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_MIDDLE) ) {
            Vector2f mousePos = new Vector2f(MouseListener.getOrthoX(), MouseListener.getOrthoY());
            Vector2f delta = new Vector2f(mousePos).sub(this.clickOrigin);
            levelEditorCamera.position.sub(delta.mul(deltaTime).mul(dragsensitivity));
            this.clickOrigin.lerp(mousePos,deltaTime);
        }
        if (dragDebounce <= 0.0f && MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_MIDDLE)) {
            dragDebounce = 0.1f;
        }
        if(MouseListener.getSrollY() != 0.0f) {
            float addValue = (float)  Math.pow(Math.abs(MouseListener.getSrollY()) * zoomSensitivity,
                    1.0f/ levelEditorCamera.getZoom());
            addValue *= -(float) Math.signum(MouseListener.getSrollY());
            levelEditorCamera.addZoom(addValue);
        }
        if(KeyListener.isKeyPressed(GLFW_KEY_SPACE)) {
            reset = true;
        }

        if(reset) {
            levelEditorCamera.position.lerp(new Vector2f(), lerpTime);
            levelEditorCamera.setZoom(this.levelEditorCamera.getZoom() + (1.0f
                    - this.levelEditorCamera.getZoom()) * lerpTime);
            this.lerpTime += 0.3f * deltaTime;
            if(Math.abs(levelEditorCamera.position.x) <= 5.0f && Math.abs(levelEditorCamera.position.y) < 5.0f) {
                this.lerpTime = 0.1f;
                levelEditorCamera.position.set(0,0);
                this.levelEditorCamera.setZoom(1.0f);
                reset = false;
            }
        }
    }
}
