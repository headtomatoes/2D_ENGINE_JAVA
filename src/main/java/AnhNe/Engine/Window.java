package AnhNe.Engine;

import AnhNe.Editor.GameViewWindow;
import AnhNe.Input_Manager.KeyListener;
import AnhNe.Input_Manager.MouseListener;
import AnhNe.Renderer.*;
import AnhNe.Scene_Manager.LevelEditorScene;
import AnhNe.Scene_Manager.LevelScene;
import AnhNe.Scene_Manager.Scene;
import AnhNe.Utility.AssetPool;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import java.util.Objects;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

// Mostly come from getting-started-with-lwjgl3 tutorial
public class Window {
    private ImGui_Layer imGuiLayer;
    private int width, height;
    private String title;
    public float r, g, b, a;
    private long glfwWindow;    // address for the window after creating it
                                // just the way in C, we have a pointer to the window
    private FrameBuffer frameBuffer;
    private static Scene currentScene;
    private PickingTexture pickingTexture;
    private GameViewWindow gameViewWindow;

    // Singleton pattern: only one instance of Window can be created
    private static Window window = null;

    // Constructor is private, so no one can create a new Window
    private Window() {
        // HD resolution
        width = 1920;
        height = 1080;
        title = "Firstep"; // changable
        r = 1;
        g = 1;
        b = 1;
        a = 1;
    }

    public static void changeScene(int newScene) {
        switch (newScene) {
            case 0:
                currentScene = new LevelEditorScene();
                break;
            case 1:
                currentScene = new LevelScene();
                break;
            default:
                assert false : "Unknown scene '" + newScene + "'";
                break;
        }
        currentScene.load();
        currentScene.init(); //or update();
        currentScene.start();
    }
    // Return the only instance of Window
    public static Window get() {
        if (Window.window == null) {
            Window.window = new Window();
        }
        return Window.window;
    }

    // Run the window
    public void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");
        System.out.println("Running window with title: " + title + " and resolution: " + width + "x" + height);
        System.out.println("Press ESC to close the window.");

        init();
        loop();
        // Free the memory
        glfwFreeCallbacks(glfwWindow);
        glfwDestroyWindow(glfwWindow);
        // Terminate GLFW and free the error callback
        glfwTerminate();
        Objects.requireNonNull(glfwSetErrorCallback(null)).free();
    }

    private void glfwFreeCallbacks(long glfwWindow) {

    }

    // Initialize the window
    private void init() {
        // set up error callback
        GLFWErrorCallback.createPrint(System.err).set();

        // initialize GLFW
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW.");
        }

        // configure GLFW
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);  // window is not visible FOR waiting for window to be created
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // window is resizable
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE); // maximize window

        // create window
        glfwWindow = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL); // NULL 1st: what monitor, 2nd: share-able
        if (glfwWindow == NULL) {
            throw new IllegalStateException("Failed to create GLFW window.");
        }

        // set up mouse callback
        glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePosCallBack);
        glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallBack);
        glfwSetScrollCallback(glfwWindow, MouseListener::mouseScrollCallBack);
        glfwSetKeyCallback(glfwWindow, KeyListener::keyCallBack);
        glfwSetWindowSizeCallback(glfwWindow, (window, newWidth, newHeight) -> {
            Window.setWidth(newWidth);
            Window.setHeight(newHeight);
        });

//        // set up keyboard callback
//        glfwSetKeyCallback(glfwWindow, KeyListener::keyCallBack);

        // make the OpenGL context current
        glfwMakeContextCurrent(glfwWindow);

        // enable v-sync
        glfwSwapInterval(1); // buffer swapping interval == 1 => as fast as the monitor refresh rate

        // make the window visible
        glfwShowWindow(glfwWindow);

        // this line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();    // break program if not called

        // Enable transparency
        glEnable(GL_BLEND);
        // Set the blend function to use the source alpha and 1 minus source alpha
        // which basically interpolates the alpha value of the source color
        glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);

        // Initialize ImGui
        this.frameBuffer = new FrameBuffer(1920, 1080);
        this.pickingTexture = new PickingTexture(1920, 1080); // mimic the game world
        glViewport(0, 0, 1920, 1080);

        this.imGuiLayer = new ImGui_Layer(glfwWindow ,pickingTexture);
        this.imGuiLayer.initImGui();
        //test
        Window.changeScene(0);
    }

    // Main loop
    private void loop() {

        float timeBegin = (float)glfwGetTime();
        float timeEnd;
        float deltaTime = -1.0f;

        Shader defaultShader = AssetPool.getShader("assets/shaders/default.glsl");
        Shader pickingShader = AssetPool.getShader("assets/shaders/pickingShader.glsl");

        while(!glfwWindowShouldClose(glfwWindow)) {
            // Poll for window events.
            glfwPollEvents();

            // Render pass 1: render the picking texture
            glDisable(GL_BLEND);
            pickingTexture.enableWriting();

            glViewport(0, 0, 1920, 1080);
            glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            Renderer.bindShader(pickingShader);
            currentScene.render();

            pickingTexture.disableWriting();
            glEnable(GL_BLEND);

            // Render pass 2: render the actual game
            DebugDraw.beginFrame();

            this.frameBuffer.bind();
            glClearColor(r, g, b, a);
            glClear(GL_COLOR_BUFFER_BIT); // clear the framebuffer

            if (deltaTime >= 0) {
                DebugDraw.drawLine2D();
                Renderer.bindShader(defaultShader);
                currentScene.update(deltaTime);
                currentScene.render();
            }
            this.frameBuffer.unbind();

            this.imGuiLayer.update(deltaTime, currentScene);
            glfwSwapBuffers(glfwWindow);
            MouseListener.endFrame();
            //System.out.println("FPS: " + 1.0f / deltaTime);
            timeEnd = (float) glfwGetTime();
            deltaTime = timeEnd - timeBegin;
            timeBegin = timeEnd;
        }

        currentScene.saveExit();
    }

    public static Scene getScene() {
        get();
        return currentScene;
    }

    public static int getWidth() {
        return get().width;
    }

    public static int getHeight() {
        return get().height;
    }

    private static void setHeight(int newHeight) {
        get().height = newHeight;
    }

    private static void setWidth(int newWidth) {
        get().width = newWidth;
    }

    public static ImGui_Layer getImguiLayer() {
        return get().imGuiLayer;
    }
    public static FrameBuffer getFrameBuffer() {
        return get().frameBuffer;
    }

    public static float getTargetAspectRatio() {
        return 16.0f / 9.0f;
    }
}
