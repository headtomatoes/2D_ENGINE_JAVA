package AnhNe.Firstep;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

// Mostly come from getting-started-with-lwjgl3 tutorial
public class Window {
    private int width, height;
    private String title;
    private long glfwWindow;    // address for the window after creating it
                                // just the way in C, we have a pointer to the window

    // Singleton pattern: only one instance of Window can be created
    private static Window instance = null;

    // Constructor is private, so no one can create a new Window
    private Window() {
        // HD resolution
        width = 1920;
        height = 1080;
        title = "Firstep"; // changable
    }

    // Return the only instance of Window
    public static Window get() {
        if (instance == null) {
            instance = new Window();
        }
        return instance;
    }

    // Run the window
    public void run() {
        System.out.println("Running window with title: " + title + " and resolution: " + width + "x" + height);
        System.out.println("Press ESC to close the window.");
        init();
        loop();
        // Free the memory
        glfwFreeCallbacks(glfwWindow);
        glfwDestroyWindow(glfwWindow);
        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void glfwFreeCallbacks(long glfwWindow) {

    }

    // Initialize the window
    private void init() {
        System.out.println("Initializing window...");
        // set up error callback
        GLFWErrorCallback.createPrint(System.err).set();

        // initialize GLFW
        if (!org.lwjgl.glfw.GLFW.glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW.");
        }

        // configure GLFW
        org.lwjgl.glfw.GLFW.glfwDefaultWindowHints();
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

        // set up keyboard callback
        glfwSetKeyCallback(glfwWindow, KeyListener::keyCallBack);

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
        GL.createCapabilities(); // break program if not called

    }

    // Main loop
    private void loop() {
        System.out.println("Running main loop...");
        while(!glfwWindowShouldClose(glfwWindow)) {
            // Poll for window events.
            glfwPollEvents();

            glClearColor(1.0f, 1.0f, 1.0f, 0.2f);
            glClear(GL_COLOR_BUFFER_BIT); // clear the framebuffer

            if(KeyListener.isKeyPressed(GLFW_KEY_ESCAPE)) {
                glfwSetWindowShouldClose(glfwWindow, true);
            }
            if(MouseListener.get().isDragging()) {
                glClearColor(0.0f, 1.0f, 0.0f, 0.2f);
                glClear(GL_COLOR_BUFFER_BIT); // clear the framebuffer
            }
            if(KeyListener.isKeyPressed(GLFW_KEY_SPACE)) {
                glClearColor(0.0f, 0.0f, 1.0f, 0.2f);
                glClear(GL_COLOR_BUFFER_BIT); // clear the framebuffer
            }
            glfwSwapBuffers(glfwWindow); // swap the color buffers
        }
    }
}
