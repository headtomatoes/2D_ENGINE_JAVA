package AnhNe.Input_Manager;

import AnhNe.Firstep.Window;
import org.joml.Vector4f;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class MouseListener {
    private static MouseListener instance;
    private double scrollX, scrollY;
    private double xPos, yPos, lastX, lastY;
    private boolean[] mouseButtonPressed = new boolean[9]; // 0 = left, 1 = right, 2 = middle
    private boolean isDragging; // true if dragging

    // Constructor is private, so no one can create a new MouseListener
    private MouseListener() {
        this.scrollX = 0.0;
        this.scrollY = 0.0;
        this.xPos = 0.0;
        this.yPos = 0.0;
        this.lastX = 0.0;
        this.lastY = 0.0;
    }

    // Return the only instance of MouseListener
    public static MouseListener get() {
        if (MouseListener.instance == null) {
            MouseListener.instance = new MouseListener();
        }
        return MouseListener.instance;
    }

    // POSITIONS AND DRAGGING callback
    public static void mousePosCallBack (long window, double xPos, double yPos) {
        get().lastX = get().xPos;
        get().lastY = get().yPos;
        get().xPos = xPos;
        get().yPos = yPos;
        get().isDragging = get().mouseButtonPressed[0] || get().mouseButtonPressed[1] || get().mouseButtonPressed[2];
    }

    // BUTTONS callback
    public static void mouseButtonCallBack (long window, int button, int action, int modifiers) {   // action: 0 = release, 1 = press, 2 = hold
                                                                                                    // modifiers: combination of various key codes + mouse buttons
        if (action == GLFW_PRESS) {
            if(button < get().mouseButtonPressed.length) {
                get().mouseButtonPressed[button] = true;
            }
        } else if (action == GLFW_RELEASE) {
            if(button < get().mouseButtonPressed.length) {
                get().mouseButtonPressed[button] = false;
                get().isDragging = false;
            }
        }
    }

    // SCROLL callback
    public static void mouseScrollCallBack (long window, double xOffset, double yOffset) {
        get().scrollX = xOffset;
        get().scrollY = yOffset;
    }

    // last frame's position
    public static void endFrame() {
        get().scrollX = 0;
        get().scrollY = 0;
        get().lastX = get().xPos;
        get().lastY = get().yPos;
    }

    public static float getX() {
        return (float) get().xPos;
    }

    public static float getY() {
        return (float) get().yPos;
    }

    public static float getDx() {
        return (float) (get().lastX - get().xPos);
    }

    public static float getDy() {
        return (float) (get().lastY - get().yPos);
    }

    public static float getScrollX() {
        return (float) get().scrollX;
    }

    public static float getScrollY() {
        return (float) get().scrollY;
    }

    public static boolean isDragging() {
        return get().isDragging;
    }

    // Check if a mouse button is pressed
    public static boolean mouseButtonDown(int button) {
        if (button < get().mouseButtonPressed.length) {
            return get().mouseButtonPressed[button];
        } else {
            return false;
        }
    }

    public static float getOrthoX() {
        float currentX = getX();
        currentX = (currentX) / (float) Window.getWidth() * 2.0f - 1.0f;  // NDC = normalized device coordinates

        // last 1 for the w component to keep the integrity of the multiplication with the matrix
        Vector4f tmp = new Vector4f(currentX, 0, 0, 1);
        tmp.mul(Window.getScene().camera().getInverseProjectionMatrix()).mul(Window.getScene().camera().getInverseViewMatrix());
        currentX = tmp.x;
        System.out.println(currentX);
        return currentX;
    }

    public static float getOrthoY() {
        float currentY = Window.getHeight() - getY(); // because the y-axis is flipped in the window
        currentY = (currentY) / (float)Window.getHeight() * 2.0f - 1.0f;  // NDC = normalized device coordinates

        // last 1 for the w component to keep the integrity of the multiplication with the matrix
        Vector4f tmp = new Vector4f(0, currentY, 0, 1);
        tmp.mul(Window.getScene().camera().getInverseProjectionMatrix()).mul(Window.getScene().camera().getInverseViewMatrix());
        currentY = tmp.y;
        System.out.println(currentY);
        return currentY;
    }
}
