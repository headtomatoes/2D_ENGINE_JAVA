package AnhNe.Input_Manager;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class KeyListener {
    private static KeyListener instance;
    private boolean keyPressed[] = new boolean[350]; // 350 is the maximum number of keys on a keyboard

    private KeyListener() {
    }

    public static KeyListener get() {
        if (KeyListener.instance == null) {
            KeyListener.instance = new KeyListener();
        }
        return KeyListener.instance;
    }

    public static void keyCallBack(long window, int key, int scancode, int action, int modifiers) {
        if (action == GLFW_RELEASE) {
            get().keyPressed[key] = false;
        } else if (action == GLFW_PRESS) {
            get().keyPressed[key] = true;
        }
    }

    public static boolean isKeyPressed(int keyCode) {
        return get().keyPressed[keyCode];
    }
}
